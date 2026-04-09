package org.zero.plugin.convention;

/**
 * Java 8 application convention - 为 Java 8 应用模块复用统一构建约定。
 */
public final class Java8ApplicationConventionPlugin extends AbstractJvmConventionPlugin {
    @Override
    protected String basePluginId() {
        return "application";
    }

    @Override
    protected int languageVersion() {
        return 8;
    }

    @Override
    protected boolean publishingEnabled() {
        return false;
    }
}
