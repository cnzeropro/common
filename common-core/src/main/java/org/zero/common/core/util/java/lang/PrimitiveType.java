package org.zero.common.core.util.java.lang;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/19
 */
@Getter
@RequiredArgsConstructor
public enum PrimitiveType {
    /* ********************************************* 基本数据类型 ********************************************* */
    BYTE(byte.class, Byte.class, (byte) 0),
    SHORT(short.class, Short.class, (short) 0),
    INT(int.class, Integer.class, 0),
    LONG(long.class, Long.class, 0L),
    FLOAT(float.class, Float.class, 0.0F),
    DOUBLE(double.class, Double.class, 0.0D),
    CHAR(char.class, Character.class, '\u0000'),
    BOOLEAN(boolean.class, Boolean.class, false),
    /* ********************************************* 特殊类型 ********************************************* */
    VOID(void.class, Void.class, null),
    ;

    private final Class<?> primitiveClass;
    private final Class<?> wrappedClass;
    private final Object defaultValue;

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

    public static Optional<PrimitiveType> getOptByPrimitiveClass(Class<?> primitiveClass) {
        for (PrimitiveType primitiveType : values()) {
            if (primitiveType.primitiveClass == primitiveClass) {
                return Optional.of(primitiveType);
            }
        }
        return Optional.empty();
    }

    public static Optional<PrimitiveType> getOptByWrappedClass(Class<?> wrappedClass) {
        for (PrimitiveType primitiveType : values()) {
            if (primitiveType.wrappedClass == wrappedClass) {
                return Optional.of(primitiveType);
            }
        }
        return Optional.empty();
    }
}
