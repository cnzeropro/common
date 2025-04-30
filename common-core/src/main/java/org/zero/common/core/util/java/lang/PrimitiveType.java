package org.zero.common.core.util.java.lang;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.zero.common.data.constant.ConstantPool;

import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/19
 */
@Getter
@RequiredArgsConstructor
public enum PrimitiveType {
    /* ********************************************* 基本数据类型 ********************************************* */
    BYTE(byte.class, Byte.class, ConstantPool.BYTE_ZERO),
    SHORT(short.class, Short.class, ConstantPool.SHORT_ZERO),
    INT(int.class, Integer.class, ConstantPool.INT_ZERO),
    LONG(long.class, Long.class, ConstantPool.LONG_ZERO),
    FLOAT(float.class, Float.class, ConstantPool.FLOAT_ZERO),
    DOUBLE(double.class, Double.class, ConstantPool.DOUBLE_ZERO),
    CHAR(char.class, Character.class, ConstantPool.CHAR_ZERO),
    BOOLEAN(boolean.class, Boolean.class, ConstantPool.BOOLEAN_FALSE),
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
