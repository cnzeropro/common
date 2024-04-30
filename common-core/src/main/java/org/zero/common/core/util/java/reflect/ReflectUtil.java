package org.zero.common.core.util.java.reflect;

import lombok.experimental.UtilityClass;
import org.zero.common.data.exception.UtilException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zero
 * @since 2021/4/30
 */
@UtilityClass
public class ReflectUtil {
    public static final String SETTER_METHOD_PREFIX = "set";
    public static final String GETTER_METHOD_PREFIX = "get";

    public static Optional<Field> getAnnotatedFieldOpt(Class<? extends Enum<?>> targetClass, Class<? extends Annotation> annotationClass) {
        return getAnnotatedFields(targetClass, annotationClass).stream().findFirst();
    }

    public static List<Field> getAnnotatedFields(Class<? extends Enum<?>> targetClass, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(targetClass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(annotationClass)).collect(Collectors.toList());
    }

    public static Method getMethodByField(Class<?> clazz, Field field) {
        String fieldName = field.getName();
        String methodName = getMethodNameByFieldName(fieldName);
        return getMethodByName(clazz, methodName);
    }

    public static Method getMethodByName(Class<?> clazz, String methodName) {
        try {
            return clazz.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            throw new UtilException(String.format("Class[%s] could not find %s()", clazz.getName(), methodName), e);
        }
    }

    public static String getMethodNameByFieldName(String fieldName) {
        char firstChar = fieldName.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            return String.format("%s%s%s", GETTER_METHOD_PREFIX, Character.toUpperCase(firstChar), fieldName.substring(1));
        }
        return String.format("%s%s", GETTER_METHOD_PREFIX, fieldName);
    }
}
