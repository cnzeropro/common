package org.zero.plugin.convention;

import java.util.Collections;

import org.gradle.api.Project;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.bundling.Jar;
import org.gradle.api.tasks.compile.JavaCompile;
import org.gradle.api.tasks.testing.Test;

/**
 * Aggregate distribution convention - 为聚合分发模块关闭本地源码生命周期，只保留装配行为。
 */
public final class AggregateDistributionConventionPlugin extends AbstractJvmConventionPlugin {
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
        /*
         * empty source sets - 当前模块不维护真实源码，jar 内容完全来自后续聚合步骤。
         */
        mainSourceSet.getJava().setSrcDirs(Collections.emptyList());
        mainSourceSet.getResources().setSrcDirs(Collections.emptyList());
        testSourceSet.getJava().setSrcDirs(Collections.emptyList());
        testSourceSet.getResources().setSrcDirs(Collections.emptyList());

        /*
         * disable local lifecycle - 关闭 compile/test，避免无源码模块触发空任务链。
         */
        project.getTasks().named("compileJava", JavaCompile.class).configure(task -> task.setEnabled(false));
        project.getTasks().named("compileTestJava", JavaCompile.class).configure(task -> task.setEnabled(false));
        project.getTasks().named("test", Test.class).configure(task -> task.setEnabled(false));
        project.getTasks().named("sourcesJar", Jar.class).configure(task -> task.setEnabled(false));
        project.getTasks().named("javadocJar", Jar.class).configure(task -> task.setEnabled(false));
        project.getTasks().named("jar", Jar.class).configure(task -> task.setDuplicatesStrategy(DuplicatesStrategy.EXCLUDE));
    }
}
