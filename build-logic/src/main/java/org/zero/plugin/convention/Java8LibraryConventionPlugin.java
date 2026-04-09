package org.zero.plugin.convention;

/**
 * Java 8 library convention - 为 Java 8 库模块复用统一构建约定。
 */
public final class Java8LibraryConventionPlugin extends AbstractJvmConventionPlugin {
    @Override
    protected String basePluginId() {
        return "java-library";
    }

    @Override
    protected int languageVersion() {
        return 8;
    }
}
