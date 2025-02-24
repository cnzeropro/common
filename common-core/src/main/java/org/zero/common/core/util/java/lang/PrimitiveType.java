package org.zero.common.core.util.java.lang;

import lombok.RequiredArgsConstructor;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/19
 */
@RequiredArgsConstructor
public enum PrimitiveType {
    BYTE(byte.class, Byte.class),
    SHORT(short.class, Short.class),
    INT(int.class, Integer.class),
    LONG(long.class, Long.class),
    FLOAT(float.class, Float.class),
    DOUBLE(double.class, Double.class),
    CHAR(char.class, Character.class),
    BOOLEAN(boolean.class, Boolean.class),
    ;

    private final Class<?> primitiveClass;
    private final Class<?> wrappedClass;

    public static Class<?> wrap(Class<?> clazz) {
        if (null == clazz || !clazz.isPrimitive()) {
            return clazz;
        }
        for (PrimitiveType primitiveType : values()) {
            if (primitiveType.primitiveClass == clazz) {
                return primitiveType.wrappedClass;
            }
        }
        return clazz;
    }

    public static Class<?> unwrap(Class<?> clazz) {
        if (null == clazz || clazz.isPrimitive()) {
            return clazz;
        }
        for (PrimitiveType primitiveType : values()) {
            if (primitiveType.wrappedClass == clazz) {
                return primitiveType.primitiveClass;
            }
        }
        return clazz;
    }
}
