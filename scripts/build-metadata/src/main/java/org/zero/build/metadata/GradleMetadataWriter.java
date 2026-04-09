package org.zero.build.metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Gradle metadata writer - 仅负责 Gradle 侧产物的同步与校验。
 */
final class GradleMetadataWriter {
	private static final String VERSION_CATALOG_DIRECTORY = "gradle";
	private static final String VERSION_CATALOG_BASENAME = "libs.versions";
	private static final String VERSION_CATALOG_PATH = VERSION_CATALOG_DIRECTORY + "/" + VERSION_CATALOG_BASENAME + ".toml";
	private static final String BUILD_REVISION_PROPERTY = "build.revision";
	private static final String BUILD_PROFILES_PROPERTY = "build.profiles";
	private static final String MANAGED_ENFORCED_PLATFORMS_BUNDLE = "managed-enforced-platforms";
	private static final String MANAGED_PLATFORMS_BUNDLE = "managed-platforms";
	private static final String MANAGED_CONSTRAINTS_BUNDLE = "managed-constraints";

	private final File rootDir;
	private final File versionCatalogDirectory;
	private final File versionCatalogFile;
	private final File gradlePropertiesFile;
	private final File commonBomGradlePropertiesFile;
	private final File legacyPlatformMetadataFile;
	private final BuildMetadataLogger logger;

	GradleMetadataWriter(File rootDir, BuildMetadataLogger logger) {
		this.rootDir = rootDir;
		this.versionCatalogDirectory = new File(rootDir, VERSION_CATALOG_DIRECTORY);
		this.versionCatalogFile = new File(rootDir, VERSION_CATALOG_PATH);
		this.gradlePropertiesFile = new File(rootDir, "gradle.properties");
		this.commonBomGradlePropertiesFile = new File(rootDir, "common-bom/gradle.properties");
		this.legacyPlatformMetadataFile = new File(rootDir, "gradle/platform-metadata.json");
		this.logger = logger;
	}

	void sync(BuildMetadataModel metadata) {
		deleteLegacyCatalogFiles(metadata);
		writeVersionCatalogFiles(metadata);
		MetadataFileSupport.writeIfChanged(
			gradlePropertiesFile,
			renderGradleProperties(gradlePropertiesFile, metadata, true),
			logger
		);
		MetadataFileSupport.writeIfChanged(
			commonBomGradlePropertiesFile,
			renderGradleProperties(commonBomGradlePropertiesFile, metadata, false),
			logger
		);
		MetadataFileSupport.deleteIfExists(legacyPlatformMetadataFile, logger);
	}

	void verify(BuildMetadataModel metadata, List<String> mismatches) {
		assertVersionCatalogFiles(metadata, mismatches);
		MetadataFileSupport.assertMatches(
			gradlePropertiesFile,
			renderGradleProperties(gradlePropertiesFile, metadata, true),
			mismatches,
			logger
		);
		MetadataFileSupport.assertMatches(
			commonBomGradlePropertiesFile,
			renderGradleProperties(commonBomGradlePropertiesFile, metadata, false),
			mismatches,
			logger
		);
		if (legacyPlatformMetadataFile.exists()) {
			mismatches.add("Unexpected legacy file: " + legacyPlatformMetadataFile);
			logger.info("mismatch", legacyPlatformMetadataFile);
		}
	}

	private void writeVersionCatalogFiles(BuildMetadataModel metadata) {
		for (String profile : metadata.getSupportedProfiles()) {
			MetadataFileSupport.writeIfChanged(catalogFile(profile, metadata), renderCatalog(metadata, profile), logger);
		}
	}

	private void assertVersionCatalogFiles(BuildMetadataModel metadata, List<String> mismatches) {
		for (String profile : metadata.getSupportedProfiles()) {
			MetadataFileSupport.assertMatches(
				catalogFile(profile, metadata),
				renderCatalog(metadata, profile),
				mismatches,
				logger
			);
		}
	}

	private String renderCatalog(BuildMetadataModel metadata, String profile) {
		StringBuilder builder = new StringBuilder();
		builder.append("[versions]\n");
		for (VersionedEntry entry : metadata.getCatalogVersionEntries()) {
			String resolvedVersion = entry.resolveVersion(metadata, profile);
			if (resolvedVersion != null) {
				builder.append(MetadataFileSupport.catalogAlias(entry.getAlias()))
					.append(" = ")
					.append(renderCatalogVersionValue(entry, resolvedVersion))
					.append("\n");
			}
		}

		builder.append('\n').append("[libraries]\n");
		renderCatalogLibraries(builder, metadata, profile);
		builder.append('\n').append("[bundles]\n");
		renderCatalogBundles(builder, metadata, profile);
		builder.append('\n').append("[plugins]\n");
		renderCatalogPlugins(builder, metadata, profile);
		builder.append('\n');
		return builder.toString();
	}

	private void renderCatalogLibraries(StringBuilder builder, BuildMetadataModel metadata, String profile) {
		for (LibraryEntry library : metadata.getCatalogLibraries()) {
			if (library.resolveVersion(metadata, profile) == null) {
				continue;
			}
			builder.append(MetadataFileSupport.catalogAlias(library.getAlias()))
				.append(" = { module = ")
				.append(MetadataFileSupport.renderTomlString(library.getCoordinate()))
				.append(", version.ref = ")
				.append(MetadataFileSupport.renderTomlString(MetadataFileSupport.catalogAlias(library.getAlias())))
				.append(" }\n");
		}
	}

	private void renderCatalogBundles(StringBuilder builder, BuildMetadataModel metadata, String profile) {
		renderCatalogBundle(builder, MANAGED_ENFORCED_PLATFORMS_BUNDLE, resolvedEnforcedPlatformAliases(metadata, profile));
		renderCatalogBundle(builder, MANAGED_PLATFORMS_BUNDLE, resolvedPlatformAliases(metadata, profile));
		renderCatalogBundle(builder, MANAGED_CONSTRAINTS_BUNDLE, resolvedConstraintAliases(metadata, profile));
	}

	private void renderCatalogBundle(StringBuilder builder, String alias, List<String> members) {
		if (members.isEmpty()) {
			return;
		}
		builder.append(alias)
			.append(" = [");
		for (int index = 0; index < members.size(); index++) {
			if (index > 0) {
				builder.append(", ");
			}
			builder.append(MetadataFileSupport.renderTomlString(members.get(index)));
		}
		builder.append("]\n");
	}

	private void renderCatalogPlugins(StringBuilder builder, BuildMetadataModel metadata, String profile) {
		for (GradlePluginEntry plugin : metadata.getVersionedGradlePlugins()) {
			if (plugin.resolveVersion(metadata, profile) == null) {
				continue;
			}
			builder.append(MetadataFileSupport.catalogAlias(plugin.getAlias()))
				.append(" = { id = ")
				.append(MetadataFileSupport.renderTomlString(plugin.getPluginId()))
				.append(", version.ref = ")
				.append(MetadataFileSupport.renderTomlString(MetadataFileSupport.catalogAlias(plugin.getAlias())))
				.append(" }\n");
		}
	}

	private String renderGradleProperties(File file, BuildMetadataModel metadata, boolean includeSupportedProfiles) {
		List<String> lines = MetadataFileSupport.readLines(file);
		List<String> updated = new ArrayList<String>();
		boolean revisionWritten = false;
		boolean supportedProfilesWritten = false;
		String revision = metadata.getRevision();
		String supportedProfiles = MetadataFileSupport.joinCommaSeparated(metadata.getSupportedProfiles());
		int revisionInsertIndex = Math.min(4, lines.size());
		for (String line : lines) {
			if (line.startsWith(BUILD_REVISION_PROPERTY + "=")) {
				if (!revisionWritten) {
					updated.add(BUILD_REVISION_PROPERTY + "=" + revision);
					revisionInsertIndex = updated.size() - 1;
					revisionWritten = true;
				}
				continue;
			}
			if (line.startsWith(BUILD_PROFILES_PROPERTY + "=")) {
				if (includeSupportedProfiles && !supportedProfilesWritten) {
					updated.add(BUILD_PROFILES_PROPERTY + "=" + supportedProfiles);
					supportedProfilesWritten = true;
				}
				continue;
			}
			updated.add(line);
		}
		if (!revisionWritten) {
			revisionInsertIndex = Math.min(4, updated.size());
			updated.add(revisionInsertIndex, BUILD_REVISION_PROPERTY + "=" + revision);
		}
		if (includeSupportedProfiles && !supportedProfilesWritten) {
			int insertIndex = Math.min(updated.size(), revisionInsertIndex + 1);
			updated.add(insertIndex, BUILD_PROFILES_PROPERTY + "=" + supportedProfiles);
		}
		return MetadataFileSupport.joinLines(updated);
	}

	private void deleteLegacyCatalogFiles(BuildMetadataModel metadata) {
		File legacyCatalogDirectory = new File(rootDir, ".gradle/version-catalogs");
		for (String profile : metadata.getSupportedProfiles()) {
			MetadataFileSupport.deleteIfExists(new File(legacyCatalogDirectory, "libs.versions-" + profile + ".toml"), logger);
		}
		if (legacyCatalogDirectory.isDirectory()) {
			File[] children = legacyCatalogDirectory.listFiles();
			if (children != null && children.length == 0 && legacyCatalogDirectory.delete()) {
				logger.info("delete", legacyCatalogDirectory);
			}
		}

		Set<String> expectedCatalogFiles = new LinkedHashSet<String>();
		for (String profile : higherProfiles(metadata)) {
			expectedCatalogFiles.add(catalogFileName(profile));
		}
		File[] generatedCatalogFiles = versionCatalogDirectory.listFiles();
		if (generatedCatalogFiles == null) {
			return;
		}
		for (File generatedCatalogFile : generatedCatalogFiles) {
			String fileName = generatedCatalogFile.getName();
			if (!fileName.matches("^libs\\.versions-java\\d+\\.toml$")) {
				continue;
			}
			if (!expectedCatalogFiles.contains(fileName)) {
				MetadataFileSupport.deleteIfExists(generatedCatalogFile, logger);
			}
		}
	}

	private File catalogFile(String profile, BuildMetadataModel metadata) {
		return metadata.getBaseProfile().equals(profile)
			? versionCatalogFile
			: new File(versionCatalogDirectory, catalogFileName(profile));
	}

	private static List<String> higherProfiles(BuildMetadataModel metadata) {
		return metadata.getSupportedProfiles().subList(1, metadata.getSupportedProfiles().size());
	}

	private static String catalogFileName(String profile) {
		return VERSION_CATALOG_BASENAME + "-" + profile + ".toml";
	}

	private static String renderCatalogVersionValue(VersionedEntry entry, String version) {
		if (entry != null && entry.isStrictVersion()) {
			return "{ strictly = " + MetadataFileSupport.renderTomlString(version) + " }";
		}
		return MetadataFileSupport.renderTomlString(version);
	}

	private List<String> resolvedPlatformAliases(BuildMetadataModel metadata, String profile) {
		List<String> aliases = new ArrayList<String>();
		for (LibraryEntry library : metadata.getLibraries().values()) {
			if (library.resolveVersion(metadata, profile) == null) {
				continue;
			}
			if (!LibraryEntry.MAVEN_SCOPE_IMPORT.equals(library.getScope())) {
				continue;
			}
			if (shouldSkipGradleManagedLibrary(library)) {
				continue;
			}
			if (isEnforcedPlatformAlias(library)) {
				continue;
			}
			aliases.add(MetadataFileSupport.catalogAlias(library.getAlias()));
		}
		Collections.sort(aliases);
		return aliases;
	}

	private List<String> resolvedEnforcedPlatformAliases(BuildMetadataModel metadata, String profile) {
		List<String> aliases = new ArrayList<String>();
		for (LibraryEntry library : metadata.getLibraries().values()) {
			if (library.resolveVersion(metadata, profile) == null) {
				continue;
			}
			if (!LibraryEntry.MAVEN_SCOPE_IMPORT.equals(library.getScope())) {
				continue;
			}
			if (shouldSkipGradleManagedLibrary(library)) {
				continue;
			}
			if (!isEnforcedPlatformAlias(library)) {
				continue;
			}
			aliases.add(MetadataFileSupport.catalogAlias(library.getAlias()));
		}
		Collections.sort(aliases);
		return aliases;
	}

	private List<String> resolvedConstraintAliases(BuildMetadataModel metadata, String profile) {
		List<String> aliases = new ArrayList<String>();
		for (LibraryEntry library : metadata.getLibraries().values()) {
			if (library.resolveVersion(metadata, profile) == null) {
				continue;
			}
			if (LibraryEntry.MAVEN_SCOPE_IMPORT.equals(library.getScope())) {
				continue;
			}
			aliases.add(MetadataFileSupport.catalogAlias(library.getAlias()));
		}
		Collections.sort(aliases);
		return aliases;
	}

	private boolean isEnforcedPlatformAlias(LibraryEntry library) {
		return "org.springframework_spring-framework-bom".equals(library.getAlias())
			|| "org.springframework.boot_spring-boot-dependencies".equals(library.getAlias());
	}

	private boolean shouldSkipGradleManagedLibrary(LibraryEntry library) {
		return "org.zero_common-bom".equals(library.getAlias());
	}
}
