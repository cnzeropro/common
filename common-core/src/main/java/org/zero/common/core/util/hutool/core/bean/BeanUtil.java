package org.zero.common.core.util.hutool.core.bean;

import cn.hutool.core.util.ArrayUtil;
import org.zero.common.core.util.java.reflect.ClassUtil;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/18
 */
public class BeanUtil extends cn.hutool.core.bean.BeanUtil {
    /**
     * 默认的 Bean 类名正则表达式模板
     * <p>
     * 注意：此处未限制层级数量，因此可能会出现过多层级导致性能问题。<br>
     * 如有需要可以尝试使用：<pre>{@code
     * public static final String DEFAULT_REGEX_TEMPLATE = "^(?:\\w+\\.){0,10}(%s)(?:\\.\\w+){0,10}$";
     * }</pre>
     * 此方案将：
     * <ul>
     *   <li>限制包路径和后缀的层级不超过 10 层</li>
     *   <li>避免因过长路径导致的正则回溯性能问题</li>
     * </ul>
     */
    public static final String DEFAULT_REGEX_TEMPLATE = "^(\\w+\\.)*(%s)(\\.\\w+)*$";
    /**
     * 默认的 Bean 类包层级名
     */
    public static final String[] DEFAULT_PACKAGE_LEVEL_NAMES = {"model", "entity", "domain", "pojo"};
    /**
     * 默认的 Bean 类名正则表达式
     */
    public static final String DEFAULT_REGEX = String.format(DEFAULT_REGEX_TEMPLATE, ArrayUtil.join(DEFAULT_PACKAGE_LEVEL_NAMES, "|"));

    /**
     * 判断是否为 Bean 对象
     * <p>
     * 不是所有人都能严格按照 Java Bean 规范来定义 Bean，因此此处只做基本的判断（是否满足传入的正则表达式）。
     *
     * @param obj     待验证的对象
     * @param regexps Bean 对象判别的正则表达式
     * @return 是否是 Bean
     * @see #DEFAULT_REGEX
     */
    public static boolean isBean(Object obj, String... regexps) {
        if (Objects.isNull(obj)) {
            return false;
        }
        return ClassUtil.isBean(obj.getClass(), regexps);
    }

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
