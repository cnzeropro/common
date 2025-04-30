package org.zero.common.core.util.mybatisplus.annotation;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.zero.common.core.support.cache.Cache;
import org.zero.common.core.support.cache.GuavaCache;
import org.zero.common.core.util.java.reflect.FieldUtil;
import org.zero.common.core.util.java.reflect.MethodUtil;
import org.zero.common.data.exception.UtilException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
    private static final Cache<Class<? extends Enum<?>>, Method> METHOD_CACHE = GuavaCache.of(10_000L);

    /**
     * 获取枚举对象的值
     */
    public static Object getValue(Enum<?> enumObj) {
        Method method = getMethod(enumObj.getDeclaringClass());
        return MethodUtil.invoke(method, enumObj);
    }

    /**
     * 获取枚举对象的值
     */
    public static <T> T getValue(Enum<?> enumObj, Class<T> clazz) {
        Object value = getValue(enumObj);
        return clazz.cast(value);
    }

    /**
     * 获取枚举对象的值的对应方法
     */
    public static Method getMethod(Class<? extends Enum<?>> enumClass) {
        return METHOD_CACHE.mapAndPutIfAbsent(enumClass, clazz -> {
            String className = clazz.getName();
            // 此处可使用自定义父类和注解，但因为Mp已经提供，所以无需重复造轮子
            if (IEnum.class.isAssignableFrom(clazz)) {
                return MethodUtil.getMethodOptByNameAndParam(clazz, "convert")
                        .orElseThrow(() -> new UtilException(String.format("No convert() method found in class[%s]", className)));
            } else {
                Field field = FieldUtil.getAnnotatedFields(clazz, EnumValue.class)
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new UtilException(String.format("No field with @EnumValue annotation found in class[%s]", className)));
                return MethodUtil.getGetterMethodOptByField(clazz, field)
                        .orElseThrow(() -> new UtilException(String.format("No needed method found in class[%s]", className)));
            }
        });
    }
}
