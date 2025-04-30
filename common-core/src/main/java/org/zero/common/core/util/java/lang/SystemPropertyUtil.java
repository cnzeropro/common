package org.zero.common.core.util.java.lang;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/29
 */
public class SystemPropertyUtil implements SystemProperties {
    public static String getOsName() {
        return System.getProperty(OS_NAME);
    }

    public static String getJvmName(){
        return System.getProperty(JAVA_VM_NAME);
    }

    public static String getJavaVersion() {
        return System.getProperty(JAVA_VERSION);
    }

    protected SystemPropertyUtil() {
        throw new UnsupportedOperationException();
    }
}
