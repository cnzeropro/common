package org.zero.common.core.util.java.bean;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TemporalAccessorUtil;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.zero.common.core.util.java.ClassUtil;
import org.zero.common.core.util.spring.context.SpringContextUtils;
import org.zero.common.data.exception.UtilException;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * @author zero
 * @since 2019/8/9
 */
@Slf4j
@UtilityClass
public class BeanMapUtil {
    private static final String DEFAULT_PREFIX = "";
    private static final String DEFAULT_REGEX_TEMPLATE = "^(\\w+\\.)*(%s)(\\.\\w+)*$";
    private static final String DEFAULT_REGEX = String.format(DEFAULT_REGEX_TEMPLATE, "model|entity|domain|pojo");

    public Map<String, Object> encode(Object... objs) {
        return encode(DEFAULT_PREFIX, objs);
    }

    public Map<String, Object> encode(String prefix, Object... objs) {
        return encode(prefix, DEFAULT_REGEX, objs);
    }

    public Map<String, Object> encodeIn(String packageName, Object... objs) {
        return encodeIn(DEFAULT_PREFIX, packageName, objs);
    }

    public Map<String, Object> encodeIn(String prefix, String packageName, Object... objs) {
        String regex = String.format(DEFAULT_REGEX_TEMPLATE, packageName);
        return encode(prefix, regex, objs);
    }

    public Map<String, Object> encodeIn(String[] packageNames, Object... objs) {
        return encodeIn(DEFAULT_PREFIX, packageNames, objs);
    }

    public Map<String, Object> encodeIn(String prefix, String[] packageNames, Object... objs) {
        String packageNamesJoined = ArrayUtil.join(packageNames, "|");
        String regex = String.format(DEFAULT_REGEX_TEMPLATE, packageNamesJoined);
        return encode(prefix, regex, objs);
    }

    public Map<String, Object> encode(String prefix, String regex, Object... objs) {
        Map<String, Object> result = MapUtil.newHashMap(true);
        if (ArrayUtil.isAllNull(objs)) {
            return result;
        }

        for (Object obj : objs) {
            Map<String, Object> map = encode(prefix, regex, obj);
            result.putAll(map);
        }
        return result;
    }

    public Map<String, Object> encode(Object obj) {
        return encode(DEFAULT_PREFIX, obj);
    }

    public Map<String, Object> encode(String prefix, Object obj) {
        return encode(prefix, DEFAULT_REGEX, obj);
    }

    public Map<String, Object> encodeIn(String packageName, Object obj) {
        return encodeIn(DEFAULT_PREFIX, packageName, obj);
    }

    public Map<String, Object> encodeIn(String prefix, String packageName, Object obj) {
        String regex = String.format(DEFAULT_REGEX_TEMPLATE, packageName);
        return encode(prefix, regex, obj);
    }

    public Map<String, Object> encodeIn(String[] packageNames, Object obj) {
        return encode(DEFAULT_PREFIX, packageNames, obj);
    }

    public Map<String, Object> encodeIn(String prefix, String[] packageNames, Object obj) {
        String packageNamesJoined = ArrayUtil.join(packageNames, "|");
        String regex = String.format(DEFAULT_REGEX_TEMPLATE, packageNamesJoined);
        return encode(prefix, regex, obj);
    }

    public Map<String, Object> encode(String prefix, String regex, Object obj) {
        Map<String, Object> result = MapUtil.newHashMap(true);

        // 如果为空
        if (Objects.isNull(obj)) {
            return result;
        }

        Class<?> clazz = obj.getClass();

        // 如果是指定bean
        if (ClassUtil.isSpecifiedClass(clazz, regex)) {
            Field[] fields = ReflectUtil.getFields(clazz);
            for (Field field : fields) {
                Map<String, Object> map = encodeField(prefix, regex, obj, field);
                result.putAll(map);
            }
            return result;
        }

        // 如果是map
        // 此处为什么没有直接返回objMap？是因为考虑到map的value可能是其他类型，比如Bean，List，Map等等
        if (obj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<Object, Object> objMap = (Map<Object, Object>) obj;
            objMap.forEach((k, v) -> {
                String key = CharSequenceUtil.isBlank(prefix) ? String.valueOf(k) : CharSequenceUtil.format("{}.{}", prefix, k);
                Map<String, Object> map = encode(key, regex, v);
                result.putAll(map);
            });
            return result;
        }

        // 如果是可迭代的，如List
        if (obj instanceof Iterable || obj instanceof Iterator || obj instanceof Enumeration) {
            Map<String, Object> map = encodeIter(prefix, regex, obj);
            result.putAll(map);
            return result;
        }

        // 如果是数组
        if (ArrayUtil.isArray(obj)) {
            Map<String, Object> map = encodeArray(prefix, regex, obj);
            result.putAll(map);
            return map;
        }

        // 数字类型
        if (ClassUtil.isNumClass(clazz)) {
            String str = formatNum(null, obj);
            result.put(prefix, str);
            return result;
        }

        // 日期时间类型
        if (obj instanceof Date || obj instanceof Calendar || obj instanceof TemporalAccessor) {
            String str = formatDataTime(null, obj);
            result.put(prefix, str);
            return result;
        }

        // 其他类型
        result.put(prefix, String.valueOf(obj));
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> encodeIter(String prefix, String regex, Object iterObj) {
        Iterator<Object> iterator;
        if (iterObj instanceof Iterable) {
            Iterable<Object> iterable = (Iterable<Object>) iterObj;
            iterator = iterable.iterator();
        } else if (iterObj instanceof Iterator) {
            iterator = (Iterator<Object>) iterObj;
        } else {
            iterator = IterUtil.asIterator((Enumeration<Object>) iterObj);
        }
        // 为了公用一个方法，做了一点转换，有点性能浪费的感觉，后期优化吧
        Object[] objects = ArrayUtil.toArray(iterator, Object.class);
        return encodeArray(prefix, regex, objects);
    }

    private Map<String, Object> encodeArray(String prefix, String regex, Object arrayObj) {
        Map<String, Object> result = MapUtil.newHashMap(true);
        Class<?> componentType = ArrayUtil.getComponentType(arrayObj);
        Object[] objects = ArrayUtil.cast(componentType, arrayObj);

        // 数字类型
        if (ClassUtil.isNumClass(componentType)) {
            String joined = ArrayUtil.join(objects, ",", obj -> formatNum(null, obj));
            result.put(prefix, joined);
            return result;
        }
        // 日期时间类型
        if (Date.class.isAssignableFrom(componentType) || Calendar.class.isAssignableFrom(componentType) || TemporalAccessor.class.isAssignableFrom(componentType)) {
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
            Map<String, Object> map = encode(key, regex, objects[i]);
            result.putAll(map);
        }
        return result;
    }

    private Map<String, Object> encodeField(String prefix, String regex, Object object, Field field) {
        String key = CharSequenceUtil.isBlank(prefix) ? field.getName() : CharSequenceUtil.format("{}.{}", prefix, field.getName());
        Object fieldValue = ReflectUtil.getFieldValue(object, field);

        Map<String, Object> result = MapUtil.newHashMap(true);
        // 空值
        if (Objects.isNull(fieldValue)) {
            // 不添加空值
            // result.put(key, null);
            return result;
        }
        // 数字类型
        if (ClassUtil.isNumClass(fieldValue.getClass())) {
            String str = formatNum(field, fieldValue);
            result.put(prefix, str);
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
        Map<String, Object> map = encode(key, regex, fieldValue);
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

        String numStr = numObj.toString();
        if (CharSequenceUtil.isBlank(pattern)) {
            return numStr;
        } else {
            BigDecimal bigDecimal = NumberUtil.toBigDecimal(numStr);
            return NumberUtil.decimalFormat(pattern, bigDecimal);
        }
    }

    private String formatDataTime(Field field, Object dataTimeObj) {
        if (Objects.isNull(dataTimeObj)) {
            return null;
        }

        String pattern = Opt.ofNullable(field)
                .map(f -> AnnotationUtils.findAnnotation(f, DateTimeFormat.class))
                .map(DateTimeFormat::pattern)
                .or(() -> Opt.ofNullable(SpringContextUtils.getProperty("spring.mvc.format.date-time")))
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
        throw new UtilException(String.format("[%s]非日期时间类型，无法格式化", dataTimeObj.getClass()));
    }
}
