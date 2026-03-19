package org.zero.plugin.convention;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Abstract JVM convention plugin - 按模块类型与 Java 版本装配统一构建基线。
 */
public abstract class AbstractJvmConventionPlugin implements Plugin<Project> {
    @Override
    public final void apply(Project project) {
        project.getPluginManager().apply(basePluginId());

        /*
         * shared convention chain - 统一收口身份、Java、测试与发布约定，具体插件只声明差异维度。
         */
        ConventionSupport.applySharedIdentity(project);
        ConventionSupport.configureJava(project, languageVersion());
        if (publishingEnabled()) {
            project.getPluginManager().apply("maven-publish");
            ConventionSupport.configurePublishing(project);
        }
        configureAdditionalConventions(project);
    }

    protected abstract String basePluginId();

    protected abstract int languageVersion();

    protected boolean publishingEnabled() {
        return true;
    }

    protected void configureAdditionalConventions(Project project) {
        // Default no-op - 默认插件无需附加行为。
    }
}
