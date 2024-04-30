package org.zero.common.core.util.mybatisplus;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.zero.common.core.util.java.reflect.ReflectUtil;
import org.zero.common.data.exception.UtilException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
    private static final ConcurrentMap<Class<? extends Enum<?>>, Method> METHOD_CACHE = new ConcurrentHashMap<>();

    @SneakyThrows(Exception.class)
    public static Object invoke(Enum<?> enumObj) {
        Method method = getMethod(enumObj.getDeclaringClass());
        return method.invoke(enumObj);
    }

    public static <T> T invoke(Enum<?> enumObj, Class<T> clazz) {
        Object value = invoke(enumObj);
        return clazz.cast(value);
    }

    public static Method getMethod(Class<? extends Enum<?>> enumClass) {
        return METHOD_CACHE.computeIfAbsent(enumClass, clazz -> {
            // 此处可使用自定义父类和注解，但因为Mp已经提供，所以无需重复造轮子
            if (IEnum.class.isAssignableFrom(clazz)) {
                return ReflectUtil.getMethodByName(clazz, "getValue");
            } else {
                Field field = ReflectUtil.getAnnotatedFieldOpt(clazz, EnumValue.class).orElseThrow(() -> new UtilException(String.format("No field with @EnumValue annotation found in class[%s]", clazz.getName())));
                return ReflectUtil.getMethodByField(clazz, field);
            }
        });
    }
}
