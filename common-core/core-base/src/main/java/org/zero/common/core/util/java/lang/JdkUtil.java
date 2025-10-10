package org.zero.common.core.util.java.lang;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/29
 */
public class JdkUtil {
    /**
     * Java 版本
     */
    public static final float JAVA_VERSION;
    /**
     * 是否 Android 环境
     */
    public static final boolean IS_ANDROID;


    static {
        JAVA_VERSION = toFloatVersion(SystemPropertyUtil.getJavaVersion());
        IS_ANDROID = "Dalvik".equals(SystemPropertyUtil.getJvmName());
    }

    protected static float toFloatVersion(final String value) {
        final int defaultValue = -1;
        if (value.contains(".")) {
            final String[] toParse = value.split("\\.");
            if (toParse.length >= 2) {
                return NumberUtil.toFloat(toParse[0] + '.' + toParse[1], defaultValue);
            }
        } else {
            return NumberUtil.toFloat(value, defaultValue);
        }
        return defaultValue;
    }

    protected JdkUtil() {
        throw new UnsupportedOperationException();
    }
}
