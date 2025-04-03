package org.zero.common.core.util.java.reflect;

import java.lang.reflect.Executable;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/1
 */
public class ExecutableUtil {
    /**
     * 获取方法或者构造器全限定名
     *
     * @param executable 方法或构造器
     * @return 全限定名
     */
    public static String getFullName(Executable executable) {
        // 类的全限定名
        String className = executable.getDeclaringClass().getName();
        // 方法名
        String executableName = executable.getName();
        // 参数类型列表（全限定名）
        String params = Arrays.stream(executable.getParameterTypes())
                .map(Class::getName)
                .collect(Collectors.joining(", "));
        return String.format("%s.%s(%s)", className, executableName, params);
    }

    protected ExecutableUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
