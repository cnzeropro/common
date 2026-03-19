package org.zero.plugin.convention;

/**
 * Java 17 application convention - 为 Java 17 应用模块复用统一构建约定。
 */
public final class Java17ApplicationConventionPlugin extends AbstractJvmConventionPlugin {
    @Override
    protected String basePluginId() {
        return "application";
    }

    @Override
    protected int languageVersion() {
        return 17;
    }

    @Override
    protected boolean publishingEnabled() {
        return false;
    }
}
