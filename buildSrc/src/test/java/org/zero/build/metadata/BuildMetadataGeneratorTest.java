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
	void syncShouldGenerateResolvedVersionsFromScalarAndReferenceDeclarations(@TempDir Path tempDir) throws IOException {
		writeFixture(tempDir);

		BuildMetadataGenerator generator = new BuildMetadataGenerator(tempDir.toFile());
		generator.sync();
		generator.verify();

		String catalog = read(tempDir.resolve("gradle").resolve("libs.versions.toml"));
		assertFalse(catalog.contains("java = \"8\""));
		assertTrue(catalog.contains("com-auth0_java-jwt = \"4.5.0\""));
		assertTrue(catalog.contains("io-jsonwebtoken_jjwt-gson = \"0.13.0\""));
		assertTrue(catalog.contains("com-github-ben-manes-caffeine_caffeine = { strictly = \"2.9.3\" }"));
		assertTrue(catalog.contains("com-github-ben-manes-caffeine_caffeine_java17 = { strictly = \"3.2.0\" }"));
		assertTrue(catalog.contains("org-jooq_jooq-bom_java17 = \"3.19.26\""));
		assertTrue(catalog.contains("org-jooq_jooq_java17 = \"3.19.26\""));
		assertTrue(catalog.contains("org-jooq_jooq_java21 = \"3.20.7\""));
		assertTrue(
			catalog.contains(
				"com-github-ben-manes-versions = { id = \"com.github.ben-manes.versions\", version.ref = \"com-github-ben-manes-versions\" }"
			)
		);
		assertFalse(catalog.contains("java-platform = { id = "));

		String pom = read(tempDir.resolve("pom.xml"));
		assertTrue(pom.contains("<!-- ************************************ Config ************************************ -->"));
		assertTrue(pom.contains("<!-- ************************************ Project ************************************ -->"));
		assertTrue(pom.contains("<!-- ************************************ Dependencies ************************************ -->"));
		assertTrue(pom.contains("<!-- ************************************ Plugins ************************************ -->"));
		assertTrue(pom.contains("<java.version>8</java.version>"));
		assertTrue(pom.contains("<project.encoding>UTF-8</project.encoding>"));
		assertTrue(pom.contains("<maven.compiler.encoding>${project.encoding}</maven.compiler.encoding>"));
		assertFalse(pom.contains("<legacy>keep-nothing</legacy>"));
		assertTrue(pom.contains("<org.zero_common-bom.version>${revision}</org.zero_common-bom.version>"));
		assertTrue(pom.contains("<org.apache.maven.plugins_maven-enforcer-plugin.version>3.5.0</org.apache.maven.plugins_maven-enforcer-plugin.version>"));
		assertTrue(pom.contains("<io.jsonwebtoken_jjwt-gson.version>0.13.0</io.jsonwebtoken_jjwt-gson.version>"));
		assertTrue(pom.contains("<artifactId>jjwt-gson</artifactId>"));
		assertTrue(pom.contains("<artifactId>maven-enforcer-plugin</artifactId>"));
		assertFalse(pom.contains("<goal>repackage</goal>"));
		assertFalse(pom.contains("<mainClass>"));
		assertTrue(pom.contains("<id>java17-higher</id>"));
		assertTrue(pom.contains("<org.jooq_jooq.version>3.19.26</org.jooq_jooq.version>"));
		assertTrue(pom.contains("<artifactId>jooq</artifactId>"));
		assertTrue(pom.contains("<artifactId>druid-spring-boot-3-starter</artifactId>"));
		assertTrue(pom.contains("<id>java21-higher</id>"));
		assertEquals(1, countMatches(pom, "<artifactId>spring-boot-maven-plugin</artifactId>"));
		assertEquals(1, countMatches(pom, "<org.zero_common-bom.version>${revision}</org.zero_common-bom.version>"));
		assertTrue(indexOfOrFail(pom, "<java.version>8</java.version>") < indexOfOrFail(pom, "<project.encoding>UTF-8</project.encoding>"));
		assertTrue(indexOfOrFail(pom, "<project.encoding>UTF-8</project.encoding>") < indexOfOrFail(pom, "<revision>1.0.0</revision>"));
		assertTrue(indexOfOrFail(pom, "<revision>1.0.0</revision>") < indexOfOrFail(pom, "<com.auth0_java-jwt.version>4.5.0</com.auth0_java-jwt.version>"));
		assertTrue(
			indexOfOrFail(pom, "<!-- ************************************ Dependencies ************************************ -->")
				< indexOfOrFail(pom, "<org.zero_common-bom.version>${revision}</org.zero_common-bom.version>")
		);
		assertTrue(
			indexOfOrFail(pom, "<org.zero_common-bom.version>${revision}</org.zero_common-bom.version>")
				< indexOfOrFail(pom, "<!-- ************************************ Plugins ************************************ -->")
		);
		assertTrue(indexOfOrFail(pom, "<com.auth0_java-jwt.version>4.5.0</com.auth0_java-jwt.version>") < indexOfOrFail(pom, "<io.jsonwebtoken_jjwt-gson.version>0.13.0</io.jsonwebtoken_jjwt-gson.version>"));
		assertTrue(indexOfOrFail(pom, "<io.jsonwebtoken_jjwt-gson.version>0.13.0</io.jsonwebtoken_jjwt-gson.version>") < indexOfOrFail(pom, "<org.springframework.boot_spring-boot-maven-plugin.version>2.7.18</org.springframework.boot_spring-boot-maven-plugin.version>"));
		assertTrue(indexOfOrFail(pom, "<org.apache.maven.plugins_maven-clean-plugin.version>3.5.0</org.apache.maven.plugins_maven-clean-plugin.version>") < indexOfOrFail(pom, "<org.apache.maven.plugins_maven-enforcer-plugin.version>3.5.0</org.apache.maven.plugins_maven-enforcer-plugin.version>"));
		assertTrue(pom.contains("<!-- https://mvnrepository.com/artifact/com.auth0/java-jwt -->"));
		assertTrue(pom.contains("<!-- https://mvnrepository.com/artifact/org.apache.maven.plugins/maven-clean-plugin -->"));
		assertTrue(indexOfOrFail(pom, "<!-- https://mvnrepository.com/artifact/com.auth0/java-jwt -->") < indexOfOrFail(pom, "<artifactId>java-jwt</artifactId>"));
		assertTrue(indexOfOrFail(pom, "<artifactId>java-jwt</artifactId>") < indexOfOrFail(pom, "<artifactId>jjwt-gson</artifactId>"));
		assertTrue(indexOfOrFail(pom, "<artifactId>maven-clean-plugin</artifactId>") < indexOfOrFail(pom, "<artifactId>maven-enforcer-plugin</artifactId>"));
		assertTrue(indexOfOrFail(pom, "<artifactId>maven-enforcer-plugin</artifactId>") < indexOfOrFail(pom, "<artifactId>spring-boot-maven-plugin</artifactId>"));

		String java21Profile = pom.substring(pom.indexOf("<id>java21-higher</id>"), pom.indexOf("</profile>", pom.indexOf("<id>java21-higher</id>")));
		assertFalse(java21Profile.contains("<dependencyManagement>"));
		assertTrue(java21Profile.contains("<org.jooq_jooq-bom.version>3.20.7</org.jooq_jooq-bom.version>"));
		assertTrue(java21Profile.contains("<org.jooq_jooq.version>3.20.7</org.jooq_jooq.version>"));

		String gradleProperties = read(tempDir.resolve("gradle.properties"));
		List<String> gradlePropertyLines = Arrays.asList(gradleProperties.split("\\R"));
		assertTrue(gradleProperties.contains("build.revision=1.0.0"));
		assertTrue(gradleProperties.contains("build.profiles=java8,java17,java21"));
		assertFalse(gradlePropertyLines.contains("revision=1.0.0"));
		assertFalse(gradlePropertyLines.contains("zero.build.supportedProfiles=java8,java17,java21"));

		String commonBomGradleProperties = read(tempDir.resolve("common-bom").resolve("gradle.properties"));
		List<String> commonBomGradlePropertyLines = Arrays.asList(commonBomGradleProperties.split("\\R"));
		assertTrue(commonBomGradleProperties.contains("build.revision=1.0.0"));
		assertFalse(commonBomGradleProperties.contains("build.profiles="));
		assertFalse(commonBomGradlePropertyLines.contains("revision=1.0.0"));

		String commonBomPom = read(tempDir.resolve("common-bom").resolve("pom.xml"));
		assertTrue(commonBomPom.contains("<version>${revision}</version>"));
		assertTrue(commonBomPom.contains("<revision>1.0.0</revision>"));
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

		Files.write(
			tempDir.resolve("metadata").resolve("build-metadata.toml"),
			Arrays.asList(
				"[metadata]",
				"revision = \"1.0.0\"",
				"profiles = [\"java8\", \"java17\", \"java21\"]",
				"java = 8",
				"",
				"[properties.maven]",
				"\"project.encoding\" = \"UTF-8\"",
				"\"resource.delimiter\" = \"@\"",
				"\"maven.compiler.encoding\" = \"${project.encoding}\"",
				"\"project.build.sourceEncoding\" = \"${project.encoding}\"",
				"\"project.reporting.outputEncoding\" = \"${project.encoding}\"",
				"\"maven.verbose\" = \"false\"",
				"\"maven.clean.verbose\" = \"${maven.verbose}\"",
				"\"maven.compiler.verbose\" = \"${maven.verbose}\"",
				"",
				"[libraries]",
				"\"jakarta.platform_jakarta.jakartaee-bom\" = { java8 = \"9.1.0\", scope = \"import\" }",
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
				"\t\t<legacy>keep-nothing</legacy>",
				"\t</properties>",
				"\t<dependencyManagement>",
				"\t\t<dependencies>",
				"\t\t\t<!-- generated dependency-management:start -->",
				"\t\t\t<dependency>",
				"\t\t\t\t<groupId>com.auth0</groupId>",
				"\t\t\t\t<artifactId>java-jwt</artifactId>",
				"\t\t\t\t<version>${com.auth0_java-jwt.version}</version>",
				"\t\t\t</dependency>",
				"\t\t\t<!-- generated dependency-management:end -->",
				"\t\t</dependencies>",
				"\t</dependencyManagement>",
				"\t<build>",
				"\t\t<pluginManagement>",
				"\t\t\t<plugins>",
				"\t\t\t\t<!-- generated plugin-management:start -->",
				"\t\t\t\t<plugin>",
				"\t\t\t\t\t<groupId>org.apache.maven.plugins</groupId>",
				"\t\t\t\t\t<artifactId>maven-clean-plugin</artifactId>",
				"\t\t\t\t\t<version>${org.apache.maven.plugins_maven-clean-plugin.version}</version>",
				"\t\t\t\t</plugin>",
				"\t\t\t\t<!-- generated plugin-management:end -->",
				"\t\t\t</plugins>",
				"\t\t</pluginManagement>",
				"\t</build>",
				"\t<profiles>",
				"\t\t<!-- generated higher-build-profiles:start -->",
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

	private int indexOfOrFail(String text, String literal) {
		int index = text.indexOf(literal);
		assertTrue(index >= 0, "Missing literal: " + literal);
		return index;
	}
}
