package org.zero.common.core.util.java.reflect;

import lombok.experimental.UtilityClass;
import org.zero.common.core.util.java.lang.ClassUtil;
import org.zero.common.core.util.java.lang.StringUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author zero
 * @since 2021/4/30
 */
@UtilityClass
public class ReflectUtil {
    /* ********************************************************* Field ********************************************************* */

    /**
     * 获取所有声明字段（包括父类中的）
     */
    public static List<Field> getAllDeclaredFields(final Class<?> clazz) {
        return getDeclaredFields(clazz, true);
    }

    /**
     * 获取所有声明字段
     *
     * @param clazz                类对象
     * @param withSuperClassFields 是否获取父类的字段
     * @return 字段
     */
    public static List<Field> getDeclaredFields(final Class<?> clazz, final boolean withSuperClassFields) {
        List<Field> allFields = new ArrayList<>();
        Class<?> searchType = clazz;
        while (Objects.nonNull(searchType)) {
            Field[] declaredFields = searchType.getDeclaredFields();
            allFields.addAll(Arrays.asList(declaredFields));
            searchType = withSuperClassFields ? searchType.getSuperclass() : null;
        }
        return allFields;
    }

    /**
     * 获取所有满足条件的字段（包括父类中的）
     */
    public static List<Field> getFilteredFields(final Class<?> clazz, final Predicate<Field> filter) {
        return getFilteredFields(clazz, true, filter);
    }

    /**
     * 获取满足条件的字段
     */
    public static List<Field> getFilteredFields(final Class<?> clazz, final boolean withSuperClassFields, final Predicate<Field> filter) {
        if (Objects.isNull(clazz)) {
            return Collections.emptyList();
        }
        List<Field> declaredFields = getDeclaredFields(clazz, withSuperClassFields);
        if (Objects.isNull(filter)) {
            return declaredFields;
        }
        return declaredFields.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有注解了指定注解的字段（包括父类中的）
     */
    public static List<Field> getAnnotatedFields(final Class<?> targetClass, final Class<? extends Annotation> annotationClass) {
        return getAnnotatedFields(targetClass, true, annotationClass);
    }

    /**
     * 获取注解了指定注解的字段
     */
    public static List<Field> getAnnotatedFields(final Class<?> targetClass, final boolean withSuperClassFields, final Class<? extends Annotation> annotationClass) {
        return getFilteredFields(targetClass, withSuperClassFields, field -> field.isAnnotationPresent(annotationClass));
    }

    /**
     * 从所有字段中获取注解了指定注解的第一个字段（包括父类中的）
     */
    public static Optional<Field> getAnnotatedFieldOpt(Class<?> targetClass, Class<? extends Annotation> annotationClass) {
        return getAnnotatedFieldOpt(targetClass, true, annotationClass);
    }

    /**
     * 获取注解了指定注解的第一个字段
     */
    public static Optional<Field> getAnnotatedFieldOpt(Class<?> targetClass, final boolean withSuperClassFields, Class<? extends Annotation> annotationClass) {
        return getAnnotatedFields(targetClass, withSuperClassFields, annotationClass).stream()
                .findFirst();
    }

    public static Object getStaticFieldValue(Field field) {
        return getStaticFieldValue(field, Object.class);
    }

    public static <T> T getStaticFieldValue(Field field, Type type) {
        return getFieldValue(field, null, type);
    }

    public static Object getFieldValue(Field field, Object obj) {
        return getFieldValue(field, obj, Object.class);
    }

    public static <T> T getFieldValue(Field field, Object obj, Type type) {
        return ReflectUtil.<T>getFieldValueOpt(field, obj, type).orElse(null);
    }

    public static <T> Optional<T> getFieldValueOpt(Field field, Object obj, Type type) {
        int modifiers = field.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            setAccessible(field);
        }
        try {
            Object got = field.get(obj);
            T result = ClassUtil.cast(got, type);
            return Optional.ofNullable(result);
        } catch (Exception ignored) {
            return Optional.empty();
        } finally {
            field.setAccessible(false);
        }
    }

    /* ********************************************************* Method ********************************************************* */
    public static final String SETTER_METHOD_PREFIX = "set";
    public static final String GETTER_METHOD_PREFIX = "get";
    public static final String BOOL_GETTER_METHOD_PREFIX = "is";

    /**
     * 获取所有声明的方法
     */
    public static List<Method> getAllDeclaredMethods(final Class<?> clazz) {
        return getDeclaredMethods(clazz, true);
    }

    /**
     * 获取声明的方法
     */
    public static List<Method> getDeclaredMethods(final Class<?> clazz, final boolean withSuperClassMethods) {
        List<Method> allMethods = new ArrayList<>();
        Class<?> searchType = clazz;
        while (Objects.nonNull(searchType)) {
            Method[] declaredMethods = searchType.getDeclaredMethods();
            allMethods.addAll(Arrays.asList(declaredMethods));
            searchType = withSuperClassMethods ? searchType.getSuperclass() : null;
        }
        return allMethods;
    }

    /**
     * 获取所有满足条件的方法
     */
    public static List<Method> getFilteredMethods(final Class<?> clazz, final Predicate<Method> filter) {
        return getFilteredMethods(clazz, true, filter);
    }

    /**
     * 获取满足条件的方法
     */
    public static List<Method> getFilteredMethods(final Class<?> clazz, final boolean withSuperClassMethods, final Predicate<Method> filter) {
        if (Objects.isNull(clazz)) {
            return Collections.emptyList();
        }
        final List<Method> methods = getDeclaredMethods(clazz, withSuperClassMethods);
        if (Objects.isNull(filter)) {
            return methods;
        }
        return methods.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有公用方法
     */
    public static List<Method> getPublicMethods(final Class<?> clazz) {
        return getFilteredMethods(clazz, false, method -> Modifier.isPublic(method.getModifiers()));
    }

    /**
     * 获取满足条件的公用方法
     */
    public static List<Method> getFilteredPublicMethods(final Class<?> clazz, final Predicate<Method> filter) {
        List<Method> publicMethods = getPublicMethods(clazz);
        if (Objects.isNull(filter)) {
            return publicMethods;
        }

        return publicMethods.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    /**
     * 通过方法名和参数类型获取方法
     */
    public static Method getMethodByNameAndParam(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) {
        return getMethodByNameAndParam(clazz, true, methodName, parameterTypes);
    }

    /**
     * 通过方法名和参数类型获取方法
     */
    public static Method getMethodByNameAndParam(final Class<?> clazz, final boolean withSuperClassMethods, final String methodName, final Class<?>... parameterTypes) {
        return getMethodOptByNameAndParam(clazz, withSuperClassMethods, methodName, parameterTypes).orElse(null);
    }

    /**
     * 通过方法名和参数类型获取方法
     */
    public static Optional<Method> getMethodOptByNameAndParam(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) {
        return getMethodOptByNameAndParam(clazz, true, methodName, parameterTypes);
    }

    /**
     * 通过方法名和参数类型获取方法
     */
    public static Optional<Method> getMethodOptByNameAndParam(final Class<?> clazz, final boolean withSuperClassMethods, final String methodName, final Class<?>... parameterTypes) {
        List<Method> filteredMethods = getMethodsByName(clazz, withSuperClassMethods, methodName).stream()
                .filter(method -> {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    if (paramTypes.length != parameterTypes.length) {
                        return false;
                    }
                    for (int i = 0; i < paramTypes.length; i++) {
                        Class<?> paramType = paramTypes[i];
                        Class<?> parameterType = parameterTypes[i];
                        if (!paramType.isAssignableFrom(parameterType)) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());

        // 优先匹配参数类型完全匹配的方法
        for (Method method : filteredMethods) {
            Class<?>[] paramTypes = method.getParameterTypes();
            if (Arrays.equals(paramTypes, parameterTypes)) {
                return Optional.of(method);
            }
        }

        return filteredMethods.stream().findFirst();
    }

    /**
     * 通过方法名获取方法
     */
    public static List<Method> getMethodsByName(final Class<?> clazz, final String methodName) {
        return getMethodsByName(clazz, true, methodName);
    }

    /**
     * 通过方法名获取方法
     */
    public static List<Method> getMethodsByName(final Class<?> clazz, final boolean withSuperClassMethods, final String methodName) {
        return getFilteredMethods(clazz, withSuperClassMethods, method -> method.getName().equals(methodName));
    }

    public static Object invokeStatic(final Method method, final Object... args) {
        return invokeStatic(method, Object.class, args);
    }

    public static <T> T invokeStatic(final Method method, final Type type, final Object... args) {
        return ReflectUtil.<T>invokeStaticOpt(method, type, args).orElse(null);
    }

    public static <T> Optional<T> invokeStaticOpt(final Method method, final Type type, final Object... args) {
        return invokeOpt(method, null, type, args);
    }

    /**
     * 调用方法获取结果
     */
    public static Object invoke(final Method method, final Object target, final Object... args) {
        return invokeOpt(method, target, args).orElse(null);
    }

    /**
     * 调用方法获取结果
     */
    public static <T> T invoke(final Method method, final Object target, final Type type, final Object... args) {
        return ReflectUtil.<T>invokeOpt(method, target, type, args).orElse(null);
    }

    /**
     * 调用方法获取结果
     */
    public static Optional<Object> invokeOpt(final Method method, final Object target, final Object... args) {
        return invokeOpt(method, target, Object.class, args);
    }

    /**
     * 调用方法获取结果
     */
    public static <T> Optional<T> invokeOpt(final Method method, final Object target, final Type type, final Object... args) {
        if (Objects.isNull(method)) {
            return Optional.empty();
        }

        int modifiers = method.getModifiers();
        Object obj = target;
        if (Modifier.isStatic(modifiers)) {
            obj = null;
        }
        if (!Modifier.isPublic(modifiers)) {
            setAccessible(method);
        }
        try {
            Object invoked = method.invoke(obj, args);
            T result = ClassUtil.cast(invoked, type);
            return Optional.ofNullable(result);
        } catch (Exception ignored) {
            return Optional.empty();
        } finally {
            // 重置强制执行 Java 语言访问控制检查
            method.setAccessible(false);
        }
    }

    /**
     * 禁止执行 Java 语言访问控制检查
     */
    public static <T extends AccessibleObject> T setAccessible(final T accessibleObject) {
        if (Objects.nonNull(accessibleObject) && !accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
        return accessibleObject;
    }

    /**
     * 通过字段获取其对应的 getter 方法
     */
    public static Optional<Method> getGetterMethodOptByField(final Class<?> clazz, final Field field) {
        return getGetterMethodOptByField(clazz, false, field);
    }

    /**
     * 通过字段获取其对应的 getter 方法
     */
    public static Optional<Method> getGetterMethodOptByField(final Class<?> clazz, final boolean withSuperClassMethods, Field field) {
        String methodName = getGetterMethodNameByFieldName(field, false);
        return getMethodOptByNameAndParam(clazz, withSuperClassMethods, methodName);
    }

    /**
     * 通过字段获取其对应的 getter 方法名
     */
    public static String getGetterMethodNameByFieldName(final Field field, final boolean includeBoxedBool) {
        String fieldName = field.getName();
        Class<?> fieldType = field.getType();

        String methodPrefix;
        if (fieldType.isPrimitive() && fieldType == boolean.class) {
            methodPrefix = BOOL_GETTER_METHOD_PREFIX;
        } else if (includeBoxedBool && Boolean.class.equals(fieldType)) {
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
     * 从 getter 方法中获取字段名
     */
    public static String getFieldNameFromGetterMethod(final Method method) {
        String methodName = method.getName();
        if (methodName.startsWith(GETTER_METHOD_PREFIX)) {
            return StringUtil.removePrefixAndFirst2Lower(methodName, GETTER_METHOD_PREFIX);
        }
        if (methodName.startsWith(BOOL_GETTER_METHOD_PREFIX)) {
            return StringUtil.removePrefixAndFirst2Lower(methodName, BOOL_GETTER_METHOD_PREFIX);
        }
        return "";
    }

    /**
     * 判断是否为 getter 方法
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

    /* ********************************************************* Constructor ********************************************************* */
    public static List<Constructor<?>> getAllDeclaredConstructors(final Class<?> clazz) {
        return getDeclaredConstructors(clazz, true);
    }

    public static List<Constructor<?>> getDeclaredConstructors(final Class<?> clazz, final boolean withSuperClassConstructors) {
        List<Constructor<?>> allConstructors = new ArrayList<>();
        Class<?> searchType = clazz;
        while (Objects.nonNull(searchType)) {
            Constructor<?>[] declaredConstructors = searchType.getDeclaredConstructors();
            allConstructors.addAll(Arrays.asList(declaredConstructors));
            searchType = withSuperClassConstructors ? searchType.getSuperclass() : null;
        }
        return allConstructors;
    }

    public static List<Constructor<?>> getFilteredConstructors(final Class<?> clazz, final Predicate<Constructor<?>> filter) {
        return getFilteredConstructors(clazz, false, filter);
    }

    public static List<Constructor<?>> getFilteredConstructors(final Class<?> clazz, final boolean withSuperClassConstructors, final Predicate<Constructor<?>> filter) {
        List<Constructor<?>> declaredConstructors = getDeclaredConstructors(clazz, withSuperClassConstructors);
        if (Objects.isNull(filter)) {
            return declaredConstructors;
        }
        return declaredConstructors.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public static Optional<Constructor<?>> getConstructorOptByParam(final Class<?> clazz, final Class<?>... parameterTypes) {
        return getConstructorOptByParam(clazz, false, parameterTypes);
    }

    public static Optional<Constructor<?>> getConstructorOptByParam(final Class<?> clazz, final boolean withSuperClassConstructors, final Class<?>... parameterTypes) {
        List<Constructor<?>> filteredConstructors = getFilteredConstructors(clazz, withSuperClassConstructors, (constructor -> {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            if (paramTypes.length != parameterTypes.length) {
                return false;
            }
            for (int i = 0; i < paramTypes.length; i++) {
                Class<?> paramType = paramTypes[i];
                Class<?> parameterType = parameterTypes[i];
                if (!paramType.isAssignableFrom(parameterType)) {
                    return false;
                }
            }
            return true;
        }));

        // 优先匹配参数类型完全匹配的构造方法
        for (Constructor<?> constructor : filteredConstructors) {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            if (Arrays.equals(paramTypes, parameterTypes)) {
                return Optional.of(constructor);
            }
        }

        return filteredConstructors.stream().findFirst();
    }

    public static <T> T newInstance(Class<T> clazz, Object... args) {
        return newInstanceOpt(clazz, args).orElse(null);
    }

    public static <T> Optional<T> newInstanceOpt(Class<T> clazz, Object... args) {
        Class<?>[] parameterTypes = Arrays.stream(args)
                .map(Object::getClass)
                .toArray(Class[]::new);

        return getConstructorOptByParam(clazz, parameterTypes).map(constructor -> {
                    try {
                        return constructor.newInstance(args);
                    } catch (Exception ignored) {
                        return null;
                    }
                })
                .filter(clazz::isInstance)
                .map(clazz::cast);
    }
}
