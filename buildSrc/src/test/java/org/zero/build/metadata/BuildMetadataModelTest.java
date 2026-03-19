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
				"java = 8",
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
