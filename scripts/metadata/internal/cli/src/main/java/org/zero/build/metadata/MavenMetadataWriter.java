package org.zero.build.metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Maven metadata writer - 仅负责 Maven 侧生成物的同步与校验。
 */
final class MavenMetadataWriter {
	private static final String REVISION_EXPRESSION = "${revision}";
	private static final String POM_FILE_NAME = "pom.xml";
	private static final String PROPERTIES_TAG = "<properties>";
	private static final String REVISION_PROPERTY_TAG = "<revision>";
	private static final String REVISION_PROPERTY_END_TAG = "</revision>";
	private static final String COMMON_BOM_ALIAS = "org.zero_common-bom";
	private static final String COMMON_BOM_PROPERTY = "org.zero_common-bom.version";
	private static final Set<String> IGNORED_POM_SCAN_DIRECTORIES = Collections.unmodifiableSet(
		new LinkedHashSet<String>(Arrays.asList(".git", ".gradle", ".idea", "build", "out"))
	);
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
	private final File rootPomFile;
	private final File commonBomPomFile;
	private final BuildMetadataLogger logger;

	MavenMetadataWriter(File rootDir, BuildMetadataLogger logger) {
		this.rootDir = rootDir;
		this.rootPomFile = new File(rootDir, "pom.xml");
		this.commonBomPomFile = new File(rootDir, "common-bom/pom.xml");
		this.logger = logger;
	}

	void sync(BuildMetadataModel metadata) {
		MetadataFileSupport.writeIfChanged(rootPomFile, renderRootPom(metadata), logger);
		MetadataFileSupport.writeIfChanged(commonBomPomFile, renderCommonBomPom(metadata), logger);
		updateChildPomVersions();
	}

	void verify(BuildMetadataModel metadata, List<String> mismatches) {
		MetadataFileSupport.assertMatches(rootPomFile, renderRootPom(metadata), mismatches, logger);
		MetadataFileSupport.assertMatches(commonBomPomFile, renderCommonBomPom(metadata), mismatches, logger);
		for (File pomFile : childPomFiles()) {
			MetadataFileSupport.assertMatches(pomFile, renderChildPom(pomFile), mismatches, logger);
		}
	}

	private String renderRootPom(BuildMetadataModel metadata) {
		String xml = MetadataFileSupport.readUtf8(rootPomFile);
		xml = MetadataFileSupport.replaceFirst(xml, ROOT_VERSION_PATTERN, REVISION_EXPRESSION);
		xml = replaceOrInsertRevisionProperty(xml, metadata.getRevision(), "\t\t");
		xml = replaceGeneratedDependencyProperties(xml, metadata);
		xml = replaceGeneratedPluginProperties(xml, metadata);
		xml = replaceGeneratedDependencyManagement(xml, metadata);
		xml = replaceGeneratedPluginManagement(xml, metadata);
		xml = replaceGeneratedHigherProfiles(xml, metadata);
		return xml;
	}

	private String renderCommonBomPom(BuildMetadataModel metadata) {
		String xml = MetadataFileSupport.replaceFirst(
			MetadataFileSupport.readUtf8(commonBomPomFile),
			COMMON_BOM_VERSION_PATTERN,
			REVISION_EXPRESSION
		);
		return replaceOrInsertRevisionProperty(xml, metadata.getRevision(), "        ");
	}

	private void updateChildPomVersions() {
		for (File pomFile : childPomFiles()) {
			MetadataFileSupport.writeIfChanged(pomFile, renderChildPom(pomFile), logger);
		}
	}

	private String renderChildPom(File pomFile) {
		String xml = MetadataFileSupport.readUtf8(pomFile);
		xml = MetadataFileSupport.replaceFirst(xml, CHILD_PARENT_VERSION_PATTERN, REVISION_EXPRESSION);
		xml = MetadataFileSupport.replaceFirst(xml, CHILD_VERSION_PATTERN, REVISION_EXPRESSION);
		return xml;
	}

	private List<File> childPomFiles() {
		List<File> pomFiles = new ArrayList<File>();
		collectPomFiles(rootDir, pomFiles);
		Collections.sort(pomFiles, (left, right) -> left.getAbsolutePath().compareTo(right.getAbsolutePath()));
		return pomFiles;
	}

	private void collectPomFiles(File directory, List<File> pomFiles) {
		if (directory == null || !directory.exists()) {
			return;
		}

		String name = directory.getName();
		if (IGNORED_POM_SCAN_DIRECTORIES.contains(name)) {
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
			if (!POM_FILE_NAME.equals(child.getName())) {
				continue;
			}
			if (child.equals(rootPomFile) || child.equals(commonBomPomFile)) {
				continue;
			}
			pomFiles.add(child);
		}
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
		Map<String, String> dependencyVersions = sortResolvedVersions(metadata, metadata.getLibraries().values());
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
		List<MavenPluginEntry> plugins = orderPlugins(metadata.getBaseMavenPlugins());
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

	private static String renderGeneratedDependencyManagement(BuildMetadataModel metadata, List<LibraryEntry> libraries) {
		StringBuilder builder = new StringBuilder();
		for (LibraryEntry library : libraries) {
			builder.append(renderManagedDependency(metadata, library, "\t\t\t"));
		}
		return MetadataFileSupport.trimTrailingNewline(builder.toString());
	}

	private static String renderGeneratedPluginManagement(BuildMetadataModel metadata, List<MavenPluginEntry> plugins) {
		StringBuilder builder = new StringBuilder();
		for (MavenPluginEntry plugin : plugins) {
			builder.append(renderManagedPlugin(metadata, plugin, "\t\t\t\t"));
		}
		return MetadataFileSupport.trimTrailingNewline(builder.toString());
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

	private static String renderGeneratedHigherProfile(BuildMetadataModel metadata, String profile) {
		StringBuilder builder = new StringBuilder();
		builder.append("\t\t<profile>\n");
		builder.append("\t\t\t<id>").append(profile).append("-higher</id>\n");
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
		builder.append(MetadataFileSupport.renderMavenRepositoryComment(indent, library.getCoordinate()));
		builder.append(indent).append("<dependency>\n");
		builder.append(indent).append("\t<groupId>").append(library.getGroupId()).append("</groupId>\n");
		builder.append(indent).append("\t<artifactId>").append(library.getArtifactId()).append("</artifactId>\n");
		builder.append(indent).append("\t<version>${").append(propertyName).append("}</version>\n");
		if (LibraryEntry.MAVEN_SCOPE_IMPORT.equals(library.getScope())) {
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
		builder.append(MetadataFileSupport.renderMavenRepositoryComment(indent, plugin.getCoordinate()));
		builder.append(indent).append("<plugin>\n");
		builder.append(indent).append("\t<groupId>").append(plugin.getGroupId()).append("</groupId>\n");
		builder.append(indent).append("\t<artifactId>").append(plugin.getArtifactId()).append("</artifactId>\n");
		builder.append(indent).append("\t<version>${").append(propertyName).append("}</version>\n");
		builder.append(indent).append("</plugin>\n");
		return builder.toString();
	}

	private static String replaceOrInsertRevisionProperty(String xml, String revision, String indent) {
		if (xml.contains(REVISION_PROPERTY_TAG) && xml.contains(REVISION_PROPERTY_END_TAG)) {
			return MetadataFileSupport.replaceFirst(xml, REVISION_PROPERTY_PATTERN, revision);
		}

		int propertiesIndex = xml.indexOf(PROPERTIES_TAG);
		if (propertiesIndex < 0) {
			return xml;
		}
		int insertIndex = propertiesIndex + PROPERTIES_TAG.length();
		String propertyLine = "\n" + indent + REVISION_PROPERTY_TAG + revision + REVISION_PROPERTY_END_TAG;
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
		return MetadataFileSupport.trimTrailingNewline(builder.toString());
	}

	private static String renderGeneratedPluginProperties(Map<String, String> pluginVersions) {
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, String> entry : pluginVersions.entrySet()) {
			appendPropertyElement(builder, entry.getKey() + ".version", entry.getValue());
		}
		return MetadataFileSupport.trimTrailingNewline(builder.toString());
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

	private static List<String> higherProfiles(BuildMetadataModel metadata) {
		return metadata.getSupportedProfiles().subList(1, metadata.getSupportedProfiles().size());
	}

	private static int profileJavaVersion(String profile) {
		if (!profile.startsWith("java")) {
			throw new IllegalStateException("Unsupported Java profile name: " + profile);
		}
		return Integer.parseInt(profile.substring("java".length()));
	}

	private static List<LibraryEntry> orderLibraries(List<LibraryEntry> libraries) {
		List<LibraryEntry> ordered = new ArrayList<LibraryEntry>(libraries);
		Collections.sort(ordered, (left, right) -> left.getCoordinate().compareTo(right.getCoordinate()));
		return ordered;
	}

	private static List<MavenPluginEntry> orderPlugins(List<MavenPluginEntry> plugins) {
		List<MavenPluginEntry> ordered = new ArrayList<MavenPluginEntry>(plugins);
		Collections.sort(ordered, (left, right) -> left.getCoordinate().compareTo(right.getCoordinate()));
		return ordered;
	}
}
