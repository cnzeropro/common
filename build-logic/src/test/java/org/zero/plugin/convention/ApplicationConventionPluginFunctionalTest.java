package org.zero.plugin.convention;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationConventionPluginFunctionalTest {
    @Test
    void java8ApplicationConventionShouldNotRegisterPublishingTasks(@TempDir Path tempDir) throws IOException {
        writeFile(
            tempDir.resolve("settings.gradle"),
            "rootProject.name = 'sample-app'"
        );
        writeFile(
            tempDir.resolve("gradle/libs.versions.toml"),
            "[versions]",
            "junit = '5.12.2'",
            "org-slf4j_slf4j-bom = '2.0.17'",
            "",
            "[libraries]",
            "org-junit_junit-bom = { module = 'org.junit:junit-bom', version.ref = 'junit' }"
        );
        writeFile(tempDir.resolve("build.gradle"), String.join(
            System.lineSeparator(),
            "plugins {",
            "    id 'org.zero.conventions.java8-application'",
            "}",
            "",
            "group = 'org.zero.test'",
            "version = '1.0.0'"
        ));

        BuildResult result = GradleRunner.create()
            .withProjectDir(tempDir.toFile())
            .withArguments("tasks", "--all")
            .withPluginClasspath()
            .build();

        assertTrue(result.getOutput().contains("Build tasks"));
        assertFalse(result.getOutput().contains("publishToMavenLocal"));
        assertFalse(result.getOutput().contains("mavenJava"));
    }

    private void writeFile(Path path, String... lines) throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.write(path, Arrays.asList(lines), StandardCharsets.UTF_8);
    }
}
