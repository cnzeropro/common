package org.zero.plugin.convention;

import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;
import org.gradle.jvm.toolchain.JavaLanguageVersion;
import org.gradle.jvm.toolchain.JavaToolchainService;
import org.zero.build.BuildProfileSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Multi-Release Java 8 convention - 在 Java 8 主线之上追加 Java 9+ 的多版本源码输出。
 */
public final class MultiReleaseJava8ConventionPlugin extends AbstractJvmConventionPlugin {
    private static final Pattern VERSIONED_SOURCE_SET_PATTERN = Pattern.compile("^java(\\d+)$");

    @Override
    protected String basePluginId() {
        return "java-library";
    }

    @Override
    protected int languageVersion() {
        return 8;
    }

    @Override
    protected void configureAdditionalConventions(Project project) {
        SourceSetContainer sourceSets = project.getExtensions().getByType(SourceSetContainer.class);
        SourceSet mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME);
        SourceSet testSourceSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME);
        JavaToolchainService toolchains = project.getExtensions().getByType(JavaToolchainService.class);

        // Multi-Release manifest - 让 JVM 在运行时优先读取 META-INF/versions 下的实现。
        project.getTasks().named("jar", Jar.class)
            .configure(task -> task.getManifest().attributes(java.util.Collections.singletonMap("Multi-Release", "true")));
        project.getTasks().named("sourcesJar", Jar.class).configure(task -> task.setDuplicatesStrategy(DuplicatesStrategy.EXCLUDE));

        for (Integer version : discoverMultiReleaseVersions(project)) {
            configureMultiReleaseVersion(project, mainSourceSet, testSourceSet, toolchains, version.intValue());
        }
    }

    private List<Integer> discoverMultiReleaseVersions(Project project) {
        // Source directory discovery - 从 src/main|test 下的 java<version> 目录动态发现多版本源码目录。
        List<Integer> versions = new ArrayList<Integer>();
        collectVersions(project.file("src/main"), versions);
        collectVersions(project.file("src/test"), versions);
        Collections.sort(versions);
        return versions;
    }

    private void collectVersions(File sourceRoot, List<Integer> versions) {
        File[] children = sourceRoot.listFiles();
        if (children == null) {
            return;
        }
        for (File child : children) {
            if (!child.isDirectory()) {
                continue;
            }
            Matcher matcher = VERSIONED_SOURCE_SET_PATTERN.matcher(child.getName());
            if (!matcher.matches()) {
                continue;
            }
            Integer version = Integer.valueOf(matcher.group(1));
            if (!versions.contains(version)) {
                versions.add(version);
            }
        }
    }

    private void configureMultiReleaseVersion(
        Project project,
        SourceSet mainSourceSet,
        SourceSet testSourceSet,
        JavaToolchainService toolchains,
        int version
    ) {
        int toolchainVersion = BuildProfileSupport.resolveToolchainLanguageVersion(project, version);
        File mainSourceDir = project.file("src/main/java" + version);
        Provider<Directory> outputDirectory = project.getLayout().getBuildDirectory().dir("classes/java/multiRelease/" + version);
        TaskProvider<JavaCompile> compileTask = project.getTasks().register("compileJava" + version, JavaCompile.class, task -> {
            // Versioned main source - 独立编译高版本目录，并复用 Java 8 主线的 classpath。
            task.source(project.fileTree(mainSourceDir, spec -> spec.include("**/*.java")));
            task.setClasspath(project.files(mainSourceSet.getOutput(), mainSourceSet.getCompileClasspath()));
            task.getDestinationDirectory().set(outputDirectory);
            task.getOptions().getRelease().set(version);
            task.getOptions().getCompilerArgs().add("-parameters");
            task.getOptions().setEncoding("UTF-8");
            task.getJavaCompiler().set(toolchains.compilerFor(spec -> spec.getLanguageVersion().set(JavaLanguageVersion.of(toolchainVersion))));
            // Lazy activation - 目录不存在或没有源码时，不创建空的编译结果。
            task.onlyIf(spec -> mainSourceDir.exists() && !project.fileTree(mainSourceDir, tree -> tree.include("**/*.java")).isEmpty());
        });

        project.getTasks().named("jar", Jar.class).configure(task -> {
            task.dependsOn(compileTask);
            task.from(outputDirectory, copy -> copy.into("META-INF/versions/" + version));
        });
        // Sources jar mirror - 源码包保持与运行时 jar 一致的多版本目录结构。
        project.getTasks().named("sourcesJar", Jar.class).configure(task -> task.from(mainSourceDir, copy -> copy.into("META-INF/versions/" + version)));

        File testSourceDir = project.file("src/test/java" + version);
        if (testSourceDir.exists() && !project.fileTree(testSourceDir, tree -> tree.include("**/*.java")).isEmpty()) {
            Provider<Directory> testOutputDirectory = project.getLayout().getBuildDirectory().dir("classes/java/testMultiRelease/" + version);
            TaskProvider<JavaCompile> compileTestTask = project.getTasks().register("compileTestJava" + version, JavaCompile.class, task -> {
                // Versioned test source - 高版本测试依赖主线输出与对应版本编译产物。
                task.source(project.fileTree(testSourceDir, spec -> spec.include("**/*.java")));
                task.setClasspath(project.files(testSourceSet.getCompileClasspath(), mainSourceSet.getOutput(), outputDirectory));
                task.getDestinationDirectory().set(testOutputDirectory);
                task.getOptions().getRelease().set(version);
                task.getOptions().getCompilerArgs().add("-parameters");
                task.getOptions().setEncoding("UTF-8");
                task.getJavaCompiler().set(toolchains.compilerFor(spec -> spec.getLanguageVersion().set(JavaLanguageVersion.of(toolchainVersion))));
            });
            TaskProvider<Test> testTask = project.getTasks().register("testJava" + version, Test.class, task -> {
                // Versioned test launcher - 测试运行使用与编译一致的 toolchain。
                task.dependsOn(compileTask, compileTestTask);
                task.setTestClassesDirs(project.files(testOutputDirectory));
                task.setClasspath(project.files(testSourceSet.getRuntimeClasspath(), testOutputDirectory, outputDirectory));
                task.getJavaLauncher().set(toolchains.launcherFor(spec -> spec.getLanguageVersion().set(JavaLanguageVersion.of(toolchainVersion))));
                task.useJUnitPlatform();
            });
            project.getTasks().named("check").configure(task -> task.dependsOn(testTask));
        }
    }
}
