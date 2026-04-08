package org.zero.build.metadata;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuildMetadataGeneratorTest {
	@Test
	void syncShouldGenerateResolvedVersionsAndNotSkipManagedPlugins(@TempDir Path tempDir) throws IOException {
		writeFixture(tempDir);

		BuildMetadataGenerator generator = new BuildMetadataGenerator(tempDir.toFile());
		generator.sync();
		generator.verify();

		String baseCatalog = read(tempDir.resolve("gradle").resolve("libs.versions.toml"));
		assertTrue(baseCatalog.contains("com-auth0_java-jwt = \"4.5.0\""));
		assertTrue(baseCatalog.contains("com-github-ben-manes-caffeine_caffeine = { strictly = \"2.9.3\" }"));
		assertFalse(baseCatalog.contains("org-jooq_jooq = "));
		assertTrue(
			baseCatalog.contains(
				"com-github-ben-manes-versions = { id = \"com.github.ben-manes.versions\", version.ref = \"com-github-ben-manes-versions\" }"
			)
		);

		String java17Catalog = read(tempDir.resolve("gradle").resolve("libs.versions-java17.toml"));
		assertTrue(java17Catalog.contains("com-github-ben-manes-caffeine_caffeine = { strictly = \"3.2.0\" }"));
		assertTrue(java17Catalog.contains("org-jooq_jooq = \"3.19.26\""));
		assertTrue(java17Catalog.contains("com-alibaba_druid-spring-boot-3-starter = \"1.2.27\""));

		String java21Catalog = read(tempDir.resolve("gradle").resolve("libs.versions-java21.toml"));
		assertTrue(java21Catalog.contains("org-jooq_jooq = \"3.20.7\""));

		String pom = read(tempDir.resolve("pom.xml"));
		assertTrue(pom.contains("<version>${revision}</version>"));
		assertTrue(pom.contains("<revision>1.0.0</revision>"));
		assertEquals(1, countMatches(pom, "<revision>1.0.0</revision>"));
		assertTrue(pom.contains("<keep>manual</keep>"));
		assertFalse(pom.contains("<legacy.dep>remove</legacy.dep>"));
		assertFalse(pom.contains("<legacy.plugin>remove</legacy.plugin>"));
		assertFalse(pom.contains("<id>legacy</id>"));
		assertTrue(pom.contains("<org.zero_common-bom.version>${revision}</org.zero_common-bom.version>"));
		assertTrue(pom.contains("<org.apache.maven.plugins_maven-enforcer-plugin.version>3.5.0</org.apache.maven.plugins_maven-enforcer-plugin.version>"));
		assertTrue(pom.contains("<artifactId>jjwt-gson</artifactId>"));
		assertTrue(pom.contains("<artifactId>maven-enforcer-plugin</artifactId>"));
		assertTrue(pom.contains("<artifactId>spring-boot-maven-plugin</artifactId>"));
		assertEquals(2, countMatches(pom, "<artifactId>maven-clean-plugin</artifactId>"));
		assertTrue(pom.contains("<id>java17-higher</id>"));
		assertTrue(pom.contains("<com.github.ben-manes.caffeine_caffeine.version>3.2.0</com.github.ben-manes.caffeine_caffeine.version>"));
		assertTrue(pom.contains("<artifactId>druid-spring-boot-3-starter</artifactId>"));
		assertTrue(pom.contains("<id>java21-higher</id>"));

		String java21Profile = pom.substring(pom.indexOf("<id>java21-higher</id>"), pom.indexOf("</profile>", pom.indexOf("<id>java21-higher</id>")));
		assertFalse(java21Profile.contains("<dependencyManagement>"));
		assertTrue(java21Profile.contains("<org.jooq_jooq-bom.version>3.20.7</org.jooq_jooq-bom.version>"));
		assertTrue(java21Profile.contains("<org.jooq_jooq.version>3.20.7</org.jooq_jooq.version>"));

		String childPom = read(tempDir.resolve("module").resolve("pom.xml"));
		assertEquals(2, countMatches(childPom, "<version>${revision}</version>"));

		String gradleProperties = read(tempDir.resolve("gradle.properties"));
		List<String> gradlePropertyLines = Arrays.asList(gradleProperties.split("\\R"));
		assertTrue(gradleProperties.contains("build.revision=1.0.0"));
		assertTrue(gradleProperties.contains("build.profiles=java8,java17,java21"));
		assertFalse(gradlePropertyLines.contains("revision=1.0.0"));

		String platformMetadata = read(tempDir.resolve("gradle").resolve("platform-metadata.json"));
		assertTrue(platformMetadata.contains("\"baseProfile\": \"java8\""));
		assertTrue(platformMetadata.contains("\"java17\""));
		assertTrue(platformMetadata.contains("\"org-jooq_jooq-bom\"".replace('.', '-')));

		String commonBomGradleProperties = read(tempDir.resolve("common-bom").resolve("gradle.properties"));
		assertTrue(commonBomGradleProperties.contains("build.revision=1.0.0"));
		assertFalse(commonBomGradleProperties.contains("build.profiles="));

		String commonBomPom = read(tempDir.resolve("common-bom").resolve("pom.xml"));
		assertTrue(commonBomPom.contains("<version>${revision}</version>"));
		assertTrue(commonBomPom.contains("<revision>1.0.0</revision>"));
		assertEquals(1, countMatches(commonBomPom, "<revision>1.0.0</revision>"));
	}

	@Test
	void syncGradleShouldOnlyTouchGradleFiles(@TempDir Path tempDir) throws IOException {
		writeFixture(tempDir);

		BuildMetadataGenerator generator = new BuildMetadataGenerator(tempDir.toFile());
		generator.syncGradle();
		generator.verifyGradle();

		String pom = read(tempDir.resolve("pom.xml"));
		assertTrue(pom.contains("<legacy.dep>remove</legacy.dep>"));
		assertTrue(pom.contains("<version>0.0.1</version>"));

		String commonBomPom = read(tempDir.resolve("common-bom").resolve("pom.xml"));
		assertTrue(commonBomPom.contains("<version>0.0.1</version>"));

		String gradleProperties = read(tempDir.resolve("gradle.properties"));
		assertTrue(gradleProperties.contains("build.revision=1.0.0"));
		assertTrue(Files.exists(tempDir.resolve("gradle").resolve("platform-metadata.json")));
	}

	@Test
	void syncMavenShouldOnlyTouchMavenFiles(@TempDir Path tempDir) throws IOException {
		writeFixture(tempDir);

		BuildMetadataGenerator generator = new BuildMetadataGenerator(tempDir.toFile());
		generator.syncMaven();
		generator.verifyMaven();

		String pom = read(tempDir.resolve("pom.xml"));
		assertTrue(pom.contains("<version>${revision}</version>"));
		assertFalse(pom.contains("<legacy.dep>remove</legacy.dep>"));

		String gradleProperties = read(tempDir.resolve("gradle.properties"));
		assertTrue(gradleProperties.contains("build.revision=0.0.1"));
		assertFalse(Files.exists(tempDir.resolve("gradle").resolve("platform-metadata.json")));
	}

	@Test
	void verifyShouldDetectSupportedProfilesDrift(@TempDir Path tempDir) throws IOException {
		writeFixture(tempDir);

		BuildMetadataGenerator generator = new BuildMetadataGenerator(tempDir.toFile());
		generator.sync();
		Files.write(
			tempDir.resolve("gradle.properties"),
			Arrays.asList("org.gradle.jvmargs=-Xmx1g", "build.profiles=java8,java17", "build.revision=1.0.0"),
			StandardCharsets.UTF_8
		);

		IllegalStateException exception = assertThrows(IllegalStateException.class, generator::verify);
		assertTrue(exception.getMessage().contains("gradle.properties"));
	}

	private void writeFixture(Path tempDir) throws IOException {
		Files.createDirectories(tempDir.resolve("metadata"));
		Files.createDirectories(tempDir.resolve("common-bom"));
		Files.createDirectories(tempDir.resolve("gradle"));
		Files.createDirectories(tempDir.resolve("module"));

		Files.write(
			tempDir.resolve("metadata").resolve("build-metadata.toml"),
			Arrays.asList(
				"[metadata]",
				"revision = \"1.0.0\"",
				"profiles = [\"java8\", \"java17\", \"java21\"]",
				"",
				"[libraries]",
				"\"com.auth0_java-jwt\" = \"4.5.0\"",
				"\"io.jsonwebtoken_jjwt-impl\" = \"0.13.0\"",
				"\"io.jsonwebtoken_jjwt-gson\" = \"io.jsonwebtoken_jjwt-impl\"",
				"\"org.zero_common-bom\" = { java8 = \"1.0.0\", scope = \"import\" }",
				"\"com.github.ben-manes.caffeine_caffeine\" = { java8 = \"2.9.3\", java17 = \"3.2.0\", strict = true }",
				"\"org.jooq_jooq-bom\" = { java17 = \"3.19.26\", java21 = \"3.20.7\", scope = \"import\" }",
				"\"org.jooq_jooq\" = \"org.jooq_jooq-bom\"",
				"\"com.alibaba_druid-spring-boot-3-starter\" = { java17 = \"1.2.27\" }",
				"",
				"[plugins.maven]",
				"\"org.apache.maven.plugins_maven-clean-plugin\" = \"3.5.0\"",
				"\"org.apache.maven.plugins_maven-enforcer-plugin\" = \"org.apache.maven.plugins_maven-clean-plugin\"",
				"\"org.springframework.boot_spring-boot-maven-plugin\" = \"2.7.18\"",
				"",
				"[plugins.gradle]",
				"\"java-platform\" = {}",
				"\"com.github.ben-manes.versions\" = \"org.apache.maven.plugins_maven-clean-plugin\""
			),
			StandardCharsets.UTF_8
		);

		Files.write(
			tempDir.resolve("pom.xml"),
			Arrays.asList(
				"<project>",
				"\t<artifactId>common</artifactId>",
				"\t<version>0.0.1</version>",
				"\t<properties>",
				"\t\t<keep>manual</keep>",
				"\t\t<!-- generated dependency-properties:start -->",
				"\t\t<legacy.dep>remove</legacy.dep>",
				"\t\t<!-- generated dependency-properties:end -->",
				"\t\t<!-- generated plugin-properties:start -->",
				"\t\t<legacy.plugin>remove</legacy.plugin>",
				"\t\t<!-- generated plugin-properties:end -->",
				"\t</properties>",
				"\t<dependencyManagement>",
				"\t\t<dependencies>",
				"\t\t\t<!-- generated dependency-management:start -->",
				"\t\t\t<dependency>",
				"\t\t\t\t<groupId>legacy</groupId>",
				"\t\t\t\t<artifactId>legacy-lib</artifactId>",
				"\t\t\t\t<version>0.0.1</version>",
				"\t\t\t</dependency>",
				"\t\t\t<!-- generated dependency-management:end -->",
				"\t\t</dependencies>",
				"\t</dependencyManagement>",
				"\t<build>",
				"\t\t<pluginManagement>",
				"\t\t\t<plugins>",
				"\t\t\t\t<plugin>",
				"\t\t\t\t\t<groupId>org.apache.maven.plugins</groupId>",
				"\t\t\t\t\t<artifactId>maven-clean-plugin</artifactId>",
				"\t\t\t\t\t<version>0.0.9</version>",
				"\t\t\t\t</plugin>",
				"\t\t\t\t<!-- generated plugin-management:start -->",
				"\t\t\t\t<plugin>",
				"\t\t\t\t\t<groupId>legacy</groupId>",
				"\t\t\t\t\t<artifactId>legacy-plugin</artifactId>",
				"\t\t\t\t\t<version>0.0.1</version>",
				"\t\t\t\t</plugin>",
				"\t\t\t\t<!-- generated plugin-management:end -->",
				"\t\t\t</plugins>",
				"\t\t</pluginManagement>",
				"\t</build>",
				"\t<profiles>",
				"\t\t<!-- generated higher-build-profiles:start -->",
				"\t\t<profile>",
				"\t\t\t<id>legacy</id>",
				"\t\t</profile>",
				"\t\t<!-- generated higher-build-profiles:end -->",
				"\t</profiles>",
				"</project>"
			),
			StandardCharsets.UTF_8
		);

		Files.write(
			tempDir.resolve("common-bom").resolve("pom.xml"),
			Arrays.asList(
				"<project>",
				"\t<artifactId>common-bom</artifactId>",
				"\t<version>0.0.1</version>",
				"\t<properties>",
				"\t\t<common-data.version>${project.version}</common-data.version>",
				"\t</properties>",
				"</project>"
			),
			StandardCharsets.UTF_8
		);

		Files.write(
			tempDir.resolve("module").resolve("pom.xml"),
			Arrays.asList(
				"<project>",
				"\t<parent>",
				"\t\t<groupId>org.zero</groupId>",
				"\t\t<artifactId>common</artifactId>",
				"\t\t<version>0.0.1</version>",
				"\t</parent>",
				"\t<artifactId>module</artifactId>",
				"\t<version>0.0.1</version>",
				"</project>"
			),
			StandardCharsets.UTF_8
		);

		Files.write(
			tempDir.resolve("gradle.properties"),
			Arrays.asList("org.gradle.jvmargs=-Xmx1g", "build.revision=0.0.1"),
			StandardCharsets.UTF_8
		);
		Files.write(
			tempDir.resolve("common-bom").resolve("gradle.properties"),
			Arrays.asList("build.revision=0.0.1"),
			StandardCharsets.UTF_8
		);
	}

	private String read(Path path) throws IOException {
		return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
	}

	private int countMatches(String text, String literal) {
		Matcher matcher = Pattern.compile(Pattern.quote(literal)).matcher(text);
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		return count;
	}
}
