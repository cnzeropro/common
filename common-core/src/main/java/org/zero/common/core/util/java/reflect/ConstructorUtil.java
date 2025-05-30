package org.zero.common.core.util.java.reflect;

import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.zero.common.core.util.java.reflect.MemberUtil.ACCESSIBLE_COMPARATOR;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/11
 */
public class ConstructorUtil {
    /**
     * 获取所有构造器
     *
     * @param clazz 目标类
     * @return 构造器
     */
    public static Collection<Constructor<?>> getAllConstructors(final Class<?> clazz) {
        return getConstructors(clazz, true);
    }

    /**
     * 获取构造器
     */
    public static Collection<Constructor<?>> getConstructors(final Class<?> clazz) {
        return getConstructors(clazz, false);
    }

    /**
     * 获取构造器
     *
     * @param clazz                      目标类
     * @param withSuperClassConstructors 是否获取父类的构造器
     * @return 构造器
     */
    public static Collection<Constructor<?>> getConstructors(final Class<?> clazz, final boolean withSuperClassConstructors) {
        Collection<Constructor<?>> allConstructors = new ArrayList<>();
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
    public static Collection<Constructor<?>> getPublicConstructors(final Class<?> clazz) {
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
    public static Collection<Constructor<?>> getFilteredConstructors(final Class<?> clazz, final Predicate<? super Constructor<?>> filter) {
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
    public static Collection<Constructor<?>> getFilteredConstructors(final Class<?> clazz, final boolean withSuperClassConstructors, final Predicate<? super Constructor<?>> filter) {
        Collection<Constructor<?>> declaredConstructors = getConstructors(clazz, withSuperClassConstructors);
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
        Collection<Constructor<?>> filteredConstructors = getFilteredConstructors(clazz, withSuperClassConstructors, ExecutableUtil.AvailableParameterTypePredicate.of(parameterTypes));
        // 优先匹配参数类型完全匹配的构造器
        for (Constructor<?> constructor : filteredConstructors) {
            if (ExecutableUtil.ExactParameterTypePredicate.of(parameterTypes).test(constructor)) {
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
        return newInstance(constructor, false, args);
    }

    /**
     * 创建实例
     *
     * @param constructor      构造器
     * @param quietIfException 是否安静处理（不抛出异常）
     * @param args             参数
     * @param <T>              实例类型
     * @return 实例
     */
    public static <T> T newInstance(final Constructor<T> constructor, final boolean quietIfException, final Object... args) {
        return newInstanceOpt(constructor, quietIfException, args).orElse(null);
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
        return newInstanceOpt(constructor, true, args);
    }

    /**
     * 创建实例 {@link Optional}
     *
     * @param constructor      构造器
     * @param quietIfException 是否安静处理（不抛出异常）
     * @param args             参数
     * @param <T>              实例类型
     * @return 实例 {@link Optional}
     */
    public static <T> Optional<T> newInstanceOpt(final Constructor<T> constructor, final boolean quietIfException, final Object... args) {
        if (Objects.isNull(constructor)) {
            return Optional.empty();
        }
        if (!Modifier.isPublic(constructor.getModifiers())) {
            AccessibleObjectUtil.setAccessible(constructor);
        }
        try {
            T object = constructor.newInstance(args);
            return Optional.of(object);
        } catch (Exception e) {
            if (quietIfException) {
                return Optional.empty();
            }
            throw ThrowableUtil.throwUnchecked(e);
        } finally {
            AccessibleObjectUtil.setInaccessible(constructor);
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
        return newInstance(clazz, false, args);
    }

    /**
     * 创建实例
     *
     * @param clazz            目标类
     * @param quietIfException 是否安静处理（不抛出异常）
     * @param args             参数
     * @param <T>              实例类型
     * @return 实例
     */
    public static <T> T newInstance(final Class<T> clazz, final boolean quietIfException, final Object... args) {
        return newInstanceOpt(clazz, quietIfException, args).orElse(null);
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
        return newInstanceOpt(clazz, true, args);
    }

    /**
     * 创建实例 {@link Optional}
     *
     * @param clazz            目标类
     * @param quietIfException 是否安静处理（不抛出异常）
     * @param args             参数
     * @param <T>              实例类型
     * @return 实例 {@link Optional}
     */
    public static <T> Optional<T> newInstanceOpt(final Class<T> clazz, final boolean quietIfException, final Object... args) {
        Class<?>[] parameterTypes = ExecutableUtil.getParameterTypes(args);
        return getConstructorOptByParam(clazz, parameterTypes).map(constructor -> newInstance(constructor, quietIfException, args))
                .filter(clazz::isInstance)
                .map(clazz::cast);
    }

    /**
     * @see MemberUtil#getInstanceOpt(Class, boolean, Object...)
     */
    @SuppressWarnings("unchecked")
    protected static <T> Collection<Constructor<T>> getInstanceConstructors(Class<T> clazz, Class<?>... parameterTypes) {
        Stream<Constructor<?>> constructorStream = getConstructors(clazz, false).stream();
        if (Objects.nonNull(parameterTypes)) {
            constructorStream = constructorStream.filter(ExecutableUtil.AvailableParameterTypePredicate.of(parameterTypes));
        }
        return (Collection<Constructor<T>>) (Object) constructorStream.collect(Collectors.toList());
    }

    protected ConstructorUtil() {
        throw new UnsupportedOperationException();
    }
}
