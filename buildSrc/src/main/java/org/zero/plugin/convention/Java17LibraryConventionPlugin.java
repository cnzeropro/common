package org.zero.plugin.convention;

/**
 * Java 17 library convention - 为 Java 17 库模块复用统一构建约定。
 */
public final class Java17LibraryConventionPlugin extends AbstractJvmConventionPlugin {
    @Override
    protected String basePluginId() {
        return "java-library";
    }

    @Override
    protected int languageVersion() {
        return 17;
    }
}
