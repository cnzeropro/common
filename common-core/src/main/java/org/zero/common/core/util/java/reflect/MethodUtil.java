package org.zero.common.core.util.java.reflect;

import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
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
public class MethodUtil {
    public static final String SETTER_METHOD_PREFIX = "set";
    public static final String GETTER_METHOD_PREFIX = "get";
    public static final String BOOL_GETTER_METHOD_PREFIX = "is";
    public static final String BUILD_METHOD_NAME = "build";
    public static final String OF_METHOD_NAME = "of";
    public static final String FROM_METHOD_NAME = "from";
    public static final String CREATE_METHOD_NAME = "from";
    public static final String BUILDER_METHOD_NAME = "builder";
    public static final String NEW_INSTANCE_METHOD_NAME = "newInstance";
    public static final String GET_INSTANCE_METHOD_NAME = "getInstance";

    /**
     * 获取所有方法
     *
     * @param clazz 目标类
     * @return 方法
     */
    public static Collection<Method> getAllMethods(final Class<?> clazz) {
        return getMethods(clazz, true);
    }

    /**
     * 获取方法
     *
     * @param clazz 目标类
     * @return 方法
     */
    public static Collection<Method> getMethods(final Class<?> clazz) {
        return getMethods(clazz, false);
    }

    /**
     * 获取方法
     *
     * @param clazz                 目标类
     * @param withSuperClassMethods 是否获取父类的方法
     * @return 方法
     */
    public static Collection<Method> getMethods(final Class<?> clazz, final boolean withSuperClassMethods) {
        Collection<Method> allMethods = new ArrayList<>();
        Class<?> searchType = clazz;
        while (Objects.nonNull(searchType)) {
            Method[] declaredMethods = searchType.getDeclaredMethods();
            allMethods.addAll(Arrays.asList(declaredMethods));
            searchType = withSuperClassMethods ? searchType.getSuperclass() : null;
        }
        return allMethods;
    }


    /**
     * 获取公共方法
     *
     * @param clazz 目标类
     * @return 公共方法
     */
    public static Collection<Method> getPublicMethods(final Class<?> clazz) {
        return new ArrayList<>(Arrays.asList(clazz.getMethods()));
    }

    /**
     * 获取封闭方法（即包含该类的方法）{@link Optional}
     *
     * @param clazz 目标类
     * @return 封闭方法 {@link Optional}
     */
    public static Optional<Method> getEnclosingMethodOpt(final Class<?> clazz) {
        return Optional.ofNullable(clazz.getEnclosingMethod());
    }

    /**
     * 获取过滤后的方法
     * <p>
     * 包含父类的方法
     *
     * @param clazz  目标类
     * @param filter 过滤器
     * @return 方法
     */
    public static Collection<Method> getFilteredMethods(final Class<?> clazz, final Predicate<? super Method> filter) {
        return getFilteredMethods(clazz, true, filter);
    }

    /**
     * 获取过滤后的方法
     *
     * @param clazz                 目标类
     * @param withSuperClassMethods 是否获取父类的方法
     * @param filter                过滤器
     * @return 方法
     */
    public static Collection<Method> getFilteredMethods(final Class<?> clazz, final boolean withSuperClassMethods, final Predicate<? super Method> filter) {
        final Collection<Method> methods = getMethods(clazz, withSuperClassMethods);
        if (Objects.isNull(filter)) {
            return methods;
        }
        return methods.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    /**
     * 获取静态方法
     * <p>
     * 包含父类的方法
     *
     * @param clazz 目标类
     * @return 静态方法
     */
    public static Collection<Method> getStaticMethods(final Class<?> clazz) {
        return getStaticMethods(clazz, true);
    }

    /**
     * 获取静态方法
     *
     * @param clazz                 目标类
     * @param withSuperClassMethods 是否获取父类的方法
     * @return 静态方法
     */
    public static Collection<Method> getStaticMethods(final Class<?> clazz, final boolean withSuperClassMethods) {
        return getFilteredMethods(clazz, withSuperClassMethods, method -> Modifier.isStrict(method.getModifiers()));
    }

    /**
     * 通过方法名获取方法
     * <p>
     * 包含父类的方法
     *
     * @param clazz      目标类
     * @param methodName 方法名
     * @return 方法
     */
    public static Collection<Method> getMethodsByName(final Class<?> clazz, final CharSequence methodName) {
        return getMethodsByName(clazz, true, methodName);
    }

    /**
     * 通过方法名获取方法
     *
     * @param clazz                 目标类
     * @param withSuperClassMethods 是否获取父类的方法
     * @param methodName            方法名
     * @return 方法
     */
    public static Collection<Method> getMethodsByName(final Class<?> clazz, final boolean withSuperClassMethods, final CharSequence methodName) {
        return getFilteredMethods(clazz, withSuperClassMethods, method -> Objects.equals(methodName, method.getName()));
    }

    /**
     * 通过方法名和参数类型获取方法 {@link Optional}
     * <p>
     * 包含父类的方法
     *
     * @param clazz          目标类
     * @param methodName     方法名
     * @param parameterTypes 参数类型
     * @return 方法
     */
    public static Method getMethodByNameAndParam(final Class<?> clazz, final CharSequence methodName, final Class<?>... parameterTypes) {
        return getMethodByNameAndParam(clazz, true, methodName, parameterTypes);
    }

    /**
     * 通过方法名和参数类型获取方法 {@link Optional}
     *
     * @param clazz                 目标类
     * @param withSuperClassMethods 是否获取父类的方法
     * @param methodName            方法名
     * @param parameterTypes        参数类型
     * @return 方法
     */
    public static Method getMethodByNameAndParam(final Class<?> clazz, final boolean withSuperClassMethods, final CharSequence methodName, final Class<?>... parameterTypes) {
        return getMethodOptByNameAndParam(clazz, withSuperClassMethods, methodName, parameterTypes).orElse(null);
    }

    /**
     * 通过方法名和参数类型获取方法 {@link Optional}
     * <p>
     * 包含父类的方法
     *
     * @param clazz          目标类
     * @param methodName     方法名
     * @param parameterTypes 参数类型
     * @return 方法 {@link Optional}
     */
    public static Optional<Method> getMethodOptByNameAndParam(final Class<?> clazz, final CharSequence methodName, final Class<?>... parameterTypes) {
        return getMethodOptByNameAndParam(clazz, true, methodName, parameterTypes);
    }

    /**
     * 通过方法名和参数类型获取方法 {@link Optional}
     *
     * @param clazz                 目标类
     * @param withSuperClassMethods 是否获取父类的方法
     * @param methodName            方法名
     * @param parameterTypes        参数类型
     * @return 方法 {@link Optional}
     */
    public static Optional<Method> getMethodOptByNameAndParam(final Class<?> clazz, final boolean withSuperClassMethods, final CharSequence methodName, final Class<?>... parameterTypes) {
        Collection<Method> filteredMethods = getMethodsByName(clazz, withSuperClassMethods, methodName).stream()
                .filter(ExecutableUtil.ParameterTypePredicate.of(parameterTypes))
                .collect(Collectors.toList());
        // 优先匹配参数类型完全匹配的方法
        for (Method method : filteredMethods) {
            Class<?>[] paramTypes = method.getParameterTypes();
            if (Arrays.equals(paramTypes, parameterTypes)) {
                return Optional.of(method);
            }
        }
        // 优先返回可访问的方法
        return filteredMethods.stream()
                .min(ACCESSIBLE_COMPARATOR);
    }

    public static boolean has(final Class<?> clazz, final CharSequence methodName) {
        return !getMethodsByName(clazz, false, methodName).isEmpty();
    }

    public static boolean has(final Class<?> clazz, final CharSequence methodName, final Class<?>... parameterTypes) {
        return getMethodOptByNameAndParam(clazz, false, methodName, parameterTypes).isPresent();
    }

    public static Object invokeStatic(final Method method, final Object... args) {
        return invokeStatic(method, false, args);
    }

    public static Object invokeStatic(final Method method, final boolean quietIfException, final Object... args) {
        return invokeStatic(method, Object.class, false, args);
    }

    public static <T> T invokeStatic(final Method method, final Type type, final Object... args) {
        return invokeStatic(method, type, false, args);
    }

    public static <T> T invokeStatic(final Method method, final Type type, boolean quietIfException, final Object... args) {
        return MethodUtil.<T>invokeStaticOpt(method, type, quietIfException, args).orElse(null);
    }

    public static <T> Optional<T> invokeStaticOpt(final Method method, final Type type, final Object... args) {
        return invokeStaticOpt(method, type, true, args);
    }

    public static <T> Optional<T> invokeStaticOpt(final Method method, final Type type, final boolean quietIfException, final Object... args) {
        return invokeOpt(method, null, type, quietIfException, args);
    }

    public static Object invoke(final Method method, final Object target, final Object... args) {
        return invoke(method, target, false, args);
    }

    public static Object invoke(final Method method, final Object target, final boolean quietIfException, final Object... args) {
        return invokeOpt(method, target, quietIfException, args).orElse(null);
    }

    public static <T> T invoke(final Method method, final Object target, final Type type, final Object... args) {
        return invoke(method, target, type, false, args);
    }

    public static <T> T invoke(final Method method, final Object target, final Type type, final boolean quietIfException, final Object... args) {
        return MethodUtil.<T>invokeOpt(method, target, type, quietIfException, args).orElse(null);
    }

    public static Optional<Object> invokeOpt(final Method method, final Object target, final Object... args) {
        return invokeOpt(method, target, true, args);
    }

    public static Optional<Object> invokeOpt(final Method method, final Object target, final boolean quietIfException, final Object... args) {
        return invokeOpt(method, target, Object.class, quietIfException, args);
    }

    public static <T> Optional<T> invokeOpt(final Method method, final Object target, final Type type, final Object... args) {
        return invokeOpt(method, target, type, true, args);
    }

    public static <T> Optional<T> invokeOpt(final Method method, final Object target, final Type type, final boolean quietIfException, final Object... args) {
        if (Objects.isNull(method)) {
            return Optional.empty();
        }
        int mod = method.getModifiers();
        Object obj = target;
        if (Modifier.isStatic(mod)) {
            obj = null;
        }
        if (!Modifier.isPublic(mod)) {
            AccessibleObjectUtil.setAccessible(method);
        }
        try {
            Object invoked = method.invoke(obj, args);
            T result = ClassUtil.cast(invoked, type);
            return Optional.ofNullable(result);
        } catch (Exception e) {
            if (quietIfException) {
                return Optional.empty();
            }
            throw ThrowableUtil.throwUnchecked(e);
        } finally {
            AccessibleObjectUtil.setInaccessible(method);
        }
    }


    public static <T, R> Optional<R> invokeStaticOpt(final Class<T> clazz, CharSequence methodName, final Type type, final Object... args) {
        // 静态方法不能被重写，因此不需要获取父类的方法
        return invokeOpt(clazz, false, null, methodName, type, true, args);
    }

    public static <T, R> Optional<R> invokeStaticOpt(final Class<T> clazz, CharSequence methodName, final Type type, final boolean quietIfException, final Object... args) {
        return invokeOpt(clazz, false, null, methodName, type, quietIfException, args);
    }

    public static <T, R> Optional<R> invokeOpt(final Class<T> clazz, final boolean withSuperClassMethods, final T target, CharSequence methodName, final Type type, final Object... args) {
        return invokeOpt(clazz, withSuperClassMethods, target, methodName, type, true, args);
    }

    public static <T, R> Optional<R> invokeOpt(final Class<T> clazz, final boolean withSuperClassMethods, final T target, CharSequence methodName, final Type type, boolean quietIfException, final Object... args) {
        Class<?>[] parameterTypes = ExecutableUtil.getParameterTypes(args);
        // 当 clazz 为 null 时，默认使用 target 的类对象
        // 当 target 是 null 时，默认其为静态方法，而静态方法不能被重写，因此不需要获取父类的方法
        return getMethodOptByNameAndParam(Objects.isNull(clazz) ? target.getClass() : clazz, Objects.nonNull(target) && withSuperClassMethods, methodName, parameterTypes).flatMap(method -> invokeOpt(method, target, type, quietIfException, args));
    }

    /**
     * 判断是否为 Getter 方法
     *
     * @param method     方法
     * @param ignoreCase 是否忽略大小写
     * @return 是否为 Getter 方法
     */
    public static boolean isGetter(final Method method, boolean ignoreCase) {
        if (Objects.isNull(method)) {
            return false;
        }

        // 参数个数必须为0
        final int parameterCount = method.getParameterCount();
        if (parameterCount > 0) {
            return false;
        }

        // 返回值类型不能为 void
        final Class<?> returnType = method.getReturnType();
        if (Void.TYPE.equals(returnType)) {
            return false;
        }

        String methodName = method.getName();
        // 跳过getClass这个特殊方法
        if ("getClass".equals(methodName)) {
            return false;
        }

        if (ignoreCase) {
            methodName = methodName.toLowerCase();
        }

        return methodName.startsWith(GETTER_METHOD_PREFIX) || methodName.startsWith(BOOL_GETTER_METHOD_PREFIX);
    }

    /**
     * 通过字段获取其对应的 Getter 方法
     * <p>
     * 包含父类的方法<br>
     * {@link Boolean} 类型字段不使用 is 前缀（使用 get 前缀）
     *
     * @param clazz 目标类
     * @param field 字段
     * @return Getter 方法
     */
    public static Optional<Method> getGetterMethodOptByField(final Class<?> clazz, final Field field) {
        return getGetterMethodOptByField(clazz, true, field, false);
    }

    /**
     * 通过字段获取其对应的 Getter 方法
     * <p>
     * 包含父类的方法
     *
     * @param clazz                 目标类
     * @param field                 字段
     * @param boxedBoolWithIsPrefix {@link Boolean} 类型字段是否使用 is 前缀
     * @return Getter 方法
     */
    public static Optional<Method> getGetterMethodOptByField(final Class<?> clazz, Field field, final boolean boxedBoolWithIsPrefix) {
        return getGetterMethodOptByField(clazz, true, field, boxedBoolWithIsPrefix);
    }

    /**
     * 通过字段获取其对应的 Getter 方法
     * <p>
     * {@link Boolean} 类型字段不使用 is 前缀（使用 get 前缀）
     *
     * @param clazz                 目标类
     * @param withSuperClassMethods 是否获取父类的方法
     * @param field                 字段
     * @return Getter 方法
     */
    public static Optional<Method> getGetterMethodOptByField(final Class<?> clazz, final boolean withSuperClassMethods, Field field) {
        return getGetterMethodOptByField(clazz, withSuperClassMethods, field, false);
    }

    /**
     * 通过字段获取其对应的 Getter 方法
     *
     * @param clazz                 目标类
     * @param withSuperClassMethods 是否获取父类的方法
     * @param field                 字段
     * @param boxedBoolWithIsPrefix {@link Boolean} 类型字段是否使用 is 前缀
     * @return Getter 方法
     */
    public static Optional<Method> getGetterMethodOptByField(final Class<?> clazz, final boolean withSuperClassMethods, Field field, final boolean boxedBoolWithIsPrefix) {
        CharSequence methodName = getGetterMethodNameByField(field, boxedBoolWithIsPrefix);
        return getMethodOptByNameAndParam(clazz, withSuperClassMethods, methodName);
    }

    /**
     * 通过字段获取其对应的 Getter 方法名
     * <p>
     * {@link Boolean} 类型字段不使用 is 前缀（使用 get 前缀）
     *
     * @param field 字段
     * @return Getter 方法名
     */
    public static String getGetterMethodNameByField(final Field field) {
        return getGetterMethodNameByField(field, false);
    }

    /**
     * 通过字段获取其对应的 Getter 方法名
     *
     * @param field                 字段
     * @param boxedBoolWithIsPrefix {@link Boolean} 类型字段是否使用 is 前缀
     * @return Getter 方法名
     */
    public static String getGetterMethodNameByField(final Field field, final boolean boxedBoolWithIsPrefix) {
        String fieldName = field.getName();
        Class<?> fieldType = field.getType();

        String methodPrefix;
        if (fieldType.isPrimitive() && boolean.class.equals(fieldType)) {
            methodPrefix = BOOL_GETTER_METHOD_PREFIX;
        } else if (boxedBoolWithIsPrefix && Boolean.class.equals(fieldType)) {
            methodPrefix = BOOL_GETTER_METHOD_PREFIX;
        } else {
            methodPrefix = GETTER_METHOD_PREFIX;
        }

        char firstChar = fieldName.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            return String.format("%s%s%s", methodPrefix, Character.toUpperCase(firstChar), fieldName.substring(1));
        }
        return String.format("%s%s", methodPrefix, fieldName);
    }

    /**
     * 判断是否为 Setter 方法
     *
     * @param method     方法
     * @param ignoreCase 是否忽略大小写
     * @return 是否为 Setter 方法
     */
    public static boolean isSetter(final Method method, boolean ignoreCase) {
        if (Objects.isNull(method)) {
            return false;
        }
        // 参数个数必须为 1
        if (method.getParameterCount() != 1) {
            return false;
        }

        String methodName = method.getName();
        if (ignoreCase) {
            methodName = methodName.toLowerCase();
        }
        return methodName.startsWith(SETTER_METHOD_PREFIX);
    }

    /**
     * 通过字段获取其对应的 Setter 方法
     *
     * @param clazz 目标类
     * @param field 字段
     * @return Setter 方法
     */
    public static Optional<Method> getSetterMethodOptByField(final Class<?> clazz, Field field) {
        return getSetterMethodOptByField(clazz, false, field);
    }

    /**
     * 通过字段获取其对应的 Setter 方法
     *
     * @param clazz                 目标类
     * @param withSuperClassMethods 是否获取父类方法
     * @param field                 字段
     * @return Setter 方法
     */
    public static Optional<Method> getSetterMethodOptByField(final Class<?> clazz, final boolean withSuperClassMethods, Field field) {
        String methodName = getSetterMethodNameByField(field);
        return getMethodOptByNameAndParam(clazz, withSuperClassMethods, methodName, field.getType());
    }

    /**
     * 通过字段获取其对应的 Setter 方法名
     *
     * @param field 字段
     * @return Setter 方法名
     */
    public static String getSetterMethodNameByField(final Field field) {
        String fieldName = field.getName();
        char firstChar = fieldName.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            return String.format("%s%s%s", SETTER_METHOD_PREFIX, Character.toUpperCase(firstChar), fieldName.substring(1));
        }
        return String.format("%s%s", SETTER_METHOD_PREFIX, fieldName);
    }

    /**
     * 判断方法是否为构建方法
     *
     * @param method 方法
     * @return 是否为构建方法
     */
    public static boolean isBuilderMethod(Method method) {
        return Modifier.isStatic(method.getModifiers()) && ClassUtil.isAssignable(method.getReturnType(), method.getDeclaringClass());
    }

    /**
     * @see MemberUtil#getInstanceOpt(Class, boolean, Object...)
     */
    protected static <T> Collection<Method> getInstanceMethods(Class<T> clazz, Class<?>... parameterTypes) {
        Stream<Method> methodStream = getStaticMethods(clazz, false).stream()
                .filter(MethodUtil::isBuilderMethod);
        if (Objects.nonNull(parameterTypes)) {
            methodStream = methodStream.filter(ExecutableUtil.ParameterTypePredicate.of(parameterTypes));
        }
        return methodStream.collect(Collectors.toList());
    }

    protected MethodUtil() {
        throw new UnsupportedOperationException();
    }
}
