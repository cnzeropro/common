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

import java.util.Collections;
import java.util.List;

/**
 * Convention support - 集中封装各模块共享的 Java、测试与发布约定。
 */
public final class ConventionSupport {
    private static final String SLF4J_API_MODULE = "org.slf4j:slf4j-api";
    private static final String SLF4J_BOM_VERSION_ALIAS = "org-slf4j_slf4j-bom";

    private ConventionSupport() {
    }

    public static void applySharedIdentity(Project project) {
        project.setGroup(project.getRootProject().getGroup());
        project.setVersion(project.getRootProject().getVersion());
    }

    public static void configureJava(Project project, int languageVersion) {
        JavaPluginExtension javaExtension = project.getExtensions().getByType(JavaPluginExtension.class);
        configureTestDependencies(project);
        configureEnforcedPlatforms(project);
        configureSlf4jApiOverride(project);
        javaExtension.withJavadocJar();
        javaExtension.withSourcesJar();
        javaExtension.setSourceCompatibility(JavaVersion.toVersion(languageVersion));
        javaExtension.setTargetCompatibility(JavaVersion.toVersion(languageVersion));
        javaExtension.getToolchain().getLanguageVersion().set(JavaLanguageVersion.of(languageVersion));

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
        project.getDependencies().add(
            JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME,
            project.getDependencies().platform(junitBom.get())
        );
        project.getDependencies().add(JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME, "org.junit.platform:junit-platform-launcher");
    }

    private static void configureEnforcedPlatforms(Project project) {
        VersionCatalog libraries = project.getExtensions().getByType(VersionCatalogsExtension.class).named("libs");
        for (String alias : enforcedPlatformAliases(project)) {
            Provider<MinimalExternalModuleDependency> library = requiredLibrary(libraries, alias);
            project.getDependencies().add(
                JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME,
                project.getDependencies().enforcedPlatform(library.get())
            );
            project.getDependencies().add(
                JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME,
                project.getDependencies().enforcedPlatform(library.get())
            );
            project.getDependencies().add(
                JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME,
                project.getDependencies().enforcedPlatform(library.get())
            );
        }
    }

    private static void configureSlf4jApiOverride(Project project) {
        VersionCatalog libraries = project.getExtensions().getByType(VersionCatalogsExtension.class).named("libs");
        String slf4jVersion = libraries.findVersion(SLF4J_BOM_VERSION_ALIAS)
            .orElseThrow(() -> new IllegalStateException("Missing Gradle catalog version '" + SLF4J_BOM_VERSION_ALIAS + "'."))
            .getRequiredVersion();

        project.getConfigurations().configureEach(configuration -> {
            if (!configuration.isCanBeResolved()) {
                return;
            }
            configuration.getResolutionStrategy().force(SLF4J_API_MODULE + ":" + slf4jVersion);
        });
    }

    private static void configureCompileTask(JavaCompile task, int languageVersion) {
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

    @SuppressWarnings("unchecked")
    private static List<String> enforcedPlatformAliases(Project project) {
        if (!project.getRootProject().getExtensions().getExtraProperties().has("enforcedPlatformAliases")) {
            return Collections.emptyList();
        }
        Object aliases = project.getRootProject().getExtensions().getExtraProperties().get("enforcedPlatformAliases");
        if (aliases instanceof List) {
            return (List<String>) aliases;
        }
        return Collections.emptyList();
    }

    public static void configurePublishing(Project project) {
        PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
        configurePublishingRepositories(project, publishing);
        if (publishing.getPublications().findByName("mavenJava") != null || project.getComponents().findByName("java") == null) {
            return;
        }
        publishing.getPublications().create("mavenJava", MavenPublication.class, publication -> {
            publication.from(project.getComponents().getByName("java"));
            publication.setArtifactId(project.getName());
        });
    }

    private static void configurePublishingRepositories(Project project, PublishingExtension publishing) {
        if (publishing.getRepositories().findByName("localStagingRepository") != null) {
            return;
        }
        publishing.getRepositories().maven(repository -> {
            repository.setName("localStagingRepository");
            repository.setUrl(project.getRootProject().getLayout().getBuildDirectory().dir("mvn-repo"));
        });
    }
}
