package org.zero.build.metadata;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Build metadata generator - 生成 Gradle/Maven 元数据。
 */
public final class BuildMetadataGenerator {
	/*
	 * 以 metadata/build-metadata.toml 为输入，
	 * 负责渲染并校验 Gradle / Maven 相关构建文件。
	 */
	private static final String VERSION_CATALOG_DIRECTORY = "gradle";
	private static final String VERSION_CATALOG_BASENAME = "libs.versions";
	private static final String VERSION_CATALOG_PATH = VERSION_CATALOG_DIRECTORY + "/" + VERSION_CATALOG_BASENAME + ".toml";
	private static final String BUILD_REVISION_PROPERTY = "build.revision";
	private static final String REVISION_EXPRESSION = "${revision}";
	private static final String BUILD_PROFILES_PROPERTY = "build.profiles";
	private static final String COMMON_BOM_ALIAS = "org.zero_common-bom";
	private static final String COMMON_BOM_PROPERTY = "org.zero_common-bom.version";
	private static final String GENERATED_DEPENDENCY_PROPERTIES_START = "<!-- generated dependency-properties:start -->";
	private static final String GENERATED_DEPENDENCY_PROPERTIES_END = "<!-- generated dependency-properties:end -->";
	private static final String GENERATED_PLUGIN_PROPERTIES_START = "<!-- generated plugin-properties:start -->";
	private static final String GENERATED_PLUGIN_PROPERTIES_END = "<!-- generated plugin-properties:end -->";
	private static final String GENERATED_DEPENDENCY_MANAGEMENT_START = "<!-- generated dependency-management:start -->";
	private static final String GENERATED_DEPENDENCY_MANAGEMENT_END = "<!-- generated dependency-management:end -->";
	private static final String GENERATED_PLUGIN_MANAGEMENT_START = "<!-- generated plugin-management:start -->";
	private static final String GENERATED_PLUGIN_MANAGEMENT_END = "<!-- generated plugin-management:end -->";
	private static final String GENERATED_HIGHER_PROFILES_START = "<!-- generated higher-build-profiles:start -->";
	private static final String GENERATED_HIGHER_PROFILES_END = "<!-- generated higher-build-profiles:end -->";
	private static final Pattern ROOT_VERSION_PATTERN = Pattern.compile(
		"(?s)(<artifactId>common</artifactId>\\s*<version>)(.*?)(</version>)"
	);
	private static final Pattern COMMON_BOM_VERSION_PATTERN = Pattern.compile(
		"(?s)(<artifactId>common-bom</artifactId>\\s*<version>)(.*?)(</version>)"
	);
	private static final Pattern REVISION_PROPERTY_PATTERN = Pattern.compile("(?s)(<revision>)(.*?)(</revision>)");
	private static final Pattern CHILD_PARENT_VERSION_PATTERN = Pattern.compile(
		"(?s)(<parent>\\s*<groupId>org\\.zero</groupId>\\s*<artifactId>common</artifactId>\\s*<version>)(.*?)(</version>)"
	);
	private static final Pattern CHILD_VERSION_PATTERN = Pattern.compile(
		"(?s)(</parent>\\s*<artifactId>[^<]+</artifactId>\\s*<version>)(.*?)(</version>)"
	);

	private final File rootDir;
	private final File metadataDir;
	private final File buildMetadataFile;
	private final File versionCatalogDirectory;
	private final File versionCatalogFile;
	private final File rootPomFile;
	private final File commonBomPomFile;
	private final File gradlePropertiesFile;
	private final File commonBomGradlePropertiesFile;
	private final BuildMetadataLogger logger;

	public BuildMetadataGenerator(File rootDir) {
		this(rootDir, new BuildMetadataLogger(false));
	}

	public BuildMetadataGenerator(File rootDir, BuildMetadataLogger logger) {
		this.rootDir = rootDir;
		this.metadataDir = new File(rootDir, "metadata");
		this.buildMetadataFile = new File(metadataDir, "build-metadata.toml");
		this.versionCatalogDirectory = new File(rootDir, VERSION_CATALOG_DIRECTORY);
		this.versionCatalogFile = new File(rootDir, VERSION_CATALOG_PATH);
		this.rootPomFile = new File(rootDir, "pom.xml");
		this.commonBomPomFile = new File(rootDir, "common-bom/pom.xml");
		this.gradlePropertiesFile = new File(rootDir, "gradle.properties");
		this.commonBomGradlePropertiesFile = new File(rootDir, "common-bom/gradle.properties");
		this.logger = logger;
	}

	/**
	 * 将元数据同步到 version catalog、POM 与 {@code gradle.properties}，并顺带清理历史产物。
	 */
	public void sync() {
		BuildMetadataModel metadata = loadMetadata();
		deleteLegacyCatalogFiles(metadata);
		writeVersionCatalogFiles(metadata);
		writeIfChanged(rootPomFile, renderRootPom(metadata));
		writeIfChanged(commonBomPomFile, renderCommonBomPom(metadata));
		writeIfChanged(gradlePropertiesFile, renderGradleProperties(gradlePropertiesFile, metadata, true));
		writeIfChanged(
			commonBomGradlePropertiesFile,
			renderGradleProperties(commonBomGradlePropertiesFile, metadata, false)
		);
		updateChildPomVersions();
	}

	/**
	 * 重新渲染目标内容并逐个比对，发现漂移时抛出汇总异常。
	 */
	public void verify() {
		BuildMetadataModel metadata = loadMetadata();
		List<String> mismatches = new ArrayList<String>();
		assertVersionCatalogFiles(metadata, mismatches);
		assertMatches(rootPomFile, renderRootPom(metadata), mismatches);
		assertMatches(commonBomPomFile, renderCommonBomPom(metadata), mismatches);
		assertMatches(gradlePropertiesFile, renderGradleProperties(gradlePropertiesFile, metadata, true), mismatches);
		assertMatches(
			commonBomGradlePropertiesFile,
			renderGradleProperties(commonBomGradlePropertiesFile, metadata, false),
			mismatches
		);
		for (File pomFile : childPomFiles()) {
			assertMatches(pomFile, renderChildPom(pomFile), mismatches);
		}
		if (!mismatches.isEmpty()) {
			throw new IllegalStateException("Build metadata is out of sync:\n - " + join(mismatches));
		}
	}

	private BuildMetadataModel loadMetadata() {
		logger.info("load metadata", buildMetadataFile);
		if (!buildMetadataFile.exists()) {
			throw new IllegalStateException("Missing build metadata file: " + buildMetadataFile);
		}
		return BuildMetadataParser.parse(buildMetadataFile);
	}

	private String renderCatalog(BuildMetadataModel metadata, String profile) {
		StringBuilder builder = new StringBuilder();
		builder.append("[versions]\n");
		for (VersionedEntry entry : metadata.getCatalogVersionEntries()) {
			String resolvedVersion = entry.resolveVersion(metadata, profile);
			if (resolvedVersion != null) {
				builder.append(renderCatalogKey(entry.getAlias()))
					.append(" = ")
					.append(renderCatalogVersionValue(entry, resolvedVersion))
					.append("\n");
			}
		}

		builder.append('\n').append("[libraries]\n");
		renderCatalogLibraries(builder, metadata, profile);

		builder.append('\n').append("[bundles]\n");
		builder.append('\n').append("[plugins]\n");
		renderCatalogPlugins(builder, metadata, profile);
		builder.append('\n');
		return builder.toString();
	}

	private void writeVersionCatalogFiles(BuildMetadataModel metadata) {
		for (String profile : metadata.getSupportedProfiles()) {
			writeIfChanged(catalogFile(profile, metadata), renderCatalog(metadata, profile));
		}
	}

	private void assertVersionCatalogFiles(BuildMetadataModel metadata, List<String> mismatches) {
		for (String profile : metadata.getSupportedProfiles()) {
			assertMatches(catalogFile(profile, metadata), renderCatalog(metadata, profile), mismatches);
		}
	}

	/**
	 * 清理旧 catalog 目录，以及当前 profile 集合之外遗留的高版本 catalog 文件。
	 */
	private void deleteLegacyCatalogFiles(BuildMetadataModel metadata) {
		File legacyCatalogDirectory = new File(rootDir, ".gradle/version-catalogs");
		for (String profile : metadata.getSupportedProfiles()) {
			deleteIfExists(new File(legacyCatalogDirectory, "libs.versions-" + profile + ".toml"));
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
				deleteIfExists(generatedCatalogFile);
			}
		}
	}

	private void renderCatalogLibraries(StringBuilder builder, BuildMetadataModel metadata, String profile) {
		for (LibraryEntry library : metadata.getCatalogLibraries()) {
			String resolvedVersion = library.resolveVersion(metadata, profile);
			if (resolvedVersion != null) {
				appendCatalogLibraryEntry(builder, library.getAlias(), library.getCoordinate(), library.getAlias());
			}
		}
	}

	private void renderCatalogPlugins(StringBuilder builder, BuildMetadataModel metadata, String profile) {
		for (GradlePluginEntry plugin : metadata.getVersionedGradlePlugins()) {
			String resolvedVersion = plugin.resolveVersion(metadata, profile);
			if (resolvedVersion != null) {
				appendCatalogPluginEntry(builder, plugin.getAlias(), plugin.getPluginId(), plugin.getAlias());
			}
		}
	}

	private static void appendCatalogLibraryEntry(StringBuilder builder, String alias, String module, String versionRef) {
		builder.append(renderCatalogKey(alias))
			.append(" = { module = ")
			.append(renderTomlString(module))
			.append(", version.ref = ")
			.append(renderTomlString(toCatalogAlias(versionRef)))
			.append(" }\n");
	}

	private static void appendCatalogPluginEntry(StringBuilder builder, String alias, String pluginId, String versionRef) {
		builder.append(renderCatalogKey(alias))
			.append(" = { id = ")
			.append(renderTomlString(pluginId))
			.append(", version.ref = ")
			.append(renderTomlString(toCatalogAlias(versionRef)))
			.append(" }\n");
	}

	private static List<String> higherProfiles(BuildMetadataModel metadata) {
		return metadata.getSupportedProfiles().subList(1, metadata.getSupportedProfiles().size());
	}

	private File catalogFile(String profile, BuildMetadataModel metadata) {
		return metadata.getBaseProfile().equals(profile)
			? versionCatalogFile
			: new File(versionCatalogDirectory, catalogFileName(profile));
	}

	private static String catalogFileName(String profile) {
		return VERSION_CATALOG_BASENAME + "-" + profile + ".toml";
	}

	private String renderRootPom(BuildMetadataModel metadata) {
		String xml = readUtf8(rootPomFile);
		xml = replaceFirst(xml, ROOT_VERSION_PATTERN, REVISION_EXPRESSION);
		xml = replaceOrInsertPomProperty(xml, "revision", metadata.getRevision(), "\t\t");
		xml = replaceGeneratedDependencyProperties(xml, metadata);
		xml = replaceGeneratedPluginProperties(xml, metadata);
		xml = replaceGeneratedDependencyManagement(xml, metadata);
		xml = replaceGeneratedPluginManagement(xml, metadata);
		xml = replaceGeneratedHigherProfiles(xml, metadata);
		return xml;
	}

	private String renderCommonBomPom(BuildMetadataModel metadata) {
		String xml = replaceFirst(readUtf8(commonBomPomFile), COMMON_BOM_VERSION_PATTERN, REVISION_EXPRESSION);
		return replaceOrInsertPomProperty(xml, "revision", metadata.getRevision(), "        ");
	}

	/**
	 * 覆盖或插入构建属性，同时尽量保持原文件其他内容和相对顺序不变。
	 */
	private String renderGradleProperties(File file, BuildMetadataModel metadata, boolean includeSupportedProfiles) {
		List<String> lines = readLines(file);
		List<String> updated = new ArrayList<String>();
		boolean revisionWritten = false;
		boolean supportedProfilesWritten = false;
		String revision = metadata.getRevision();
		String supportedProfiles = joinCommaSeparated(metadata.getSupportedProfiles());
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
			revisionWritten = true;
		}
		if (includeSupportedProfiles && !supportedProfilesWritten) {
			int insertIndex = Math.min(updated.size(), revisionInsertIndex + 1);
			updated.add(insertIndex, BUILD_PROFILES_PROPERTY + "=" + supportedProfiles);
		}
		return joinLines(updated);
	}

	private void updateChildPomVersions() {
		for (File pomFile : childPomFiles()) {
			writeIfChanged(pomFile, renderChildPom(pomFile));
		}
	}

	private String renderChildPom(File pomFile) {
		String xml = readUtf8(pomFile);
		xml = replaceFirst(xml, CHILD_PARENT_VERSION_PATTERN, REVISION_EXPRESSION);
		xml = replaceFirst(xml, CHILD_VERSION_PATTERN, REVISION_EXPRESSION);
		return xml;
	}

	private List<File> childPomFiles() {
		List<File> pomFiles = new ArrayList<File>();
		collectPomFiles(rootDir, pomFiles);
		java.util.Collections.sort(pomFiles, (left, right) -> left.getAbsolutePath().compareTo(right.getAbsolutePath()));
		return pomFiles;
	}

	private void collectPomFiles(File directory, List<File> pomFiles) {
		if (directory == null || !directory.exists()) {
			return;
		}

		String name = directory.getName();
		if (".git".equals(name) || ".gradle".equals(name) || ".idea".equals(name) || "build".equals(name) || "out".equals(name)) {
			return;
		}

		File[] children = directory.listFiles();
		if (children == null) {
			return;
		}

		for (File child : children) {
			if (child.isDirectory()) {
				collectPomFiles(child, pomFiles);
				continue;
			}
			if (!"pom.xml".equals(child.getName())) {
				continue;
			}
			if (child.equals(rootPomFile) || child.equals(commonBomPomFile)) {
				continue;
			}
			pomFiles.add(child);
		}
	}

	private static String mavenProfileId(String profile) {
		return profile + "-higher";
	}

	private static String replaceGeneratedDependencyManagement(String xml, BuildMetadataModel metadata) {
		List<LibraryEntry> libraries = orderLibraries(metadata.getBaseManagedLibraries());
		return replaceGeneratedBlock(
			xml,
			GENERATED_DEPENDENCY_MANAGEMENT_START,
			GENERATED_DEPENDENCY_MANAGEMENT_END,
			renderGeneratedDependencyManagement(metadata, libraries),
			"\t\t\t"
		);
	}

	private static String replaceGeneratedDependencyProperties(String xml, BuildMetadataModel metadata) {
		Map<String, String> dependencyVersions = sortResolvedVersions(
			metadata,
			joinVersionedEntries(metadata.getValues().values(), metadata.getLibraries().values())
		);
		return replaceGeneratedBlock(
			xml,
			GENERATED_DEPENDENCY_PROPERTIES_START,
			GENERATED_DEPENDENCY_PROPERTIES_END,
			renderGeneratedDependencyProperties(dependencyVersions),
			"\t\t"
		);
	}

	private static String replaceGeneratedPluginProperties(String xml, BuildMetadataModel metadata) {
		Map<String, String> pluginVersions = sortResolvedVersions(metadata, metadata.getMavenPlugins().values());
		return replaceGeneratedBlock(
			xml,
			GENERATED_PLUGIN_PROPERTIES_START,
			GENERATED_PLUGIN_PROPERTIES_END,
			renderGeneratedPluginProperties(pluginVersions),
			"\t\t"
		);
	}

	private static String replaceGeneratedPluginManagement(String xml, BuildMetadataModel metadata) {
		Set<String> manualPluginAliases = findManualPluginAliases(xml);
		List<MavenPluginEntry> plugins = orderPlugins(metadata.getBaseMavenPlugins(), manualPluginAliases);
		return replaceGeneratedBlock(
			xml,
			GENERATED_PLUGIN_MANAGEMENT_START,
			GENERATED_PLUGIN_MANAGEMENT_END,
			renderGeneratedPluginManagement(metadata, plugins),
			"\t\t\t\t"
		);
	}

	private static String replaceGeneratedHigherProfiles(String xml, BuildMetadataModel metadata) {
		return replaceGeneratedBlock(
			xml,
			GENERATED_HIGHER_PROFILES_START,
			GENERATED_HIGHER_PROFILES_END,
			renderGeneratedHigherProfiles(metadata),
			"\t\t"
		);
	}

	/**
	 * 用标记包裹的生成片段替换 root POM 中的对应区域，保留标记本身不变。
	 */
	private static String replaceGeneratedBlock(
		String xml,
		String startMarker,
		String endMarker,
		String content,
		String indentBeforeEnd
	) {
		int startIndex = xml.indexOf(startMarker);
		int endIndex = xml.indexOf(endMarker);
		if (startIndex < 0 || endIndex < 0 || endIndex < startIndex) {
			throw new IllegalStateException("Missing generated block markers '" + startMarker + "' / '" + endMarker + "' in root pom.xml");
		}

		int contentStart = startIndex + startMarker.length();
		StringBuilder builder = new StringBuilder();
		builder.append('\n');
		if (!content.isEmpty()) {
			builder.append(content).append('\n');
		}
		builder.append(indentBeforeEnd);
		return xml.substring(0, contentStart) + builder + xml.substring(endIndex);
	}

	private static String renderGeneratedHigherProfiles(BuildMetadataModel metadata) {
		StringBuilder builder = new StringBuilder();
		List<String> higherProfiles = higherProfiles(metadata);
		for (int index = 0; index < higherProfiles.size(); index++) {
			if (index > 0) {
				builder.append('\n');
			}
			builder.append(renderGeneratedHigherProfile(metadata, higherProfiles.get(index)));
		}
		return builder.toString();
	}

	private static String renderGeneratedDependencyManagement(BuildMetadataModel metadata, List<LibraryEntry> libraries) {
		StringBuilder builder = new StringBuilder();
		for (LibraryEntry library : libraries) {
			builder.append(renderManagedDependency(metadata, library, "\t\t\t"));
		}
		return trimTrailingNewline(builder.toString());
	}

	private static String renderGeneratedPluginManagement(BuildMetadataModel metadata, List<MavenPluginEntry> plugins) {
		StringBuilder builder = new StringBuilder();
		for (MavenPluginEntry plugin : plugins) {
			builder.append(renderManagedPlugin(metadata, plugin, "\t\t\t\t"));
		}
		return trimTrailingNewline(builder.toString());
	}

	private static String renderGeneratedHigherProfile(BuildMetadataModel metadata, String profile) {
		StringBuilder builder = new StringBuilder();
		builder.append("\t\t<profile>\n");
		builder.append("\t\t\t<id>").append(mavenProfileId(profile)).append("</id>\n");
		builder.append("\t\t\t<activation>\n");
		builder.append("\t\t\t\t<jdk>[").append(profileJavaVersion(profile)).append(",)</jdk>\n");
		builder.append("\t\t\t</activation>\n");
		builder.append(renderGeneratedProfileProperties(metadata, profile));
		builder.append(renderGeneratedProfileDependencyManagement(metadata, profile));
		builder.append("\t\t</profile>");
		return builder.toString();
	}

	private static String renderGeneratedProfileProperties(BuildMetadataModel metadata, String profile) {
		Map<String, String> versions = metadata.resolveChangedVersions(metadata.getPomPropertyEntries(), profile);
		boolean hasVisibleVersion = false;
		for (String alias : versions.keySet()) {
			if (!COMMON_BOM_ALIAS.equals(alias)) {
				hasVisibleVersion = true;
				break;
			}
		}
		if (!hasVisibleVersion) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		builder.append("\t\t\t<properties>\n");
		for (Map.Entry<String, String> entry : versions.entrySet()) {
			if (COMMON_BOM_ALIAS.equals(entry.getKey())) {
				continue;
			}
			String propertyName = metadata.propertyName(entry.getKey());
			builder.append("\t\t\t\t<").append(propertyName).append(">")
				.append(entry.getValue())
				.append("</").append(propertyName).append(">\n");
		}
		builder.append("\t\t\t</properties>\n");
		return builder.toString();
	}

	private static String renderGeneratedProfileDependencyManagement(BuildMetadataModel metadata, String profile) {
		List<LibraryEntry> managedDependencies = orderLibraries(metadata.getManagedLibrariesForProfile(profile));
		if (managedDependencies.isEmpty()) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		builder.append("\t\t\t<dependencyManagement>\n");
		builder.append("\t\t\t\t<dependencies>\n");
		for (LibraryEntry library : managedDependencies) {
			builder.append(renderManagedDependency(metadata, library, "\t\t\t\t\t"));
		}
		builder.append("\t\t\t\t</dependencies>\n");
		builder.append("\t\t\t</dependencyManagement>\n");
		return builder.toString();
	}

	private static String renderManagedDependency(BuildMetadataModel metadata, LibraryEntry library, String indent) {
		String propertyName = metadata.propertyName(library.getAlias());
		StringBuilder builder = new StringBuilder();
		builder.append(renderMavenRepositoryComment(indent, library.getCoordinate()));
		builder.append(indent).append("<dependency>\n");
		builder.append(indent).append("\t<groupId>").append(library.getGroupId()).append("</groupId>\n");
		builder.append(indent).append("\t<artifactId>").append(library.getArtifactId()).append("</artifactId>\n");
		builder.append(indent).append("\t<version>${").append(propertyName).append("}</version>\n");
		if ("import".equals(library.getScope())) {
			builder.append(indent).append("\t<type>pom</type>\n");
		}
		if (library.getScope() != null && !library.getScope().isEmpty()) {
			builder.append(indent).append("\t<scope>").append(library.getScope()).append("</scope>\n");
		}
		builder.append(indent).append("</dependency>\n");
		return builder.toString();
	}

	private static String renderManagedPlugin(BuildMetadataModel metadata, MavenPluginEntry plugin, String indent) {
		String propertyName = metadata.propertyName(plugin.getAlias());
		StringBuilder builder = new StringBuilder();
		builder.append(renderMavenRepositoryComment(indent, plugin.getCoordinate()));
		builder.append(indent).append("<plugin>\n");
		builder.append(indent).append("\t<groupId>").append(plugin.getGroupId()).append("</groupId>\n");
		builder.append(indent).append("\t<artifactId>").append(plugin.getArtifactId()).append("</artifactId>\n");
		builder.append(indent).append("\t<version>${").append(propertyName).append("}</version>\n");
		builder.append(indent).append("</plugin>\n");
		return builder.toString();
	}

	private static int profileJavaVersion(String profile) {
		if (!profile.startsWith("java")) {
			throw new IllegalStateException("Unsupported Java profile name: " + profile);
		}
		return Integer.parseInt(profile.substring("java".length()));
	}

	private static String replaceOrInsertPomProperty(String xml, String propertyName, String value, String indent) {
		String propertyTag = "<" + propertyName + ">";
		String propertyEndTag = "</" + propertyName + ">";
		if (xml.contains(propertyTag) && xml.contains(propertyEndTag)) {
			return replaceFirst(xml, REVISION_PROPERTY_PATTERN, value);
		}

		int propertiesIndex = xml.indexOf("<properties>");
		if (propertiesIndex < 0) {
			return xml;
		}
		int insertIndex = propertiesIndex + "<properties>".length();
		String propertyLine = "\n" + indent + propertyTag + value + propertyEndTag;
		return xml.substring(0, insertIndex) + propertyLine + xml.substring(insertIndex);
	}

	private static String renderGeneratedDependencyProperties(Map<String, String> dependencyVersions) {
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, String> entry : dependencyVersions.entrySet()) {
			if (COMMON_BOM_ALIAS.equals(entry.getKey())) {
				appendPropertyElement(builder, COMMON_BOM_PROPERTY, REVISION_EXPRESSION);
				continue;
			}
			appendPropertyElement(builder, entry.getKey() + ".version", entry.getValue());
		}
		return trimTrailingNewline(builder.toString());
	}

	private static String renderGeneratedPluginProperties(Map<String, String> pluginVersions) {
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, String> entry : pluginVersions.entrySet()) {
			appendPropertyElement(builder, entry.getKey() + ".version", entry.getValue());
		}
		return trimTrailingNewline(builder.toString());
	}

	private static Map<String, String> sortResolvedVersions(BuildMetadataModel metadata, Iterable<? extends VersionedEntry> entries) {
		Map<String, String> resolved = metadata.resolveVersions(toVersionedEntryList(entries), metadata.getBaseProfile());
		List<Map.Entry<String, String>> sorted = new ArrayList<Map.Entry<String, String>>(resolved.entrySet());
		Collections.sort(sorted, (left, right) -> metadata.propertyName(left.getKey()).compareTo(metadata.propertyName(right.getKey())));
		Map<String, String> ordered = new LinkedHashMap<String, String>();
		for (Map.Entry<String, String> entry : sorted) {
			ordered.put(entry.getKey(), entry.getValue());
		}
		return ordered;
	}

	private static List<VersionedEntry> joinVersionedEntries(
		Iterable<? extends VersionedEntry> first,
		Iterable<? extends VersionedEntry> second
	) {
		List<VersionedEntry> entries = new ArrayList<VersionedEntry>();
		entries.addAll(toVersionedEntryList(first));
		entries.addAll(toVersionedEntryList(second));
		return entries;
	}

	private static List<VersionedEntry> toVersionedEntryList(Iterable<? extends VersionedEntry> entries) {
		List<VersionedEntry> list = new ArrayList<VersionedEntry>();
		for (VersionedEntry entry : entries) {
			list.add(entry);
		}
		return list;
	}

	private static void appendPropertyElement(StringBuilder builder, String propertyName, String value) {
		builder.append("\t\t<").append(propertyName).append(">")
			.append(value)
			.append("</").append(propertyName).append(">\n");
	}

	private static String renderMavenRepositoryComment(String indent, String coordinate) {
		String[] gav = coordinate.split(":", 2);
		return indent + "<!-- https://mvnrepository.com/artifact/" + gav[0] + "/" + gav[1] + " -->\n";
	}

	private static String replaceFirst(String text, Pattern pattern, String replacement) {
		Matcher matcher = pattern.matcher(text);
		if (!matcher.find()) {
			return text;
		}
		return text.substring(0, matcher.start(2)) + replacement + text.substring(matcher.end(2));
	}

	private static String renderCatalogKey(String alias) {
		return toCatalogAlias(alias);
	}

	private static String renderCatalogVersionValue(VersionedEntry entry, String version) {
		if (entry != null && entry.isStrictVersion()) {
			return "{ strictly = " + renderTomlString(version) + " }";
		}
		return renderTomlString(version);
	}

	private static String preferredGeneratedBlock(String xml, String startMarker, String endMarker) {
		int startIndex = xml.indexOf(startMarker);
		int endIndex = xml.indexOf(endMarker);
		if (startIndex < 0 || endIndex < 0 || endIndex < startIndex) {
			return "";
		}
		int contentStart = startIndex + startMarker.length();
		return xml.substring(contentStart, endIndex);
	}

	/**
	 * 扫描 pluginManagement 中非自动生成的插件，避免覆盖手工维护条目。
	 */
	private static Set<String> findManualPluginAliases(String xml) {
		String pluginManagementBody = firstSectionBody(xml, "pluginManagement", "plugins");
		if (pluginManagementBody == null || pluginManagementBody.isEmpty()) {
			return Collections.emptySet();
		}

		int startIndex = pluginManagementBody.indexOf(GENERATED_PLUGIN_MANAGEMENT_START);
		int endIndex = pluginManagementBody.indexOf(GENERATED_PLUGIN_MANAGEMENT_END);
		StringBuilder manual = new StringBuilder();
		if (startIndex >= 0 && endIndex >= startIndex) {
			manual.append(pluginManagementBody.substring(0, startIndex));
			manual.append(pluginManagementBody.substring(endIndex + GENERATED_PLUGIN_MANAGEMENT_END.length()));
		} else {
			manual.append(pluginManagementBody);
		}

		Set<String> aliases = new LinkedHashSet<String>();
		Matcher matcher = Pattern.compile("(?s)<plugin>(.*?)</plugin>").matcher(manual.toString());
		while (matcher.find()) {
			String body = matcher.group(1);
			String groupId = firstTagValue(body, "groupId");
			String artifactId = firstTagValue(body, "artifactId");
			if (groupId != null && artifactId != null) {
				aliases.add(groupId + "_" + artifactId);
			}
		}
		return aliases;
	}

	private static String firstSectionBody(String xml, String outerTag, String innerTag) {
		Pattern pattern = Pattern.compile(
			"(?s)<" + outerTag + ">\\s*<" + innerTag + ">(.*?)</" + innerTag + ">\\s*</" + outerTag + ">"
		);
		Matcher matcher = pattern.matcher(xml);
		return matcher.find() ? matcher.group(1) : null;
	}

	private static String firstTagValue(String body, String tagName) {
		Matcher matcher = Pattern.compile("(?s)<" + Pattern.quote(tagName) + ">(.*?)</" + Pattern.quote(tagName) + ">").matcher(body);
		return matcher.find() ? matcher.group(1).trim() : null;
	}

	private static List<LibraryEntry> orderLibraries(List<LibraryEntry> libraries) {
		List<LibraryEntry> ordered = new ArrayList<LibraryEntry>(libraries);
		Collections.sort(ordered, (left, right) -> left.getCoordinate().compareTo(right.getCoordinate()));
		return ordered;
	}

	private static List<MavenPluginEntry> orderPlugins(
		List<MavenPluginEntry> plugins,
		Set<String> manualAliases
	) {
		List<MavenPluginEntry> ordered = new ArrayList<MavenPluginEntry>();
		for (MavenPluginEntry plugin : plugins) {
			if (!manualAliases.contains(plugin.getAlias())) {
				ordered.add(plugin);
			}
		}
		Collections.sort(ordered, (left, right) -> left.getCoordinate().compareTo(right.getCoordinate()));
		return ordered;
	}

	private static String trimTrailingNewline(String text) {
		if (text.endsWith("\n")) {
			return text.substring(0, text.length() - 1);
		}
		return text;
	}

	private static String toCatalogAlias(String alias) {
		return alias.replace('.', '-');
	}

	private static String renderTomlString(String value) {
		return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
	}

	private void assertMatches(File file, String expected, List<String> mismatches) {
		logger.info("check", file);
		if (!file.exists()) {
			mismatches.add("Missing file: " + file);
			logger.info("mismatch", file);
			return;
		}
		String actual = readUtf8(file);
		if (!normalize(actual).equals(normalize(expected))) {
			mismatches.add("Out-of-sync file: " + file);
			logger.info("mismatch", file);
		}
	}

	private void deleteIfExists(File file) {
		boolean existed = file.exists();
		if (existed && !file.delete()) {
			throw new IllegalStateException("Failed to delete legacy catalog file: " + file);
		}
		if (existed) {
			logger.info("delete", file);
		}
	}

	/**
	 * 仅在内容发生变化时写回文件，并统一输出为 LF。
	 */
	private void writeIfChanged(File file, String content) {
		String normalizedContent = content.replace("\r\n", "\n");
		if (file.exists()) {
			String current = readUtf8(file).replace("\r\n", "\n");
			if (current.equals(normalizedContent)) {
				logger.info("skip", file);
				return;
			}
		}
		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}
		writeUtf8(file, normalizedContent);
		logger.info("write", file);
	}

	private static String join(List<String> lines) {
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < lines.size(); index++) {
			if (index > 0) {
				builder.append("\n - ");
			}
			builder.append(lines.get(index));
		}
		return builder.toString();
	}

	private static String joinCommaSeparated(List<String> values) {
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < values.size(); index++) {
			if (index > 0) {
				builder.append(',');
			}
			builder.append(values.get(index));
		}
		return builder.toString();
	}

	private static String joinLines(List<String> lines) {
		StringBuilder builder = new StringBuilder();
		for (int index = 0; index < lines.size(); index++) {
			if (index > 0) {
				builder.append('\n');
			}
			builder.append(lines.get(index));
		}
		builder.append('\n');
		return builder.toString();
	}

	private static List<String> readLines(File file) {
		try {
			return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
		} catch (IOException ex) {
			throw new IllegalStateException("Failed to read file: " + file, ex);
		}
	}

	private static String readUtf8(File file) {
		try {
			return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
		} catch (IOException ex) {
			throw new IllegalStateException("Failed to read file: " + file, ex);
		}
	}

	private static void writeUtf8(File file, String content) {
		try {
			Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
		} catch (IOException ex) {
			throw new IllegalStateException("Failed to write file: " + file, ex);
		}
	}

	private static String normalize(String text) {
		return text.replace("\r\n", "\n").trim();
	}
}
