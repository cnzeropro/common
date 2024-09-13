package org.zero.common.core.util.java;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/11/29
 */
public abstract class TypeReference<T> implements Type {
    /**
     * 泛型参数
     */
    private final Type type;

    protected TypeReference() {
        this.type = getTypeArgument(getClass());
    }

    public Type getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return this.type.toString();
    }

    /**
     * 获得给定类的第一个泛型参数
     */
    public static Type getTypeArgument(Type type) {
        return getTypeArgument(type, 0);
    }

    /**
     * 获得给定类的泛型参数
     */
    public static Type getTypeArgument(Type type, int index) {
        final Type[] typeArguments = getTypeArguments(type);
        if (typeArguments.length > index) {
            return typeArguments[index];
        }
        return null;
    }

    /**
     * 获得指定类型中所有泛型参数类型，例如：
     *
     * <pre>
     * class CommonCellData&lt;T&gt;
     * class B extends CommonCellData&lt;String&gt;
     * </pre>
     * <p>
     * 通过此方法，传入B.class即可得到String
     */
    public static Type[] getTypeArguments(Type type) {
        if (Objects.isNull(type)) {
            return new Type[0];
        }

        final ParameterizedType parameterizedType = getParameterizedType(type);
        return Objects.isNull(parameterizedType) ? new Type[0] : parameterizedType.getActualTypeArguments();
    }

    /**
     * 将{@link Type} 转换为{@link ParameterizedType}<br>
     * {@link ParameterizedType}用于获取当前类或父类中泛型参数化后的类型<br>
     * 一般用于获取泛型参数具体的参数类型，例如：
     *
     * <pre>
     * class CommonCellData&lt;T&gt;
     * class B extends CommonCellData&lt;String&gt;
     * </pre>
     * <p>
     * 通过此方法，传入B.class即可得到B的{@link ParameterizedType}，从而获取到String
     */
    private static ParameterizedType getParameterizedType(Type type) {
        ParameterizedType result = null;
        if (type instanceof ParameterizedType) {
            result = (ParameterizedType) type;
        } else if (type instanceof Class) {
            final Class<?> clazz = (Class<?>) type;
            Type genericSuperclass = clazz.getGenericSuperclass();
            // 如果类没有父类，而是实现一些定义好的泛型接口，则取接口的Type
            if (Objects.isNull(genericSuperclass) || Object.class.equals(genericSuperclass)) {
                final Type[] genericInterfaces = clazz.getGenericInterfaces();
                if (genericInterfaces.length > 0) {
                    // 默认取第一个实现接口的泛型Type
                    genericSuperclass = genericInterfaces[0];
                }
            }
            result = getParameterizedType(genericSuperclass);
        }
        return result;
    }
}
