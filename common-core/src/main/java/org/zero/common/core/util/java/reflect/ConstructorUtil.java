package org.zero.common.core.util.java.reflect;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/11
 */
@UtilityClass
public class ConstructorUtil extends ReflectUtil {
    /**
     * 获取所有构造器
     *
     * @param clazz 目标类
     * @return 构造器
     */
    public static List<Constructor<?>> getAllConstructors(final Class<?> clazz) {
        return getConstructors(clazz, true);
    }

    /**
     * 获取构造器
     */
    public static List<Constructor<?>> getConstructors(final Class<?> clazz) {
        return getConstructors(clazz, false);
    }

    /**
     * 获取构造器
     *
     * @param clazz                      目标类
     * @param withSuperClassConstructors 是否获取父类的构造器
     * @return 构造器
     */
    public static List<Constructor<?>> getConstructors(final Class<?> clazz, final boolean withSuperClassConstructors) {
        List<Constructor<?>> allConstructors = new ArrayList<>();
        Class<?> searchType = clazz;
        while (Objects.nonNull(searchType)) {
            Constructor<?>[] declaredConstructors = searchType.getDeclaredConstructors();
            allConstructors.addAll(Arrays.asList(declaredConstructors));
            searchType = withSuperClassConstructors ? searchType.getSuperclass() : null;
        }
        return allConstructors;
    }

    /**
     * 获取公共构造器
     *
     * @param clazz 目标类
     * @return 公共构造器
     */
    public static List<Constructor<?>> getPublicConstructors(final Class<?> clazz) {
        return new ArrayList<>(Arrays.asList(clazz.getConstructors()));
    }

    /**
     * 获取封闭构造器（即在构造器内部定义的局部类或匿名类的构造方法）{@link Optional}
     *
     * @param clazz 目标类
     * @return 封闭构造器 {@link Optional}
     */
    public static Optional<Constructor<?>> getEnclosingConstructorOpt(final Class<?> clazz) {
        return Optional.ofNullable(clazz.getEnclosingConstructor());
    }

    /**
     * 获取过滤后的构造器
     * <p>
     * 不包含父类的构造器
     *
     * @param clazz  目标类
     * @param filter 过滤器
     * @return 构造器
     */
    public static List<Constructor<?>> getFilteredConstructors(final Class<?> clazz, final Predicate<Constructor<?>> filter) {
        return getFilteredConstructors(clazz, false, filter);
    }

    /**
     * 获取过滤后的构造器
     *
     * @param clazz                      目标类
     * @param withSuperClassConstructors 是否获取父类的构造器
     * @param filter                     过滤器
     * @return 构造器
     */
    public static List<Constructor<?>> getFilteredConstructors(final Class<?> clazz, final boolean withSuperClassConstructors, final Predicate<Constructor<?>> filter) {
        List<Constructor<?>> declaredConstructors = getConstructors(clazz, withSuperClassConstructors);
        if (Objects.isNull(filter)) {
            return declaredConstructors;
        }
        return declaredConstructors.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    /**
     * 根据参数类型获取构造器 {@link Optional}
     * <p>
     * 不包含父类的构造器
     *
     * @param clazz          目标类
     * @param parameterTypes 参数类型
     * @return 构造器 {@link Optional}
     */
    public static Optional<Constructor<?>> getConstructorOptByParam(final Class<?> clazz, final Class<?>... parameterTypes) {
        return getConstructorOptByParam(clazz, false, parameterTypes);
    }

    /**
     * 根据参数类型获取构造器 {@link Optional}
     *
     * @param clazz                      目标类
     * @param withSuperClassConstructors 是否获取父类的构造器
     * @param parameterTypes             参数类型
     * @return 构造器 {@link Optional}
     */
    public static Optional<Constructor<?>> getConstructorOptByParam(final Class<?> clazz, final boolean withSuperClassConstructors, final Class<?>... parameterTypes) {
        List<Constructor<?>> filteredConstructors = getFilteredConstructors(clazz, withSuperClassConstructors, (constructor -> {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            if (paramTypes.length != parameterTypes.length) {
                return false;
            }
            for (int i = 0; i < paramTypes.length; i++) {
                Class<?> paramType = paramTypes[i];
                Class<?> parameterType = parameterTypes[i];
                // 不是 void 类型进行判断处理，否则跳过该次匹配
                if (!(void.class.equals(parameterType) || Void.class.equals(parameterType))) {
                    // 判断参数类型是否兼容
                    if (!ClassUtil.isAssignable(paramType, parameterType)) {
                        return false;
                    }
                }
            }
            return true;
        }));
        // 优先匹配参数类型完全匹配的构造器
        for (Constructor<?> constructor : filteredConstructors) {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            if (Arrays.equals(paramTypes, parameterTypes)) {
                return Optional.of(constructor);
            }
        }
        // 优先返回可访问的构造器
        return filteredConstructors.stream()
                .min(ACCESSIBLE_COMPARATOR);
    }

    /**
     * 创建实例
     *
     * @param constructor 构造器
     * @param args        参数
     * @param <T>         实例类型
     * @return 实例
     */
    public static <T> T newInstance(final Constructor<T> constructor, final Object... args) {
        return newInstanceOpt(constructor, args).orElse(null);
    }

    /**
     * 创建实例 {@link Optional}
     *
     * @param constructor 构造器
     * @param args        参数
     * @param <T>         实例类型
     * @return 实例 {@link Optional}
     */
    public static <T> Optional<T> newInstanceOpt(final Constructor<T> constructor, final Object... args) {
        if (Objects.isNull(constructor)) {
            return Optional.empty();
        }
        if (!Modifier.isPublic(constructor.getModifiers())) {
            setAccessible(constructor);
        }
        try {
            T object = constructor.newInstance(args);
            return Optional.of(object);
        } catch (Exception ignored) {
            return Optional.empty();
        } finally {
            setInaccessible(constructor);
        }
    }

    /**
     * 创建实例
     *
     * @param clazz 目标类
     * @param args  参数
     * @param <T>   实例类型
     * @return 实例
     */
    public static <T> T newInstance(final Class<T> clazz, final Object... args) {
        return newInstanceOpt(clazz, args).orElse(null);
    }

    /**
     * 创建实例 {@link Optional}
     *
     * @param clazz 目标类
     * @param args  参数
     * @param <T>   实例类型
     * @return 实例 {@link Optional}
     */
    public static <T> Optional<T> newInstanceOpt(final Class<T> clazz, final Object... args) {
        Class<?>[] parameterTypes = Arrays.stream(args)
                .map(o -> {
                    if (Objects.isNull(o)) {
                        return void.class;
                    }
                    return o.getClass();
                })
                .toArray(Class[]::new);
        return getConstructorOptByParam(clazz, parameterTypes).flatMap(constructor -> newInstanceOpt(constructor, args))
                .filter(clazz::isInstance)
                .map(clazz::cast);
    }
}
