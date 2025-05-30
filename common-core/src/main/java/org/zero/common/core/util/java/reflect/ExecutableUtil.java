package org.zero.common.core.util.java.reflect;

import java.lang.reflect.Executable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/1
 */
public class ExecutableUtil {
    /**
     * 获取参数类型
     * <p>
     * 当参数列表中参数为 null 时，其类型也为 null（注意 NPE）。
     *
     * @param args 参数列表
     * @return 参数类型列表
     * @see AvailableParameterTypePredicate
     */
    public static Class<?>[] getParameterTypes(Object... args) {
        if (Objects.isNull(args)) {
            return new Class[0];
        }
        return Arrays.stream(args)
                .map(o -> {
                    if (Objects.isNull(o)) {
                        return null;
                    }
                    return o.getClass();
                })
                .toArray(Class[]::new);
    }

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

    public static class ExactParameterTypePredicate implements Predicate<Executable> {
        protected final Class<?>[] parameterTypes;

        protected ExactParameterTypePredicate(Class<?>... parameterTypes) {
            this.parameterTypes = parameterTypes;
        }

        public static ExactParameterTypePredicate of(Class<?>... parameterTypes) {
            return new ExactParameterTypePredicate(parameterTypes);
        }

        @Override
        public boolean test(Executable executable) {
            Class<?>[] paramTypes = executable.getParameterTypes();
            if (paramTypes.length != parameterTypes.length) {
                return false;
            }
            // 此处不使用 Arrays.equals 方法，否则无法做到 null 处理
            for (int i = 0; i < paramTypes.length; i++) {
                Class<?> paramType = paramTypes[i];
                Class<?> parameterType = parameterTypes[i];
                // 不为 null 时进行判断处理，否则跳过该次匹配
                if (Objects.nonNull(parameterType)) {
                    // 判断参数类型是否相等
                    if (!Objects.equals(paramType, parameterType)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    public static class AvailableParameterTypePredicate implements Predicate<Executable> {
        protected final Class<?>[] parameterTypes;

        protected AvailableParameterTypePredicate(Class<?>... parameterTypes) {
            this.parameterTypes = parameterTypes;
        }

        public static AvailableParameterTypePredicate of(Class<?>... parameterTypes) {
            return new AvailableParameterTypePredicate(parameterTypes);
        }

        @Override
        public boolean test(Executable executable) {
            Class<?>[] paramTypes = executable.getParameterTypes();
            if (paramTypes.length != parameterTypes.length) {
                return false;
            }
            for (int i = 0; i < paramTypes.length; i++) {
                Class<?> paramType = paramTypes[i];
                Class<?> parameterType = parameterTypes[i];
                // 不为 null 时进行判断处理，否则跳过该次匹配
                if (Objects.nonNull(parameterType)) {
                    // 判断参数类型是否兼容
                    if (!ClassUtil.isAssignable(paramType, parameterType)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    protected ExecutableUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
