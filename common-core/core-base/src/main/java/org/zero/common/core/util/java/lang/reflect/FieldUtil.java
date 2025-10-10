package org.zero.common.core.util.java.lang.reflect;

import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.ClassUtil;
import org.zero.common.core.util.java.lang.StringUtil;
import org.zero.common.core.util.java.lang.ThrowableUtil;
import org.zero.common.core.util.java.util.ListUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.zero.common.core.util.java.lang.reflect.MethodUtil.BOOL_GETTER_METHOD_PREFIX;
import static org.zero.common.core.util.java.lang.reflect.MethodUtil.GETTER_METHOD_PREFIX;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/11
 */
public class FieldUtil {
    /**
     * 获取字段
     *
     * @param clazz 目标类
     * @return 字段
     */
    public static Collection<Field> list(final Class<?> clazz) {
        return list(clazz, true);
    }

    /**
     * 获取字段
     *
     * @param clazz                目标类
     * @param withSuperClassFields 是否获取父类的字段
     * @return 字段
     */
    public static Collection<Field> list(final Class<?> clazz, final boolean withSuperClassFields) {
        Collection<Field> fields = new ArrayList<>();
        Class<?> searchType = clazz;
        while (Objects.nonNull(searchType)) {
            Field[] declaredFields = searchType.getDeclaredFields();
            Collections.addAll(fields, declaredFields);
            searchType = withSuperClassFields ? searchType.getSuperclass() : null;
        }
        return fields;
    }

    /**
     * 获取公共字段
     *
     * @param clazz 目标类
     * @return 公共字段
     */
    public static Collection<Field> listPublic(final Class<?> clazz) {
        return ListUtil.of(clazz.getFields());
    }

    /**
     * 获取过滤后的字段
     *
     * @param clazz  目标类
     * @param filter 过滤器
     * @return 字段
     */
    public static Collection<Field> listFiltered(final Class<?> clazz, final Predicate<Field> filter) {
        return listFiltered(clazz, true, filter);
    }

    /**
     * 获取过滤后的字段
     *
     * @param clazz                目标类
     * @param withSuperClassFields 是否获取父类的字段
     * @param filter               过滤器
     * @return 字段
     */
    public static Collection<Field> listFiltered(final Class<?> clazz, final boolean withSuperClassFields, final Predicate<Field> filter) {
        Collection<Field> declaredFields = list(clazz, withSuperClassFields);
        if (Objects.isNull(filter)) {
            return declaredFields;
        }
        return declaredFields.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    /**
     * 获取静态字段
     *
     * @param targetClass 目标类
     * @return 静态字段
     */
    public static Collection<Field> listStatic(final Class<?> targetClass) {
        return listStatic(targetClass, true);
    }

    /**
     * 获取静态字段
     *
     * @param targetClass          目标类
     * @param withSuperClassFields 是否获取父类的字段
     * @return 静态字段
     */
    public static Collection<Field> listStatic(final Class<?> targetClass, final boolean withSuperClassFields) {
        return listFiltered(targetClass, withSuperClassFields, field -> Modifier.isStatic(field.getModifiers()));
    }

    /**
     * 获取拥有指定注解的字段
     *
     * @param targetClass     目标类
     * @param annotationClass 注解类型
     * @return 字段
     */
    public static Collection<Field> listAnnotated(final Class<?> targetClass, final Class<? extends Annotation> annotationClass) {
        return listAnnotated(targetClass, true, annotationClass);
    }

    /**
     * 获取拥有指定注解的字段
     *
     * @param targetClass          目标类
     * @param withSuperClassFields 是否获取父类的字段
     * @param annotationClass      注解类型
     * @return 字段
     */
    public static Collection<Field> listAnnotated(final Class<?> targetClass, final boolean withSuperClassFields, final Class<? extends Annotation> annotationClass) {
        return listFiltered(targetClass, withSuperClassFields, field -> field.isAnnotationPresent(annotationClass));
    }

    /**
     * 通过字段名获取字段
     *
     * @param clazz     目标类
     * @param fieldName 字段名称
     * @return 字段
     */
    public static Field getByName(final Class<?> clazz, final CharSequence fieldName) {
        return FieldUtil.getOptByName(clazz, fieldName).orElse(null);
    }

    /**
     * 通过字段名获取字段
     *
     * @param clazz                目标类
     * @param withSuperClassFields 是否获取父类的字段
     * @param fieldName            字段名称
     * @return 字段
     */
    public static Field getByName(final Class<?> clazz, final boolean withSuperClassFields, final CharSequence fieldName) {
        return getOptByName(clazz, withSuperClassFields, fieldName).orElse(null);
    }

    /**
     * 通过字段名获取字段 {@link Optional}
     *
     * @param clazz     目标类
     * @param fieldName 字段名称
     * @return 字段 {@link Optional}
     */
    public static Optional<Field> getOptByName(final Class<?> clazz, final CharSequence fieldName) {
        return getOptByName(clazz, true, fieldName);
    }

    /**
     * 通过字段名获取字段 {@link Optional}
     *
     * @param clazz                目标类
     * @param withSuperClassFields 是否获取父类的字段
     * @param fieldName            字段名称
     * @return 字段 {@link Optional}
     */
    public static Optional<Field> getOptByName(final Class<?> clazz, final boolean withSuperClassFields, final CharSequence fieldName) {
        Collection<Field> filteredFields = listFiltered(clazz, withSuperClassFields, field -> CharSequenceUtil.contentEquals(fieldName, field.getName()));
        return filteredFields.stream()
                .findFirst();
    }

    public static Object getStaticValue(final Field field) {
        return getStaticValue(field, false);
    }

    public static Object getStaticValue(final Field field, final boolean quietIfException) {
        return getStaticValue(field, Object.class, quietIfException);
    }

    public static <T> T getStaticValue(final Field field, final Type type) {
        return getStaticValue(field, type, false);
    }

    public static <T> T getStaticValue(final Field field, final Type type, final boolean quietIfException) {
        return getValue(field, null, type, quietIfException);
    }

    public static Object getValue(final Field field, final Object target) {
        return getValue(field, target, false);
    }

    public static Object getValue(final Field field, final Object target, final boolean quietIfException) {
        return getValue(field, target, Object.class, quietIfException);
    }

    public static <T> T getValue(final Field field, final Object target, final Type type) {
        return getValue(field, target, type, false);
    }

    public static <T> T getValue(final Field field, final Object target, final Type type, final boolean quietIfException) {
        return FieldUtil.<T>getValueOpt(field, target, type, quietIfException).orElse(null);
    }

    public static <T> Optional<T> getValueOpt(final Field field, final Object target, final Type type) {
        return getValueOpt(field, target, type, true);
    }

    public static <T> Optional<T> getValueOpt(final Field field, final Object target, final Type type, final boolean quietIfException) {
        if (Objects.isNull(field)) {
            return Optional.empty();
        }
        int mod = field.getModifiers();
        Object obj = target;
        if (Objects.nonNull(target) && Modifier.isStatic(mod)) {
            obj = null;
        }
        boolean canAccess = field.isAccessible();
        if (!Modifier.isPublic(mod) && !canAccess) {
            AccessibleObjectUtil.setAccessible(field);
        }
        try {
            Object value = field.get(obj);
            T result = ClassUtil.cast(value, type);
            return Optional.ofNullable(result);
        } catch (Exception e) {
            if (quietIfException) {
                return Optional.empty();
            }
            throw ThrowableUtil.throwUnchecked(e);
        } finally {
            if (!canAccess) {
                AccessibleObjectUtil.setInaccessible(field);
            }
        }
    }

    public static <T, R> Optional<R> getStaticValueOpt(final Class<T> clazz, final CharSequence fieldName, final Type type) {
        return getStaticValueOpt(clazz, fieldName, type, true);
    }

    public static <T, R> Optional<R> getStaticValueOpt(final Class<T> clazz, final CharSequence fieldName, final Type type, final boolean quietIfException) {
        return getValueOpt(clazz, false, null, fieldName, type, quietIfException);
    }

    public static <T, R> Optional<R> getValueOpt(final Class<T> clazz, final boolean withSuperClassFields, final T target, CharSequence fieldName, final Type type) {
        return getValueOpt(clazz, withSuperClassFields, target, fieldName, type, true);
    }

    public static <T, R> Optional<R> getValueOpt(final Class<T> clazz, final boolean withSuperClassFields, final T target, CharSequence fieldName, final Type type, final boolean quietIfException) {
        return getOptByName(Objects.isNull(clazz) ? target.getClass() : clazz, Objects.nonNull(target) && withSuperClassFields, fieldName).flatMap(field -> getValueOpt(field, target, type, quietIfException));
    }

    public static boolean setStaticValue(final Field field, final Object value) {
        return setStaticValue(field, value, true);
    }

    public static boolean setStaticValue(final Field field, final Object value, final boolean quietIfException) {
        return setValue(field, null, value, quietIfException);
    }

    public static boolean setValue(final Field field, final Object target, final Object value) {
        return setValue(field, target, value, true);
    }

    public static boolean setValue(final Field field, final Object target, final Object value, final boolean quietIfException) {
        int mod = field.getModifiers();
        Object obj = target;
        if (Objects.nonNull(target) && Modifier.isStatic(mod)) {
            obj = null;
        }
        boolean canAccess = field.isAccessible();
        if (!Modifier.isPublic(mod) && !canAccess) {
            AccessibleObjectUtil.setAccessible(field);
        }
        try {
            field.set(obj, value);
            return true;
        } catch (Exception e) {
            if (quietIfException) {
                return false;
            }
            throw ThrowableUtil.throwUnchecked(e);
        } finally {
            if (!canAccess) {
                AccessibleObjectUtil.setInaccessible(field);
            }
        }
    }

    public static <T> boolean setStaticValue(final Class<T> clazz, CharSequence fieldName, final Object value) {
        return setStaticValue(clazz, fieldName, value, true);
    }

    public static <T> boolean setStaticValue(final Class<T> clazz, CharSequence fieldName, final Object value, final boolean quietIfException) {
        return setValue(clazz, false, null, fieldName, value, quietIfException);
    }

    public static <T> boolean setValue(final Class<T> clazz, final boolean withSuperClassFields, final T target, CharSequence fieldName, final Object value) {
        return setValue(clazz, withSuperClassFields, target, fieldName, value, true);
    }

    public static <T> boolean setValue(final Class<T> clazz, final boolean withSuperClassFields, final T target, CharSequence fieldName, final Object value, final boolean quietIfException) {
        return getOptByName(Objects.isNull(clazz) ? target.getClass() : clazz, Objects.nonNull(target) && withSuperClassFields, fieldName).map(field -> setValue(field, target, value, quietIfException)).orElse(Boolean.FALSE);
    }

    /**
     * 从 Getter 方法中获取字段
     *
     * @param getterMethod Getter 方法
     * @return 字段名
     */
    public static Optional<Field> getOptFromGetterMethod(final Method getterMethod) {
        CharSequence fieldName = getNameFromGetterMethod(getterMethod);
        return getOptByName(getterMethod.getDeclaringClass(), fieldName);
    }

    /**
     * 从 Getter 方法中获取字段名
     *
     * @param getterMethod Getter 方法
     * @return 字段名
     */
    public static String getNameFromGetterMethod(final Method getterMethod) {
        String methodName = getterMethod.getName();
        if (methodName.startsWith(GETTER_METHOD_PREFIX)) {
            return StringUtil.removePrefixAndFirstToLower(methodName, GETTER_METHOD_PREFIX, false);
        }
        if (methodName.startsWith(BOOL_GETTER_METHOD_PREFIX)) {
            return StringUtil.removePrefixAndFirstToLower(methodName, BOOL_GETTER_METHOD_PREFIX, false);
        }
        return methodName;
    }

    public static boolean has(Class<?> clazz, CharSequence fieldName) {
        return getOptByName(clazz, fieldName).isPresent();
    }

    public static final String OUTER_CLASS_FIELD_NAME = "this$0";

    /**
     * 是否为父类引用字段
     * <p>
     * 当字段所在类是对象子类时（对象中定义的非 static 的 class），会自动生成一个以 {@code this$0} 为名称的字段，指向父类对象
     *
     * @param field 字段
     * @return 是否为父类引用字段
     */
    public static boolean isOuterClassField(Field field) {
        return OUTER_CLASS_FIELD_NAME.equals(field.getName());
    }

    /**
     * 获取实例字段
     * <p>
     * 实例字段要求：
     * <ol>
     *     <li>静态字段</li>
     *     <li>在本类</li>
     *     <li>字段类型</li>
     * </ol>
     *
     * @see MemberUtil#getInstanceOpt(Class, boolean, Object...)
     */
    protected static <T> Collection<Field> listInstance(Class<T> clazz) {
        return listStatic(clazz, false).stream()
                .filter(field -> ClassUtil.isAssignable(field.getType(), clazz))
                .collect(Collectors.toList());
    }

    protected FieldUtil() {
        throw new UnsupportedOperationException();
    }
}
