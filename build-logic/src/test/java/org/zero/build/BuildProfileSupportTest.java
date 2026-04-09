package org.zero.build;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Properties;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BuildProfileSupportTest {
	@Test
	void resolveProfileShouldHonorExplicitProfile(@TempDir Path tempDir) throws IOException {
		Project project = createProject(tempDir, true);

		String resolvedProfile = BuildProfileSupport.resolveProfile(project, "java17", "1.8.0_451");

		assertEquals("java17", resolvedProfile);
	}

	@Test
	void resolveActiveProfileChainShouldRepresentCurrentHigherProfiles(@TempDir Path tempDir) throws IOException {
		Project project = createProject(tempDir, true);

		assertEquals(
			Arrays.asList("java8", "java11", "java17", "java21"),
			BuildProfileSupport.resolveActiveProfileChain(project, null, "21.0.1")
		);
	}

	@Test
	void resolveActiveProfileChainShouldHonorExplicitProfile(@TempDir Path tempDir) throws IOException {
		Project project = createProject(tempDir, true);

		assertEquals(
			Arrays.asList("java8", "java11", "java17"),
			BuildProfileSupport.resolveActiveProfileChain(project, "java17", "21.0.1")
		);
	}

	@Test
	void resolveProfileAtLeastShouldUseBaselineWhenCurrentJvmIsLower(@TempDir Path tempDir) throws IOException {
		Project project = createProject(tempDir, true);

		String resolvedProfile = BuildProfileSupport.resolveProfileAtLeast(project, null, "java17", "1.8.0_451");

		assertEquals("java17", resolvedProfile);
	}

	@Test
	void resolveProfileAtLeastShouldKeepHigherCompatibleProfile(@TempDir Path tempDir) throws IOException {
		Project project = createProject(tempDir, true);

		String resolvedProfile = BuildProfileSupport.resolveProfileAtLeast(project, null, "java17", "21.0.1");

		assertEquals("java21", resolvedProfile);
	}

	@Test
	void resolveProfileShouldResolveJava25WhenCurrentJvmMatches(@TempDir Path tempDir) throws IOException {
		Project project = createProject(tempDir, true);

		String resolvedProfile = BuildProfileSupport.resolveProfile(project, null, "25.0.1");

		assertEquals("java25", resolvedProfile);
	}

	@Test
	void isAtLeastShouldCompareSupportedProfiles(@TempDir Path tempDir) throws IOException {
		Project project = createProject(tempDir, true);

		assertTrue(BuildProfileSupport.isAtLeast(project, "java25", "java17"));
		assertFalse(BuildProfileSupport.isAtLeast(project, "java11", "java17"));
	}

	@Test
	void resolveProfileAtLeastShouldRejectExplicitProfileBelowBaseline(@TempDir Path tempDir) throws IOException {
		Project project = createProject(tempDir, true);

		IllegalArgumentException exception = assertThrows(
			IllegalArgumentException.class,
			() -> BuildProfileSupport.resolveProfileAtLeast(project, "java8", "java17", "1.8.0_451")
		);

		assertTrue(exception.getMessage().contains("required baseline 'java17'"));
	}

	@Test
	void resolveProfileShouldFailWhenSupportedProfilesPropertyMissing(@TempDir Path tempDir) throws IOException {
		Project project = createProject(tempDir, false);

		IllegalStateException exception = assertThrows(
			IllegalStateException.class,
			() -> BuildProfileSupport.resolveProfile(project, null, "17.0.10")
		);

		assertTrue(exception.getMessage().contains("build.profiles"));
	}

	private Project createProject(Path tempDir, boolean writeSupportedProfiles) throws IOException {
		Files.createDirectories(tempDir);
		if (writeSupportedProfiles) {
			Files.write(
				tempDir.resolve("gradle.properties"),
				Arrays.asList(
					"org.gradle.jvmargs=-Xmx1g",
					"build.profiles=java8,java11,java17,java21,java25",
					"build.revision=1.0.0"
				),
				StandardCharsets.UTF_8
			);
		} else {
			Files.write(
				tempDir.resolve("gradle.properties"),
				Arrays.asList(
					"org.gradle.jvmargs=-Xmx1g",
					"build.revision=1.0.0"
				),
				StandardCharsets.UTF_8
			);
		}
		Files.write(
			tempDir.resolve("settings.gradle"),
			Arrays.asList("rootProject.name = 'test-project'"),
			StandardCharsets.UTF_8
		);
		Project project = ProjectBuilder.builder().withProjectDir(tempDir.toFile()).build();
		if (writeSupportedProfiles) {
			Properties properties = new Properties();
			try (java.io.Reader reader = Files.newBufferedReader(tempDir.resolve("gradle.properties"), StandardCharsets.UTF_8)) {
				properties.load(reader);
			}
			project.getExtensions().getExtraProperties().set(
				"build.profiles",
				properties.getProperty("build.profiles")
			);
		}
		return project;
	}

}
