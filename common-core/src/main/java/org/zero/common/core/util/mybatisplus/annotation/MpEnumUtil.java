package org.zero.common.core.util.mybatisplus.annotation;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.zero.common.core.support.cache.Cache;
import org.zero.common.core.support.cache.GuavaCache;
import org.zero.common.core.util.java.lang.reflect.FieldUtil;
import org.zero.common.core.util.java.lang.reflect.MethodUtil;
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
            // 此处可使用自定义父类和注解，但因为 Mp 已经提供，所以无需重复造轮子
            if (IEnum.class.isAssignableFrom(clazz)) {
                return MethodUtil.getOptByNameAndParam(clazz, true, "getValue")
                        .orElseThrow(() -> new UtilException(String.format("No getValue() method found in %s", clazz)));
            } else {
                Field field = FieldUtil.listAnnotated(clazz, EnumValue.class)
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new UtilException(String.format("No field with @EnumValue annotation found in %s", clazz)));
                return MethodUtil.GetterOrSetterQuerier.create(MethodUtil.GetterOrSetterQuerier.Mode.GETTER)
                        .targetClass(clazz)
                        .field(field)
                        .queryOpt()
                        .orElseThrow(() -> new UtilException(String.format("No needed targetMethod found in %s", clazz)));
            }
        });
    }
}
