package org.zero.common.core.extension.java.lang.reflect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * copy from sun.reflect.generics.reflectiveObjects.GenericArrayTypeImpl
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/27
 */
@RequiredArgsConstructor(staticName = "make")
@Getter
public class GenericArrayTypeImpl implements GenericArrayType {
    private final Type genericComponentType;

    @Override
    public String toString() {
        return getGenericComponentType().getTypeName() + "[]";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GenericArrayType) {
            GenericArrayType that = (GenericArrayType) o;
            return Objects.equals(genericComponentType, that.getGenericComponentType());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(genericComponentType);
    }
}
