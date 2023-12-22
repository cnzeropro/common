package org.zero.common.core.util.mybatisplus;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.zero.common.data.exception.UtilException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2023/1/5
 */
@Slf4j
@UtilityClass
public class MpEnumUtil {
    /**
     * 方法缓存
     */
    private static final ConcurrentMap<Class<? extends Enum<?>>, Method> METHOD_MAP = new ConcurrentHashMap<>();

    @SneakyThrows
    public static Object invoke(Enum<?> enumObj) {
        Method method = MpEnumUtil.getMethod(enumObj.getDeclaringClass());
        return method.invoke(enumObj);
    }

    public static Method getMethod(Class<? extends Enum<?>> enumType) {
        return METHOD_MAP.computeIfAbsent(enumType, k -> {
            // 此处可使用自定义父类和注解，但因为Mp已经提供，所以无需重复造轮子
            if (IEnum.class.isAssignableFrom(k)) {
                return getMethodWithName(k, "getValue");
            } else {
                Field field = getAnnotatedFieldOpt(k, EnumValue.class).orElseThrow(() -> new UtilException(String.format("Class[%s] fields could not find @EnumValue", k.getName())));
                return getMethodWithField(k, field);
            }
        });
    }

    public static Optional<Field> getAnnotatedFieldOpt(Class<? extends Enum<?>> targetClass, Class<? extends Annotation> annotationClass) {
        return targetClass.isEnum() ? Arrays.stream(targetClass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(annotationClass)).findFirst() : Optional.empty();
    }

    private static Method getMethodWithField(Class<?> clazz, Field field) {
        String fieldName = field.getName();
        String methodName = getMethodName(fieldName);
        return getMethodWithName(clazz, methodName);
    }

    private static Method getMethodWithName(Class<?> clazz, String methodName) {
        try {
            return clazz.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            throw new UtilException(String.format("Class[%s] could not find %s()", clazz.getName(), methodName), e);
        }
    }

    private static String getMethodName(String name) {
        char firstChar = name.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            return "get" + Character.toUpperCase(firstChar) + name.substring(1);
        }
        return "get" + name;
    }
}
