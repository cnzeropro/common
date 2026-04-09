package org.zero.build.metadata;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuildMetadataModelTest {
	@Test
	void resolveChangedVersionsShouldMatchCumulativeHigherProfiles(@TempDir Path tempDir) throws IOException {
		Path metadataFile = tempDir.resolve("build-metadata.toml");
		Files.write(
			metadataFile,
			Arrays.asList(
				"[metadata]",
				"revision = \"1.0.0\"",
				"profiles = [\"java8\", \"java11\", \"java17\", \"java21\"]",
				"",
				"[libraries]",
				"\"com.example_base-only\" = { java8 = \"8.0.0\" }",
				"\"com.example_java11-only\" = { java11 = \"11.0.0\" }",
				"\"com.example_java17-only\" = { java17 = \"17.0.0\" }",
				"\"com.example_java21-only\" = { java21 = \"21.0.0\" }",
				"\"com.example_shared-lib\" = { java8 = \"8.1.0\", java17 = \"17.1.0\", java21 = \"21.1.0\" }",
				"",
				"[plugins.maven]",
				"\"org.example_demo-plugin\" = { java8 = \"1.0.0\", java17 = \"1.7.0\" }"
			),
			StandardCharsets.UTF_8
		);

		BuildMetadataModel metadata = BuildMetadataParser.parse(metadataFile.toFile());

		assertResolvedViaHigherProfiles(metadata, "java17");
		assertResolvedViaHigherProfiles(metadata, "java21");
	}

	@Test
	void parseShouldValidateSupportedMavenScopes(@TempDir Path tempDir) throws IOException {
		Path allowedMetadataFile = tempDir.resolve("build-metadata-allowed.toml");
		Files.write(
			allowedMetadataFile,
			Arrays.asList(
				"[metadata]",
				"revision = \"1.0.0\"",
				"profiles = [\"java8\", \"java17\"]",
				"",
				"[libraries]",
				"\"org.example_demo-bom\" = { java8 = \"1.0.0\", scope = \"import\" }"
			),
			StandardCharsets.UTF_8
		);

		BuildMetadataModel allowedMetadata = BuildMetadataParser.parse(allowedMetadataFile.toFile());
		assertEquals(LibraryEntry.MAVEN_SCOPE_IMPORT, allowedMetadata.getLibraries().get("org.example_demo-bom").getScope());

		Path rejectedMetadataFile = tempDir.resolve("build-metadata-rejected.toml");
		Files.write(
			rejectedMetadataFile,
			Arrays.asList(
				"[metadata]",
				"revision = \"1.0.0\"",
				"profiles = [\"java8\", \"java17\"]",
				"",
				"[libraries]",
				"\"org.example_demo-lib\" = { java8 = \"1.0.0\", scope = \"custom\" }"
			),
			StandardCharsets.UTF_8
		);

		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> BuildMetadataParser.parse(rejectedMetadataFile.toFile())
		);
		assertEquals("Unsupported Maven dependency scope 'custom' for alias 'org.example_demo-lib'", exception.getMessage());
	}

	private void assertResolvedViaHigherProfiles(BuildMetadataModel metadata, String effectiveProfile) {
		List<VersionedEntry> pomEntries = metadata.getPomPropertyEntries();
		Map<String, String> resolvedByGradle = metadata.resolveVersions(pomEntries, effectiveProfile);
		Map<String, String> resolvedByMavenHigherProfiles = new LinkedHashMap<String, String>(
			metadata.resolveVersions(pomEntries, metadata.getBaseProfile())
		);

		for (String higherProfile : activeHigherProfiles(metadata, effectiveProfile)) {
			resolvedByMavenHigherProfiles.putAll(metadata.resolveChangedVersions(pomEntries, higherProfile));
		}

		assertEquals(resolvedByGradle, resolvedByMavenHigherProfiles);
	}

	private List<String> activeHigherProfiles(BuildMetadataModel metadata, String effectiveProfile) {
		int effectiveProfileIndex = metadata.profileIndex(effectiveProfile);
		return metadata.getSupportedProfiles().subList(1, effectiveProfileIndex + 1);
	}
}
