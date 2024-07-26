package org.zero.common.core.util.java.reflect;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
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
    public static final String SETTER_METHOD_PREFIX = "set";
    public static final String GETTER_METHOD_PREFIX = "get";
    public static final String BOOL_GETTER_METHOD_PREFIX = "is";

    /* ********************************************************* Field ********************************************************* */

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
            allFields.addAll(CollectionUtils.toList(declaredFields));
            searchType = withSuperClassFields ? searchType.getSuperclass() : null;
        }
        return allFields;
    }

    /**
     * 获取满足条件的字段
     */
    public static List<Field> getFilteredFields(final Class<?> clazz, final Predicate<Field> filter) {
        if (Objects.isNull(clazz)) {
            return Collections.emptyList();
        }
        List<Field> declaredFields = getDeclaredFields(clazz, true);
        if (Objects.isNull(filter)) {
            return declaredFields;
        }
        return declaredFields.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    /**
     * 获取指定注解的字段列表
     */
    public static List<Field> getAnnotatedFields(Class<?> targetClass, Class<? extends Annotation> annotationClass) {
        return getFilteredFields(targetClass, field -> field.isAnnotationPresent(annotationClass));
    }

    /**
     * 获取指定注解的第一个字段
     */
    public static Optional<Field> getAnnotatedFieldOpt(Class<?> targetClass, Class<? extends Annotation> annotationClass) {
        return getAnnotatedFields(targetClass, annotationClass).stream()
                .findFirst();
    }

    /* ********************************************************* Method ********************************************************* */

    /**
     * 通过方法名获取方法
     */
    public static Optional<Method> getMethodOptByName(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            return Optional.of(method);
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    /**
     * 调用方法获取结果
     */
    public static Optional<Object> invoke(Method method, Object target, Object... args) {
        try {
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers)) {
                target = null;
            }
            if (!Modifier.isPublic(modifiers)) {
                setAccessible(method);
            }

            Object result = method.invoke(target, args);
            method.setAccessible(false);
            return Optional.ofNullable(result);
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    public static <T extends AccessibleObject> T setAccessible(T accessibleObject) {
        if (Objects.nonNull(accessibleObject) && !accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
        return accessibleObject;
    }

    /**
     * 获取满足条件的公用方法
     */
    public static List<Method> getFilteredPublicMethods(final Class<?> clazz, final Predicate<Method> filter) {
        if (Objects.isNull(clazz)) {
            return Collections.emptyList();
        }
        final Method[] methods = clazz.getMethods();
        if (Objects.isNull(filter)) {
            return CollectionUtils.toList(methods);
        }
        List<Method> result = new ArrayList<>();
        for (Method method : methods) {
            if (filter.test(method)) {
                result.add(method);
            }
        }
        return result;
    }

    /**
     * 通过字段获取其对应的 getter 方法
     */
    public static Optional<Method> getGetterMethodOptByField(Class<?> clazz, Field field) {
        String methodName = getGetterMethodNameByFieldName(field, false);
        return getMethodOptByName(clazz, methodName);
    }

    /**
     * 通过字段获取其对应的 getter 方法名
     */
    public static String getGetterMethodNameByFieldName(Field field, boolean includeBoxedBool) {
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
        if (methodName.startsWith("get")) {
            return StringUtils.removePrefixAfterPrefixToLower(methodName, 3);
        }
        if (methodName.startsWith("is")) {
            return StringUtils.removePrefixAfterPrefixToLower(methodName, 2);
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
}
