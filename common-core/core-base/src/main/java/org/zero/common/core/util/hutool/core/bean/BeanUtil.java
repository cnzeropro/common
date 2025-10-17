package org.zero.common.core.util.hutool.core.bean;

import org.zero.common.core.util.java.lang.ClassUtil;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/18
 */
public class BeanUtil extends cn.hutool.core.bean.BeanUtil {
    public static boolean isJavaStrictBean(Object obj) {
        if (Objects.isNull(obj)) {
            return false;
        }
        return ClassUtil.isJavaStrictBean(obj.getClass());
    }

    public static boolean setValue(Object obj, CharSequence expression, Object value) {
        return BeanPath.parse(expression).set(obj, value);
    }

    public static Object getValue(Object obj, CharSequence expression) {
        return BeanPath.parse(expression).get(obj);
    }

    public static <T> T getValue(Object obj, CharSequence expression, Class<T> clazz) {
        Object value = getValue(obj, expression);
        return ClassUtil.cast(value, clazz);
    }

    protected BeanUtil() {
        throw new UnsupportedOperationException();
    }
}
