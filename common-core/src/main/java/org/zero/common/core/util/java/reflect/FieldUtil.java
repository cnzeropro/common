package org.zero.common.core.util.java.reflect;

import lombok.experimental.UtilityClass;
import org.zero.common.core.util.java.lang.StringUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.zero.common.core.util.java.reflect.MethodUtil.BOOL_GETTER_METHOD_PREFIX;
import static org.zero.common.core.util.java.reflect.MethodUtil.GETTER_METHOD_PREFIX;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/11
 */
@UtilityClass
public class FieldUtil extends ReflectUtil {
    /**
     * 获取所有字段
     *
     * @param clazz 目标类
     * @return 字段
     */
    public static List<Field> getAllFields(final Class<?> clazz) {
        return getFields(clazz, true);
    }

    /**
     * 获取字段
     *
     * @param clazz 目标类
     * @return 字段
     */
    public static List<Field> getFields(final Class<?> clazz) {
        return getFields(clazz, false);
    }

    /**
     * 获取字段
     *
     * @param clazz                目标类
     * @param withSuperClassFields 是否获取父类的字段
     * @return 字段
     */
    public static List<Field> getFields(final Class<?> clazz, final boolean withSuperClassFields) {
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
     * 获取公共字段
     *
     * @param clazz 目标类
     * @return 公共字段
     */
    public static List<Field> getPublicFields(final Class<?> clazz) {
        return new ArrayList<>(Arrays.asList(clazz.getFields()));
    }

    /**
     * 获取过滤后的字段
     * <p>
     * 包含父类的字段
     *
     * @param clazz  目标类
     * @param filter 过滤器
     * @return 字段
     */
    public static List<Field> getFilteredFields(final Class<?> clazz, final Predicate<Field> filter) {
        return getFilteredFields(clazz, true, filter);
    }

    /**
     * 获取过滤后的字段
     *
     * @param clazz                目标类
     * @param withSuperClassFields 是否获取父类的字段
     * @param filter               过滤器
     * @return 字段
     */
    public static List<Field> getFilteredFields(final Class<?> clazz, final boolean withSuperClassFields, final Predicate<Field> filter) {
        List<Field> declaredFields = getFields(clazz, withSuperClassFields);
        if (Objects.isNull(filter)) {
            return declaredFields;
        }
        return declaredFields.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    /**
     * 获取静态字段
     * <p>
     * 包含父类的字段
     *
     * @param targetClass 目标类
     * @return 静态字段
     */
    public static List<Field> getStaticFields(final Class<?> targetClass) {
        return getStaticFields(targetClass, true);
    }

    /**
     * 获取静态字段
     *
     * @param targetClass          目标类
     * @param withSuperClassFields 是否获取父类的字段
     * @return 静态字段
     */
    public static List<Field> getStaticFields(final Class<?> targetClass, final boolean withSuperClassFields) {
        return getFilteredFields(targetClass, withSuperClassFields, field -> Modifier.isStatic(field.getModifiers()));
    }

    /**
     * 获取拥有指定注解的字段
     * <p>
     * 包含父类的字段
     *
     * @param targetClass     目标类
     * @param annotationClass 注解类型
     * @return 字段
     */
    public static List<Field> getAnnotatedFields(final Class<?> targetClass, final Class<? extends Annotation> annotationClass) {
        return getAnnotatedFields(targetClass, true, annotationClass);
    }

    /**
     * 获取拥有指定注解的字段
     *
     * @param targetClass          目标类
     * @param withSuperClassFields 是否获取父类的字段
     * @param annotationClass      注解类型
     * @return 字段
     */
    public static List<Field> getAnnotatedFields(final Class<?> targetClass, final boolean withSuperClassFields, final Class<? extends Annotation> annotationClass) {
        return getFilteredFields(targetClass, withSuperClassFields, field -> field.isAnnotationPresent(annotationClass));
    }

    /**
     * 通过字段名获取字段 {@link Optional}
     * <p>
     * 包含父类的字段
     *
     * @param clazz     目标类
     * @param fieldName 字段名称
     * @return 字段 {@link Optional}
     */
    public static Optional<Field> getFieldOptByName(final Class<?> clazz, final String fieldName) {
        return getFieldOptByName(clazz, true, fieldName);
    }

    /**
     * 通过字段名获取字段 {@link Optional}
     *
     * @param clazz                目标类
     * @param withSuperClassFields 是否获取父类的字段
     * @param fieldName            字段名称
     * @return 字段 {@link Optional}
     */
    public static Optional<Field> getFieldOptByName(final Class<?> clazz, final boolean withSuperClassFields, final String fieldName) {
        List<Field> filteredFields = getFilteredFields(clazz, withSuperClassFields, field -> Objects.equals(field.getName(), fieldName));
        return filteredFields.stream()
                .findFirst();
    }

    public static Object getStaticFieldValue(final Field field) {
        return getStaticFieldValue(field, Object.class);
    }

    public static <T> T getStaticFieldValue(final Field field, final Type type) {
        return getFieldValue(field, null, type);
    }

    public static Object getFieldValue(final Field field, final Object target) {
        return getFieldValue(field, target, Object.class);
    }

    public static <T> T getFieldValue(final Field field, final Object target, final Type type) {
        return FieldUtil.<T>getFieldValueOpt(field, target, type).orElse(null);
    }

    public static <T> Optional<T> getFieldValueOpt(final Field field, final Object target, final Type type) {
        if (Objects.isNull(field)) {
            return Optional.empty();
        }
        int mod = field.getModifiers();
        Object obj = target;
        if (Modifier.isStatic(mod)) {
            obj = null;
        }
        if (!Modifier.isPublic(mod)) {
            setAccessible(field);
        }
        try {
            Object got = field.get(obj);
            T result = ClassUtil.cast(got, type);
            return Optional.ofNullable(result);
        } catch (Exception ignored) {
            return Optional.empty();
        } finally {
            setInaccessible(field);
        }
    }

    public static <T, R> Optional<R> getStaticFieldValueOpt(final Class<T> clazz, final String fieldName, final Type type) {
        return getFieldValueOpt(clazz, false, null, fieldName, type);
    }

    public static <T, R> Optional<R> getFieldValueOpt(final Class<T> clazz, final boolean withSuperClassFields, final T target, String fieldName, final Type type) {
        return getFieldOptByName(Objects.isNull(clazz) ? target.getClass() : clazz, Objects.nonNull(target) && withSuperClassFields, fieldName).flatMap(field -> getFieldValueOpt(field, target, type));
    }

    public static boolean setStaticFieldValue(final Field field, final Object value) {
        return setFieldValue(field, null, value);
    }

    public static boolean setFieldValue(final Field field, final Object target, final Object value) {
        int mod = field.getModifiers();
        Object obj = target;
        if (Modifier.isStatic(mod)) {
            obj = null;
        }
        if (!Modifier.isPublic(mod)) {
            setAccessible(field);
        }
        try {
            field.set(obj, value);
            return true;
        } catch (Exception ignored) {
            return false;
        } finally {
            setInaccessible(field);
        }
    }

    public static <T> boolean setStaticFieldValue(final Class<T> clazz, String fieldName, final Object value) {
        return setFieldValue(clazz, false, null, fieldName, value);
    }

    public static <T> boolean setFieldValue(final Class<T> clazz, final boolean withSuperClassFields, final T target, String fieldName, final Object value) {
        return getFieldOptByName(Objects.isNull(clazz) ? target.getClass() : clazz, Objects.nonNull(target) && withSuperClassFields, fieldName).map(field -> setFieldValue(field, target, value)).orElse(Boolean.FALSE);
    }

    /**
     * 从 Getter 方法中获取字段
     *
     * @param getterMethod Getter 方法
     * @return 字段名
     */
    public static Optional<Field> getFieldOptFromGetterMethod(final Method getterMethod) {
        String fieldName = getFieldNameFromGetterMethod(getterMethod);
        return getFieldOptByName(getterMethod.getDeclaringClass(), fieldName);
    }

    /**
     * 从 Getter 方法中获取字段名
     *
     * @param getterMethod Getter 方法
     * @return 字段名
     */
    public static String getFieldNameFromGetterMethod(final Method getterMethod) {
        String methodName = getterMethod.getName();
        if (methodName.startsWith(GETTER_METHOD_PREFIX)) {
            return StringUtil.removePrefixAndFirst2Lower(methodName, GETTER_METHOD_PREFIX);
        }
        if (methodName.startsWith(BOOL_GETTER_METHOD_PREFIX)) {
            return StringUtil.removePrefixAndFirst2Lower(methodName, BOOL_GETTER_METHOD_PREFIX);
        }
        return methodName;
    }


    /**
     * 是否为父类引用字段
     * <p>
     * 当字段所在类是对象子类时（对象中定义的非 static 的 class），会自动生成一个以 {@code this$0} 为名称的字段，指向父类对象
     *
     * @param field 字段
     * @return 是否为父类引用字段
     */
    public static boolean isOuterClassField(Field field) {
        return "this$0".equals(field.getName());
    }
}
