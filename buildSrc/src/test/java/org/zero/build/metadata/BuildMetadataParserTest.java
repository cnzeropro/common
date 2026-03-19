package org.zero.build.metadata;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuildMetadataParserTest {
	@Test
	void parseShouldSupportScalarDeclarationsAndAliasReferences(@TempDir Path tempDir) throws IOException {
		Path metadataFile = tempDir.resolve("build-metadata.toml");
		Files.write(
			metadataFile,
			Arrays.asList(
				"[metadata]",
				"revision = \"1.2.3\"",
				"profiles = [\"java8\", \"java11\", \"java17\", \"java21\"]",
				"java = 8",
				"",
				"[properties.maven]",
				"\"project.encoding\" = \"UTF-8\"",
				"\"maven.compiler.encoding\" = \"${project.encoding}\"",
				"",
				"[libraries]",
				"\"com.auth0_java-jwt\" = \"4.5.0\"",
				"\"io.jsonwebtoken_jjwt-impl\" = \"0.13.0\"",
				"\"io.jsonwebtoken_jjwt-gson\" = \"io.jsonwebtoken_jjwt-impl\"",
				"\"com.example_demo-lib\" = \"org.apache.maven.plugins_maven-clean-plugin\"",
				"\"org.jooq_jooq-bom\" = { java17 = \"3.19.26\", java21 = \"3.20.7\", scope = \"import\" }",
				"\"org.jooq_jooq\" = \"org.jooq_jooq-bom\"",
				"\"org.springdoc_springdoc-openapi-bom\" = { java8 = \"io.jsonwebtoken_jjwt-impl\", java17 = \"2.8.13\", scope = \"import\" }",
				"",
				"[plugins.maven]",
				"\"org.apache.maven.plugins_maven-clean-plugin\" = \"3.5.0\"",
				"\"org.apache.maven.plugins_maven-enforcer-plugin\" = \"org.apache.maven.plugins_maven-clean-plugin\"",
				"",
				"[plugins.gradle]",
				"\"java-platform\" = {}",
				"\"com.github.ben-manes.versions\" = \"org.apache.maven.plugins_maven-clean-plugin\""
			),
			StandardCharsets.UTF_8
		);

		BuildMetadataModel metadata = BuildMetadataParser.parse(metadataFile.toFile());

		assertEquals(Arrays.asList("java8", "java11", "java17", "java21"), metadata.getSupportedProfiles());
		assertEquals("1.2.3", metadata.getRevision());
		assertEquals("8", metadata.getJavaVersion());
		assertEquals("UTF-8", metadata.getMavenProperties().get("project.encoding"));
		assertEquals("${project.encoding}", metadata.getMavenProperties().get("maven.compiler.encoding"));
		assertTrue(metadata.getValues().isEmpty());

		LibraryEntry auth0 = metadata.getLibraries().get("com.auth0_java-jwt");
		assertEquals("4.5.0", auth0.resolveVersion(metadata, "java8"));

		LibraryEntry jjwtGson = metadata.getLibraries().get("io.jsonwebtoken_jjwt-gson");
		assertEquals("0.13.0", jjwtGson.resolveVersion(metadata, "java21"));

		LibraryEntry demoLib = metadata.getLibraries().get("com.example_demo-lib");
		assertEquals("com.example:demo-lib", demoLib.getCoordinate());
		assertEquals("3.5.0", demoLib.resolveVersion(metadata, "java8"));

		LibraryEntry jooq = metadata.getLibraries().get("org.jooq_jooq");
		assertNull(jooq.resolveVersion(metadata, "java11"));
		assertEquals("3.19.26", jooq.resolveVersion(metadata, "java17"));
		assertEquals("3.20.7", jooq.resolveVersion(metadata, "java21"));

		LibraryEntry springdoc = metadata.getLibraries().get("org.springdoc_springdoc-openapi-bom");
		assertEquals("0.13.0", springdoc.resolveVersion(metadata, "java8"));
		assertEquals("2.8.13", springdoc.resolveVersion(metadata, "java17"));
		assertEquals("import", springdoc.getScope());

		MavenPluginEntry cleanPlugin = metadata.getMavenPlugins().get("org.apache.maven.plugins_maven-clean-plugin");
		assertEquals("3.5.0", cleanPlugin.resolveVersion(metadata, "java21"));

		MavenPluginEntry enforcerPlugin = metadata.getMavenPlugins().get("org.apache.maven.plugins_maven-enforcer-plugin");
		assertEquals("3.5.0", enforcerPlugin.resolveVersion(metadata, "java21"));

		GradlePluginEntry corePlugin = metadata.getGradlePlugins().get("java-platform");
		assertFalse(corePlugin.hasVersions());
		assertEquals("java-platform", corePlugin.getPluginId());

		GradlePluginEntry benManes = metadata.getGradlePlugins().get("com.github.ben-manes.versions");
		assertTrue(benManes.hasVersions());
		assertEquals("3.5.0", benManes.resolveVersion(metadata, "java17"));
	}

	@Test
	void parseShouldRejectMavenPluginWithoutBaseProfileVersion(@TempDir Path tempDir) throws IOException {
		Path metadataFile = tempDir.resolve("build-metadata.toml");
		Files.write(
			metadataFile,
			Arrays.asList(
				"[metadata]",
				"revision = \"1.0.0\"",
				"profiles = [\"java8\", \"java17\"]",
				"java = 8",
				"",
				"[plugins.maven]",
				"\"org.apache.maven.plugins_maven-clean-plugin\" = { java17 = \"3.5.0\" }"
			),
			StandardCharsets.UTF_8
		);

		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> BuildMetadataParser.parse(metadataFile.toFile())
		);

		assertTrue(exception.getMessage().contains("base profile 'java8'"));
	}

	@Test
	void parseShouldRejectMissingMetadataJava(@TempDir Path tempDir) throws IOException {
		Path metadataFile = tempDir.resolve("build-metadata.toml");
		Files.write(
			metadataFile,
			Arrays.asList(
				"[metadata]",
				"revision = \"1.0.0\"",
				"profiles = [\"java8\", \"java17\"]",
				"",
				"[libraries]",
				"\"com.auth0_java-jwt\" = \"4.5.0\""
			),
			StandardCharsets.UTF_8
		);

		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> BuildMetadataParser.parse(metadataFile.toFile())
		);

		assertTrue(exception.getMessage().contains("Missing metadata java"));
	}

	@Test
	void parseShouldRejectCircularAndSelfReferences(@TempDir Path tempDir) throws IOException {
		Path circularFile = tempDir.resolve("build-metadata-circular.toml");
		Files.write(
			circularFile,
			Arrays.asList(
				"[metadata]",
				"revision = \"1.0.0\"",
				"profiles = [\"java8\", \"java17\"]",
				"java = 8",
				"",
				"[libraries]",
				"\"com.example_alpha\" = \"com.example_beta\"",
				"\"com.example_beta\" = \"com.example_alpha\""
			),
			StandardCharsets.UTF_8
		);

		IllegalArgumentException circular = assertThrows(
			IllegalArgumentException.class,
			() -> BuildMetadataParser.parse(circularFile.toFile())
		);
		assertTrue(circular.getMessage().contains("Circular version reference"));

		Path selfFile = tempDir.resolve("build-metadata-self.toml");
		Files.write(
			selfFile,
			Arrays.asList(
				"[metadata]",
				"revision = \"1.0.0\"",
				"profiles = [\"java8\", \"java17\"]",
				"java = 8",
				"",
				"[plugins.maven]",
				"\"org.apache.maven.plugins_maven-clean-plugin\" = \"org.apache.maven.plugins_maven-clean-plugin\""
			),
			StandardCharsets.UTF_8
		);

		IllegalArgumentException self = assertThrows(
			IllegalArgumentException.class,
			() -> BuildMetadataParser.parse(selfFile.toFile())
		);
		assertTrue(self.getMessage().contains("Circular version reference"));
	}

	@Test
	void parseShouldRejectScopeOutsideLibraries(@TempDir Path tempDir) throws IOException {
		Path metadataFile = tempDir.resolve("build-metadata.toml");
		Files.write(
			metadataFile,
			Arrays.asList(
				"[metadata]",
				"revision = \"1.0.0\"",
				"profiles = [\"java8\", \"java17\"]",
				"java = 8",
				"",
				"[plugins.maven]",
				"\"org.apache.maven.plugins_maven-clean-plugin\" = { java8 = \"3.5.0\", scope = \"import\" }"
			),
			StandardCharsets.UTF_8
		);

		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> BuildMetadataParser.parse(metadataFile.toFile())
		);

		assertTrue(exception.getMessage().contains("Unsupported key 'scope'"));
	}

	@Test
	void parseShouldRejectNonScalarMavenProperty(@TempDir Path tempDir) throws IOException {
		Path metadataFile = tempDir.resolve("build-metadata.toml");
		Files.write(
			metadataFile,
			Arrays.asList(
				"[metadata]",
				"revision = \"1.0.0\"",
				"profiles = [\"java8\", \"java17\"]",
				"java = 8",
				"",
				"[properties.maven]",
				"\"project.encoding\" = { java8 = \"UTF-8\" }"
			),
			StandardCharsets.UTF_8
		);

		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> BuildMetadataParser.parse(metadataFile.toFile())
		);

		assertTrue(exception.getMessage().contains("Unsupported Maven property value"));
	}
}
