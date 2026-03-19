package org.zero.plugin.convention;

import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.api.artifacts.VersionCatalog;
import org.gradle.api.artifacts.VersionCatalogsExtension;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.provider.Provider;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.Delete;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.logging.TestLogEvent;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;
import org.gradle.jvm.toolchain.JavaLanguageVersion;

/**
 * Convention support - 集中封装各模块共享的 Java、测试与发布约定。
 */
public final class ConventionSupport {
    private ConventionSupport() {
    }

    public static void applySharedIdentity(Project project) {
        project.setGroup(project.getRootProject().getGroup());
        project.setVersion(project.getRootProject().getVersion());
    }

    public static void configureJava(Project project, int languageVersion) {
        JavaPluginExtension javaExtension = project.getExtensions().getByType(JavaPluginExtension.class);
        configureTestDependencies(project);
        /*
         * standard artifacts - 统一生成 sources/javadoc jar，减少各模块重复配置。
         */
        javaExtension.withJavadocJar();
        javaExtension.withSourcesJar();
        javaExtension.setSourceCompatibility(JavaVersion.toVersion(languageVersion));
        javaExtension.setTargetCompatibility(JavaVersion.toVersion(languageVersion));
        javaExtension.getToolchain().getLanguageVersion().set(JavaLanguageVersion.of(languageVersion));

        /*
         * compile task wiring - 主源码与测试源码共用统一的编码、release 与参数名配置。
         */
        project.getTasks().named(JavaPlugin.COMPILE_JAVA_TASK_NAME, JavaCompile.class)
            .configure(task -> configureCompileTask(task, languageVersion));
        project.getTasks().named(JavaPlugin.COMPILE_TEST_JAVA_TASK_NAME, JavaCompile.class)
            .configure(task -> configureCompileTask(task, languageVersion));
        project.getTasks().named(JavaPlugin.JAVADOC_TASK_NAME, Javadoc.class).configure(task -> {
            task.setFailOnError(false);
            StandardJavadocDocletOptions options = (StandardJavadocDocletOptions) task.getOptions();
            options.addStringOption("Xdoclint:none", "-quiet");
            options.setEncoding("UTF-8");
        });
        project.getTasks().named("clean", Delete.class).configure(task -> task.setFollowSymlinks(true));
        project.getTasks().named(JavaPlugin.TEST_TASK_NAME, Test.class).configure(task -> {
            /*
             * shared JUnit 5 policy - 统一测试匹配规则与日志输出，保持模块间行为一致。
             */
            task.useJUnitPlatform();
            task.filter(filter -> {
                filter.includeTestsMatching("*Test");
                filter.includeTestsMatching("*Tests");
                filter.includeTestsMatching("*Spec");
            });
            task.getTestLogging().setShowStandardStreams(true);
            task.getTestLogging().events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED);
        });
    }

    private static void configureTestDependencies(Project project) {
        VersionCatalog libraries = project.getExtensions().getByType(VersionCatalogsExtension.class).named("libs");
        Provider<MinimalExternalModuleDependency> junitBom = requiredLibrary(libraries, "org-junit_junit-bom");

        project.getDependencies().add(
            JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME,
            project.getDependencies().platform(junitBom.get())
        );
        project.getDependencies().add(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME, "org.junit.jupiter:junit-jupiter");
    }

    private static void configureCompileTask(JavaCompile task, int languageVersion) {
        /*
         * legacy compatibility - Java 8 及以下继续沿用 1.x 写法，高版本统一走 --release。
         */
        if (languageVersion <= 8) {
            String compatibilityVersion = "1." + languageVersion;
            task.setSourceCompatibility(compatibilityVersion);
            task.setTargetCompatibility(compatibilityVersion);
        } else {
            task.getOptions().getRelease().set(languageVersion);
        }
        task.getOptions().getCompilerArgs().add("-parameters");
        task.getOptions().setEncoding("UTF-8");
    }

    private static Provider<MinimalExternalModuleDependency> requiredLibrary(VersionCatalog catalog, String alias) {
        return catalog.findLibrary(alias).orElseThrow(() -> new IllegalStateException("Missing Gradle catalog alias '" + alias + "'."));
    }

    public static void configurePublishing(Project project) {
        PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
        /*
         * idempotent publication - 已存在 mavenJava 或缺少 java component 时不重复注册。
         */
        if (publishing.getPublications().findByName("mavenJava") != null || project.getComponents().findByName("java") == null) {
            return;
        }
        publishing.getPublications().create("mavenJava", MavenPublication.class, publication -> {
            publication.from(project.getComponents().getByName("java"));
            publication.setArtifactId(project.getName());
        });
    }
}
