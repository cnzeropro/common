package org.zero.common.core.util;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TemporalAccessorUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReflectUtil;
import feign.Param;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.zero.common.core.support.context.spring.SpringUtils;
import org.zero.common.core.util.hutool.core.bean.BeanUtil;
import org.zero.common.core.util.java.lang.ClassUtil;
import org.zero.common.data.exception.UtilException;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.zero.common.core.util.hutool.core.bean.BeanUtil.DEFAULT_REGEX;
import static org.zero.common.core.util.hutool.core.bean.BeanUtil.DEFAULT_REGEX_TEMPLATE;

/**
 * Bean Path Map 工具类
 * <table>
 *     <caption>所需依赖</caption>
 *     <tr>
 *         <th>GA</th>
 *         <th>说明</th>
 *     </tr>
 *     <tr>
 *         <td>cn.hutool:hutool-core</td>
 *         <td>借用某些工具类，如：集合、日期时间、反射工具类等等</td>
 *     </tr>
 *     <tr>
 *         <td>io.github.openfeign:feign-core</td>
 *         <td>支持 {@link Param} 注解</td>
 *     </tr>
 *     <tr>
 *         <td>org.springframework:spring-core</td>
 *         <td>借用工具类</td>
 *     </tr>
 *     <tr>
 *         <td>org.springframework:spring-context</td>
 *         <td>支持 {@link DateTimeFormat} 和 {@link NumberFormat} 注解</td>
 *     </tr>
 * </table>
 *
 * @author zero
 * @since 2019/8/9
 */
@Slf4j
@UtilityClass
public class BeanPathMapUtil {
    /**
     * 默认 Bean Path 前缀
     */
    public static final String DEFAULT_PREFIX = "";

    public Map<String, Object> toMap(Object... objs) {
        return toMap(true, objs);
    }

    public Map<String, Object> toMap(boolean ignoreNull, Object... objs) {
        return toMap(DEFAULT_PREFIX, ignoreNull, objs);
    }

    public Map<String, Object> toMap(String prefix, Object... objs) {
        return toMap(prefix, true, objs);
    }

    public Map<String, Object> toMap(String prefix, boolean ignoreNull, Object... objs) {
        return toMap(prefix, DEFAULT_REGEX, ignoreNull, objs);
    }

    public Map<String, Object> toMapIn(String packageLevelName, Object... objs) {
        return toMapIn(packageLevelName, true, objs);
    }

    public Map<String, Object> toMapIn(String packageLevelName, boolean ignoreNull, Object... objs) {
        return toMapIn(DEFAULT_PREFIX, packageLevelName, ignoreNull, objs);
    }

    public Map<String, Object> toMapIn(String prefix, String packageLevelName, Object... objs) {
        return toMapIn(prefix, packageLevelName, true, objs);
    }

    public Map<String, Object> toMapIn(String prefix, String packageLevelName, boolean ignoreNull, Object... objs) {
        String regex = String.format(DEFAULT_REGEX_TEMPLATE, packageLevelName);
        return toMap(prefix, regex, ignoreNull, objs);
    }

    public Map<String, Object> toMapIn(String[] packageLevelNames, Object... objs) {
        return toMapIn(packageLevelNames, true, objs);
    }

    public Map<String, Object> toMapIn(String[] packageLevelNames, boolean ignoreNull, Object... objs) {
        return toMapIn(DEFAULT_PREFIX, packageLevelNames, ignoreNull, objs);
    }

    public Map<String, Object> toMapIn(String prefix, String[] packageLevelNames, Object... objs) {
        return toMapIn(prefix, packageLevelNames, true, objs);
    }

    public Map<String, Object> toMapIn(String prefix, String[] packageLevelNames, boolean ignoreNull, Object... objs) {
        String packageLevelNamesJoined = ArrayUtil.join(packageLevelNames, "|");
        String regex = String.format(DEFAULT_REGEX_TEMPLATE, packageLevelNamesJoined);
        return toMap(prefix, regex, ignoreNull, objs);
    }

    public Map<String, Object> toMap(String prefix, String regex, Object... objs) {
        return toMap(prefix, regex, true, objs);
    }

    public Map<String, Object> toMap(String prefix, String regex, boolean ignoreNull, Object... objs) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (ArrayUtil.isEmpty(objs)) {
            return result;
        }

        for (Object obj : objs) {
            Map<String, Object> map = toMap(prefix, regex, ignoreNull, obj);
            result.putAll(map);
        }
        return result;
    }

    public Map<String, Object> toMap(Object obj) {
        return toMap(true, obj);
    }

    public Map<String, Object> toMap(boolean ignoreNull, Object obj) {
        return toMap(DEFAULT_PREFIX, ignoreNull, obj);
    }

    public Map<String, Object> toMap(String prefix, Object obj) {
        return toMap(prefix, true, obj);
    }

    public Map<String, Object> toMap(String prefix, boolean ignoreNull, Object obj) {
        return toMap(prefix, DEFAULT_REGEX, ignoreNull, obj);
    }

    public Map<String, Object> toMapIn(String packageLevelName, Object obj) {
        return toMapIn(packageLevelName, true, obj);
    }

    public Map<String, Object> toMapIn(String packageLevelName, boolean ignoreNull, Object obj) {
        return toMapIn(DEFAULT_PREFIX, packageLevelName, ignoreNull, obj);
    }

    public Map<String, Object> toMapIn(String prefix, String packageLevelName, Object obj) {
        return toMapIn(prefix, packageLevelName, true, obj);
    }

    public Map<String, Object> toMapIn(String prefix, String packageLevelName, boolean ignoreNull, Object obj) {
        String regex = String.format(DEFAULT_REGEX_TEMPLATE, packageLevelName);
        return toMap(prefix, regex, ignoreNull, obj);
    }

    public Map<String, Object> toMapIn(String[] packageLevelNames, Object obj) {
        return toMapIn(packageLevelNames, true, obj);
    }

    public Map<String, Object> toMapIn(String[] packageLevelNames, boolean ignoreNull, Object obj) {
        return toMapIn(DEFAULT_PREFIX, packageLevelNames, ignoreNull, obj);
    }

    public Map<String, Object> toMapIn(String prefix, String[] packageLevelNames, Object obj) {
        return toMapIn(prefix, packageLevelNames, true, obj);
    }

    public Map<String, Object> toMapIn(String prefix, String[] packageLevelNames, boolean ignoreNull, Object obj) {
        String packageLevelNamesJoined = ArrayUtil.join(packageLevelNames, "|");
        String regex = String.format(DEFAULT_REGEX_TEMPLATE, packageLevelNamesJoined);
        return toMap(prefix, regex, ignoreNull, obj);
    }

    public Map<String, Object> toMap(String prefix, String regex, Object obj) {
        return toMap(prefix, regex, true, obj);
    }

    public Map<String, Object> toMap(String prefix, String regex, boolean ignoreNull, Object obj) {
        Map<String, Object> result = new LinkedHashMap<>();

        // null 值
        if (Objects.isNull(obj)) {
            if (!ignoreNull) {
                result.put(prefix, null);
            }
            return result;
        }

        Class<?> clazz = obj.getClass();

        // 指定的 Bean
        if (BeanUtil.isBean(obj, regex)) {
            Field[] fields = ReflectUtil.getFields(clazz);
            for (Field field : fields) {
                Map<String, Object> map = toMapWithField(prefix, regex, obj, field, ignoreNull);
                result.putAll(map);
            }
            return result;
        }

        // map
        // 此处为什么没有直接返回 objMap？是因为考虑到 map 的 value 可能是其他类型，如：Bean，List，Map 等等
        if (obj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<Object, Object> objMap = (Map<Object, Object>) obj;
            objMap.forEach((k, v) -> {
                String key = CharSequenceUtil.isBlank(prefix) ? String.valueOf(k) : CharSequenceUtil.format("{}.{}", prefix, k);
                Map<String, Object> map = toMap(key, regex, v, ignoreNull);
                result.putAll(map);
            });
            return result;
        }

        // 集合，如：List、Set 等等
        if (obj instanceof Iterable || obj instanceof Iterator || obj instanceof Enumeration) {
            Map<String, Object> map = toMapWithIter(prefix, regex, obj);
            result.putAll(map);
            return result;
        }

        // 数组
        if (ArrayUtil.isArray(obj)) {
            Map<String, Object> map = toMapWithArray(prefix, regex, obj);
            result.putAll(map);
            return map;
        }

        // 数字
        if (ClassUtil.isNumber(clazz)) {
            String str = formatNum(null, obj);
            result.put(prefix, str);
            return result;
        }

        // 日期时间
        if (obj instanceof Date || obj instanceof Calendar || obj instanceof TemporalAccessor) {
            String str = formatDataTime(null, obj);
            result.put(prefix, str);
            return result;
        }

        // 其他
        result.put(prefix, String.valueOf(obj));
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> toMapWithIter(String prefix, String regex, Object iterObj) {
        Iterator<Object> iterator;
        if (iterObj instanceof Iterator) {
            iterator = (Iterator<Object>) iterObj;
        } else if (iterObj instanceof Iterable) {
            iterator = ((Iterable<Object>) iterObj).iterator();
        } else {
            iterator = IterUtil.asIterator((Enumeration<Object>) iterObj);
        }
        // 为了公用一个方法，做了一点转换，有点性能浪费
        Object[] objects = ArrayUtil.toArray(iterator, Object.class);
        return toMapWithArray(prefix, regex, objects);
    }

    private Map<String, Object> toMapWithArray(String prefix, String regex, Object arrayObj) {
        Map<String, Object> result = new LinkedHashMap<>();
        Class<?> componentType = ArrayUtil.getComponentType(arrayObj);
        Object[] objects = ArrayUtil.cast(componentType, arrayObj);

        // 数字类型
        if (ClassUtil.isNumber(componentType)) {
            String joined = ArrayUtil.join(objects, ",", obj -> formatNum(null, obj));
            result.put(prefix, joined);
            return result;
        }
        // 日期时间类型
        if (Date.class.isAssignableFrom(componentType) ||
                Calendar.class.isAssignableFrom(componentType) ||
                TemporalAccessor.class.isAssignableFrom(componentType)) {
            String joined = ArrayUtil.join(objects, ",", obj -> formatDataTime(null, obj));
            result.put(prefix, joined);
            return result;
        }
        // 简单值类型
        if (cn.hutool.core.util.ClassUtil.isSimpleValueType(componentType)) {
            String joined = ArrayUtil.join(objects, ",");
            result.put(prefix, joined);
            return result;
        }
        // 其他类型，比如bean、map等等
        for (int i = 0; i < objects.length; i++) {
            String key = CharSequenceUtil.format("{}[{}]", prefix, i);
            Map<String, Object> map = toMap(key, regex, objects[i]);
            result.putAll(map);
        }
        return result;
    }

    private Map<String, Object> toMapWithField(String prefix, String regex, Object object, Field field, boolean ignoreNull) {
        // 支持 feign Param 注解
        String name = Opt.ofNullable(field)
                .map(f -> AnnotationUtils.findAnnotation(f, Param.class))
                .map(Param::value)
                .or(() -> Opt.ofNullable(field).map(Field::getName))
                .orElse(null);

        String key = CharSequenceUtil.isBlank(prefix) ? name : CharSequenceUtil.format("{}.{}", prefix, name);
        Object fieldValue = ReflectUtil.getFieldValue(object, field);

        Map<String, Object> result = new LinkedHashMap<>();
        // null 值
        if (Objects.isNull(fieldValue)) {
            if (!ignoreNull) {
                result.put(key, null);
            }
            return result;
        }
        // 数字类型
        if (ClassUtil.isNumber(fieldValue.getClass())) {
            String str = formatNum(field, fieldValue);
            result.put(key, str);
            return result;
        }
        // 日期时间类型
        if (fieldValue instanceof Date ||
                fieldValue instanceof Calendar ||
                fieldValue instanceof TemporalAccessor) {
            String str = formatDataTime(field, fieldValue);
            result.put(key, str);
            return result;
        }
        // 其他类型
        Map<String, Object> map = toMap(key, regex, fieldValue);
        result.putAll(map);
        return result;
    }

    private String formatNum(Field field, Object numObj) {
        if (Objects.isNull(numObj)) {
            return null;
        }

        String pattern = Opt.ofNullable(field)
                .map(f -> AnnotationUtils.findAnnotation(f, NumberFormat.class))
                .map(NumberFormat::pattern)
                .orElse(null);

        if (CharSequenceUtil.isBlank(pattern)) {
            return numObj.toString();
        }
        return NumberUtil.decimalFormat(pattern, numObj);
    }

    private String formatDataTime(Field field, Object dataTimeObj) {
        if (Objects.isNull(dataTimeObj)) {
            return null;
        }

        String pattern = Opt.ofNullable(field)
                .map(f -> AnnotationUtils.findAnnotation(f, DateTimeFormat.class))
                .map(DateTimeFormat::pattern)
                .or(() -> Opt.ofNullable(SpringUtils.getProperty("spring.mvc.format.date-time")))
                .orElse(null);

        if (dataTimeObj instanceof Date) {
            if (CharSequenceUtil.isBlank(pattern)) {
                pattern = DatePattern.NORM_DATETIME_PATTERN;
            }
            return DateUtil.format((Date) dataTimeObj, pattern);
        }
        if (dataTimeObj instanceof Calendar) {
            if (CharSequenceUtil.isBlank(pattern)) {
                pattern = DatePattern.NORM_DATETIME_PATTERN;
            }
            return DateUtil.format(((Calendar) dataTimeObj).getTime(), pattern);
        }
        if (dataTimeObj instanceof TemporalAccessor) {
            if (CharSequenceUtil.isBlank(pattern)) {
                pattern = DatePattern.NORM_DATETIME_PATTERN;
                if (dataTimeObj instanceof LocalDate) {
                    pattern = DatePattern.NORM_DATE_PATTERN;
                }
                if (dataTimeObj instanceof LocalTime) {
                    pattern = DatePattern.NORM_TIME_PATTERN;
                }
            }
            return TemporalAccessorUtil.format((TemporalAccessor) dataTimeObj, pattern);
        }
        throw new UtilException(String.format("Not datetime type, cannot be formatted: %s", dataTimeObj.getClass()));
    }
}
