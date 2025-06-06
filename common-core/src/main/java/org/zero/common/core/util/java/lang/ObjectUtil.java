package org.zero.common.core.util.java.lang;

import org.zero.common.core.util.java.util.EnumerationUtil;
import org.zero.common.core.util.java.util.IteratorUtil;

import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/1
 */
public class ObjectUtil {
    public static boolean isNull(Object object) {
        return Objects.isNull(object);
    }

    public static boolean nonNull(Object object) {
        return !isNull(object);
    }

    public static boolean isEmpty(Object object) {
        if (isNull(object)) {
            return true;
        }
        if (object instanceof CharSequence) {
            return CharSequenceUtil.isEmpty((CharSequence) object);
        }
        if (ArrayUtil.isArray(object)) {
            return ArrayUtil.isEmpty(ArrayUtil.of(object));
        }
        if (object instanceof Collection) {
            return !((Collection<?>) object).isEmpty();
        }
        if (object instanceof Map) {
            return ((Map<?, ?>) object).isEmpty();
        }
        if (object instanceof Iterable) {
            return IterableUtil.isEmpty((Iterable<?>) object);
        }
        if (object instanceof Iterator) {
            return IteratorUtil.isEmpty((Iterator<?>) object);
        }
        if (object instanceof Enumeration) {
            return EnumerationUtil.isEmpty((Enumeration<?>) object);
        }
        return false;
    }

    public static boolean nonEmpty(Object object) {
        return !isEmpty(object);
    }

    public static boolean isNumber(Object object) {
        return object instanceof Number;
    }

    public static boolean isDatetime(Object object) {
        return object instanceof Date || object instanceof Calendar || object instanceof TemporalAccessor;
    }

    public static boolean isMultivariable(Object object) {
        return ArrayUtil.isArray(object) || object instanceof Iterable || object instanceof Iterator || object instanceof Enumeration;
    }

    public static <T> T defaultIfNull(final T object, final T defaultValue) {
        return isNull(object) ? defaultValue : object;
    }

    public static <T> T defaultIfEmpty(final T object, final T defaultValue) {
        return isEmpty(object) ? defaultValue : object;
    }

    public static <T, R> R mapIfNonNull(final T object, final Function<T, R> mapper) {
        return nonNull(object) ? mapper.apply(object) : null;
    }

    public static <T, R> R mapIfNonEmpty(final T object, final Function<T, R> mapper) {
        return nonEmpty(object) ? mapper.apply(object) : null;
    }

    protected ObjectUtil() {
        throw new UnsupportedOperationException();
    }
}
