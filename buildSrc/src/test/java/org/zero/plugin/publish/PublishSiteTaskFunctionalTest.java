package org.zero.plugin.publish;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PublishSiteTaskFunctionalTest {
    @Test
    void publishSiteShouldSkipPushWhenDryRunEnabled(@TempDir Path tempDir) throws IOException {
        Assumptions.assumeTrue(isGitAvailable(), "git not available");

        Path remoteRepository = initBareRepository(tempDir.resolve("remote.git"));
        Path projectDir = tempDir.resolve("project");
        Files.createDirectories(projectDir);
        writeBuildSrc(projectDir);
        writeBuild(projectDir, remoteRepository.toUri().toString(), true);

        BuildResult result = runner(projectDir)
            .withArguments("publishSite", "--stacktrace")
            .build();

        assertTrue(result.getOutput().contains("dry-run enabled"));
        assertFalse(remoteBranchExists(remoteRepository, "main"));
    }

    @Test
    void publishSiteShouldReportNoChangesOnSecondRun(@TempDir Path tempDir) throws IOException {
        Assumptions.assumeTrue(isGitAvailable(), "git not available");

        Path remoteRepository = initBareRepository(tempDir.resolve("remote.git"));
        Path projectDir = tempDir.resolve("project");
        Files.createDirectories(projectDir);
        writeBuildSrc(projectDir);
        writeBuild(projectDir, remoteRepository.toUri().toString(), false);

        runner(projectDir).withArguments("publishSite", "--stacktrace").build();
        assertTrue(remoteBranchExists(remoteRepository, "main"));

        BuildResult secondRun = runner(projectDir)
            .withArguments("publishSite", "--stacktrace")
            .build();

        assertTrue(secondRun.getOutput().contains("publishSite: no site changes detected."));
    }

    private GradleRunner runner(Path projectDir) {
        return GradleRunner.create()
            .withProjectDir(projectDir.toFile());
    }

    private void writeBuild(Path projectDir, String remoteUrl, boolean dryRun) throws IOException {
        Files.write(
            projectDir.resolve("settings.gradle"),
            Arrays.asList("rootProject.name = 'site-publish-sample'"),
            StandardCharsets.UTF_8
        );
        Files.write(
            projectDir.resolve("build.gradle"),
            Arrays.asList(
                "import org.zero.plugin.publish.PublishSiteTask",
                "",
                "plugins {",
                "    id 'base'",
                "}",
                "",
                "tasks.register('publish') {",
                "    doLast {",
                "        def repoDir = layout.buildDirectory.dir('mvn-repo').get().asFile",
                "        repoDir.mkdirs()",
                "        new File(repoDir, 'demo.txt').text = 'hello\\n'",
                "    }",
                "}",
                "",
                "tasks.register('publishSite', PublishSiteTask) {",
                "    dependsOn tasks.named('publish')",
                "    inputDirectory.set(layout.buildDirectory.dir('mvn-repo'))",
                "    workspaceDirectory.set(layout.buildDirectory.dir('site-publish'))",
                "    remoteUrl.set('" + remoteUrl.replace("\\", "\\\\") + "')",
                "    repositoryOwner.set('local')",
                "    repositoryName.set('site')",
                "    branch.set('main')",
                "    commitMessage.set('publish demo')",
                "    merge.set(true)",
                "    noJekyll.set(true)",
                "    dryRun.set(" + dryRun + ")",
                "}"
            ),
            StandardCharsets.UTF_8
        );
    }

    private void writeBuildSrc(Path projectDir) throws IOException {
        writeFile(
            projectDir.resolve("buildSrc/build.gradle"),
            "plugins {",
            "    id 'java'",
            "}",
            "",
            "repositories {",
            "    mavenCentral()",
            "    gradlePluginPortal()",
            "}",
            "",
            "dependencies {",
            "    implementation gradleApi()",
            "}"
        );
        Path sourceFile = Paths.get("src", "main", "java", "org", "zero", "plugin", "publish", "PublishSiteTask.java").toAbsolutePath();
        Path targetFile = projectDir.resolve("buildSrc/src/main/java/org/zero/plugin/publish/PublishSiteTask.java");
        Files.createDirectories(targetFile.getParent());
        Files.copy(sourceFile, targetFile);
    }

    private Path initBareRepository(Path remoteRepository) {
        runCommand(remoteRepository.getParent(), "git", "init", "--bare", remoteRepository.toString());
        return remoteRepository;
    }

    private boolean remoteBranchExists(Path remoteRepository, String branch) {
        CommandResult result = runCommand(
            remoteRepository.getParent(),
            false,
            "git",
            "ls-remote",
            "--exit-code",
            "--heads",
            remoteRepository.toString(),
            "refs/heads/" + branch
        );
        return result.exitCode == 0;
    }

    private boolean isGitAvailable() {
        return runCommand(null, false, "git", "--version").exitCode == 0;
    }

    private CommandResult runCommand(Path workingDirectory, String... command) {
        return runCommand(workingDirectory, true, command);
    }

    private CommandResult runCommand(Path workingDirectory, boolean failOnError, String... command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            if (workingDirectory != null) {
                processBuilder.directory(workingDirectory.toFile());
            }
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            String output = readAll(process.getInputStream());
            int exitCode = process.waitFor();
            if (failOnError && exitCode != 0) {
                throw new IllegalStateException("Command failed: " + Arrays.toString(command) + "\n" + output);
            }
            return new CommandResult(exitCode, output);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to run command: " + Arrays.toString(command), ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while running command: " + Arrays.toString(command), ex);
        }
    }

    private String readAll(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = inputStream.read(buffer)) >= 0) {
            outputStream.write(buffer, 0, read);
        }
        return outputStream.toString("UTF-8");
    }

    private void writeFile(Path path, String... lines) throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.write(path, Arrays.asList(lines), StandardCharsets.UTF_8);
    }

    private static final class CommandResult {
        private final int exitCode;
        private final String output;

        private CommandResult(int exitCode, String output) {
            this.exitCode = exitCode;
            this.output = output;
        }
    }
}
