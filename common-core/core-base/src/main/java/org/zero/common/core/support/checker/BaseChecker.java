package org.zero.common.core.support.checker;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_PATTERN;

/**
 * @author Zero
 * @since 2023/4/19
 */
public abstract class BaseChecker {
    /* **************************************************************** 基础检查 **************************************************************** */

    protected boolean assertTrue(Boolean val) {
        return Boolean.TRUE.equals(val);
    }

    protected boolean assertFalse(Boolean val) {
        return Boolean.FALSE.equals(val);
    }

    protected <T> boolean isNull(T val) {
        return Objects.isNull(val);
    }

    protected <T> boolean notNull(T val) {
        return Objects.nonNull(val);
    }

    protected <T> boolean equals(T val, Object other) {
        return ObjectUtil.equals(val, other);
    }

    protected <T> boolean notEquals(T val, Object other) {
        return ObjectUtil.notEqual(val, other);
    }

    protected <T> boolean deepEquals(T val, Object other) {
        if (ArrayUtil.isArray(val) && ArrayUtil.isArray(other)) {
            return ArrayUtil.equals(val, other);
        }
        if (val instanceof Iterable && other instanceof Iterable) {
            return IterUtil.isEqualList((Iterable<?>) val, (Iterable<?>) other);
        }
        return equals(val, other);
    }

    protected <T extends Comparable<T>> boolean equal(T val, T other) {
        return val.compareTo(other) == 0;
    }

    protected <T extends Comparable<T>> boolean notEqual(T val, T other) {
        return val.compareTo(other) != 0;
    }

    protected <T extends Comparable<T>> boolean less(T val, T other) {
        return val.compareTo(other) < 0;
    }

    protected <T extends Comparable<T>> boolean equalOrLess(T val, T other) {
        return val.compareTo(other) <= 0;
    }

    protected <T extends Comparable<T>> boolean greater(T val, T other) {
        return val.compareTo(other) > 0;
    }

    protected <T extends Comparable<T>> boolean equalOrGreater(T val, T other) {
        return val.compareTo(other) >= 0;
    }

    protected <T> boolean isEmpty(T val) {
        return isEmpty0(val).orElse(Boolean.FALSE);
    }

    protected <T> boolean notEmpty(T val) {
        return !isEmpty0(val).orElse(Boolean.TRUE);
    }

    private <T> Optional<Boolean> isEmpty0(T val) {
        if (val instanceof Map) {
            return Optional.of(isEmpty((Map<?, ?>) val));
        }
        if (val instanceof Iterable) {
            return Optional.of(isEmpty((Iterable<?>) val));
        }
        if (val instanceof Iterator) {
            return Optional.of(isEmpty((Iterator<?>) val));
        }
        if (val instanceof Enumeration) {
            return Optional.of(isEmpty((Enumeration<?>) val));
        }
        if (val instanceof CharSequence) {
            return Optional.of(isEmpty((CharSequence) val));
        }
        if (ArrayUtil.isArray(val)) {
            return Optional.of(isEmpty(val));
        }
        return Optional.empty();
    }

    protected <T> boolean size(T val, long min, long max) {
        if (val instanceof Map) {
            return size((Map<?, ?>) val, min, max);
        }
        if (val instanceof Iterable) {
            return size((Iterable<?>) val, min, max);
        }
        if (val instanceof Iterator) {
            return size((Iterator<?>) val, min, max);
        }
        if (val instanceof Enumeration) {
            return size((Enumeration<?>) val, min, max);
        }
        if (val instanceof CharSequence) {
            return length((CharSequence) val, min, max);
        }
        if (ArrayUtil.isArray(val)) {
            return length((Object[]) val, min, max);
        }
        return false;
    }

    /* **************************************************************** 字符序列检查 **************************************************************** */

    protected <T extends CharSequence> boolean isEmpty(T val) {
        return CharSequenceUtil.isEmpty(val);
    }

    protected <T extends CharSequence> boolean notEmpty(T val) {
        return CharSequenceUtil.isNotEmpty(val);
    }

    protected <T extends CharSequence> boolean isBlack(T val) {
        return CharSequenceUtil.isBlank(val);
    }

    protected <T extends CharSequence> boolean notBlack(T val) {
        return CharSequenceUtil.isNotBlank(val);
    }

    protected <T extends CharSequence> boolean length(T val, long min, long max) {
        return notNull(val) && between(val.length(), min, max);
    }

    protected <T extends CharSequence> boolean equalsIgnoreCase(T val, CharSequence other) {
        return CharSequenceUtil.equalsIgnoreCase(val, other);
    }

    protected <T extends CharSequence> boolean startWith(T val, CharSequence item) {
        return CharSequenceUtil.startWith(val, item);
    }

    protected <T extends CharSequence> boolean endWith(T val, CharSequence item) {
        return CharSequenceUtil.endWith(val, item);
    }

    protected <T extends CharSequence> boolean containsAny(T val, CharSequence... substrings) {
        return CharSequenceUtil.containsAny(val, substrings);
    }

    /* **************************************************************** 数组检查 **************************************************************** */
    protected <T> boolean isEmpty(T[] val) {
        return ArrayUtil.isEmpty(val);
    }

    protected <T> boolean notEmpty(T[] val) {
        return ArrayUtil.isNotEmpty(val);
    }

    protected <T> boolean length(T[] val, long min, long max) {
        return notNull(val) && between(val.length, min, max);
    }

    @SuppressWarnings("unchecked")
    protected <T> boolean containsAny(T[] val, T... items) {
        return ArrayUtil.containsAny(val, items);
    }

    /* **************************************************************** 集合检查 **************************************************************** */

    protected <K, V, T extends Map<K, V>> boolean isEmpty(T val) {
        return MapUtil.isEmpty(val);
    }

    protected <K, V, T extends Map<K, V>> boolean notEmpty(T val) {
        return MapUtil.isNotEmpty(val);
    }

    protected <E, T extends Iterable<E>> boolean isEmpty(T val) {
        return IterUtil.isEmpty(val);
    }

    protected <E, T extends Iterable<E>> boolean notEmpty(T val) {
        return IterUtil.isNotEmpty(val);
    }

    protected <E, T extends Iterator<E>> boolean isEmpty(T val) {
        return IterUtil.isEmpty(val);
    }

    protected <E, T extends Iterator<E>> boolean notEmpty(T val) {
        return IterUtil.isNotEmpty(val);
    }

    protected <E, T extends Enumeration<E>> boolean isEmpty(T val) {
        return notNull(val) && isEmpty(IterUtil.asIterator(val));
    }

    protected <E, T extends Enumeration<E>> boolean notEmpty(T val) {
        return !isEmpty(val);
    }

    protected <K, V, T extends Map<K, V>> boolean size(T val, long min, long max) {
        return notNull(val) && between(val.size(), min, max);
    }

    protected <E, T extends Iterable<E>> boolean size(T val, long min, long max) {
        return notNull(val) && between(IterUtil.size(val), min, max);
    }

    protected <E, T extends Iterator<E>> boolean size(T val, long min, long max) {
        return notNull(val) && between(IterUtil.size(val), min, max);
    }

    protected <E, T extends Enumeration<E>> boolean size(T val, long min, long max) {
        return notNull(val) && size(IterUtil.asIterator(val), min, max);
    }

    @SuppressWarnings("unchecked")
    protected <K, V, T extends Map<K, V>> boolean containsAnyKey(T val, K... keys) {
        return notEmpty(val) && containsAny(val.keySet(), keys);
    }

    @SuppressWarnings("unchecked")
    protected <K, V, T extends Map<K, V>> boolean containsAnyValue(T val, V... values) {
        return notEmpty(val) && containsAny(val.values(), values);
    }

    @SuppressWarnings("unchecked")
    protected <K, V, T extends Map<K, V>> boolean containsAny(T val, Map.Entry<K, V>... entries) {
        return notEmpty(val) && containsAny(val.entrySet(), entries);
    }

    @SuppressWarnings("unchecked")
    protected <K, V, T extends Map<K, V>> boolean containsAny(T val, K[] keys, V[] values) {
        return containsAnyKey(val, keys) && containsAnyValue(val, values);
    }

    @SuppressWarnings("unchecked")
    protected <E, T extends Iterable<E>> boolean containsAny(T val, E... items) {
        if (val instanceof Collection) {
            return CollUtil.containsAny((Collection<?>) val, CollUtil.newHashSet(items));
        }
        return containsAny(IterUtil.getIter(val), items);
    }

    @SuppressWarnings("unchecked")
    protected <E, T extends Iterator<E>> boolean containsAny(T val, E... items) {
        if (isNull(val) || isNull(items)) {
            return false;
        }

        boolean isFound = false;
        while (val.hasNext() && !isFound) {
            E element = val.next();
            for (E item : items) {
                if (equals(element, item)) {
                    isFound = true;
                }
            }
        }
        return isFound;
    }

    @SuppressWarnings("unchecked")
    protected <E, T extends Enumeration<E>> boolean containsAny(T val, E... items) {
        return notNull(val) && containsAny(IterUtil.asIterator(val), items);
    }

    /* **************************************************************** 数字检查 **************************************************************** */

    protected <T extends Number> boolean less(T val, long max) {
        return notNull(val) && val.longValue() < max;
    }

    protected <T extends Number> boolean less(T val, double max) {
        return notNull(val) && val.doubleValue() < max;
    }

    protected <T extends BigDecimal> boolean less(T val, BigDecimal max) {
        return CompareUtil.compare(val, max) < 0;
    }

    protected <T extends BigInteger> boolean less(T val, BigInteger max) {
        return CompareUtil.compare(val, max) < 0;
    }

    protected <T extends Number> boolean greater(T val, long min) {
        return notNull(val) && val.doubleValue() > min;
    }

    protected <T extends Number> boolean greater(T val, double min) {
        return notNull(val) && val.doubleValue() > min;
    }

    protected <T extends BigDecimal> boolean greater(T val, BigDecimal min) {
        return CompareUtil.compare(val, min) > 0;
    }

    protected <T extends BigInteger> boolean greater(T val, BigInteger min) {
        return CompareUtil.compare(val, min) > 0;
    }

    protected <T extends Number> boolean min(T val, long min) {
        return notNull(val) && val.longValue() >= min;
    }

    protected <T extends Number> boolean min(T val, double min) {
        return notNull(val) && val.doubleValue() >= min;
    }

    protected <T extends BigDecimal> boolean min(T val, BigDecimal min) {
        return CompareUtil.compare(val, min) >= 0;
    }

    protected <T extends BigInteger> boolean min(T val, BigInteger min) {
        return CompareUtil.compare(val, min) >= 0;
    }

    protected <T extends Number> boolean max(T val, long max) {
        return notNull(val) && val.doubleValue() <= max;
    }

    protected <T extends Number> boolean max(T val, double max) {
        return notNull(val) && val.doubleValue() <= max;
    }

    protected <T extends BigDecimal> boolean max(T val, BigDecimal max) {
        return CompareUtil.compare(val, max) <= 0;
    }

    protected <T extends BigInteger> boolean max(T val, BigInteger max) {
        return CompareUtil.compare(val, max) <= 0;
    }

    protected <T extends Number> boolean negative(T val) {
        if (val instanceof Float || val instanceof Double) {
            return less(val, 0.0);
        }
        if (val instanceof BigDecimal) {
            return less((BigDecimal) val, BigDecimal.ZERO);
        }
        if (val instanceof BigInteger) {
            return less((BigInteger) val, BigInteger.ZERO);
        }
        return less(val, 0L);
    }

    protected <T extends Number> boolean negativeOrZero(T val) {
        if (val instanceof Float || val instanceof Double) {
            return max(val, 0.0);
        }
        if (val instanceof BigDecimal) {
            return max((BigDecimal) val, BigDecimal.ZERO);
        }
        if (val instanceof BigInteger) {
            return max((BigInteger) val, BigInteger.ZERO);
        }
        return max(val, 0L);
    }

    protected <T extends Number> boolean positive(T val) {
        return !negativeOrZero(val);
    }

    protected <T extends Number> boolean positiveOrZero(T val) {
        return !negative(val);
    }

    protected <T extends Number> boolean between(T val, long min, long max) {
        return min(val, min) && max(val, max);
    }

    protected <T extends Number> boolean between(T val, double min, double max) {
        return min(val, min) && max(val, max);
    }

    protected <T extends BigDecimal> boolean between(T val, BigDecimal min, BigDecimal max) {
        return min(val, min) && max(val, max);
    }

    protected <T extends BigInteger> boolean between(T val, BigInteger min, BigInteger max) {
        return min(val, min) && max(val, max);
    }

    /* **************************************************************** 日期、时间检查 **************************************************************** */

    protected <T> boolean past(T val) {
        return past(val, NORM_DATETIME_PATTERN);
    }

    protected <T> boolean past(T val, String format) {
        return !presentOrFuture0(val, format).orElse(Boolean.TRUE);
    }

    protected <T extends Date> boolean past(T val) {
        return past(val, NORM_DATETIME_PATTERN);
    }

    protected <T extends Date> boolean past(T val, String format) {
        return pastThan(val, DateTime.now(), format);
    }

    protected <T extends Date> boolean pastThan(T val, Date date) {
        return pastThan(val, date, NORM_DATETIME_PATTERN);
    }

    protected <T extends Date> boolean pastThan(T val, Date date, String format) {
        return DateUtil.compare(val, date, format) < 0;
    }

    protected <T extends Calendar> boolean past(T val) {
        return past(DateUtil.date(val));
    }

    protected <T extends Calendar> boolean past(T val, String format) {
        return past(DateUtil.date(val), format);
    }

    protected <T extends Calendar> boolean pastThan(T val, Calendar calendar) {
        return pastThan(DateUtil.date(val), DateUtil.date(calendar));
    }

    protected <T extends Calendar> boolean pastThan(T val, Calendar calendar, String format) {
        return pastThan(DateUtil.date(val), DateUtil.date(calendar), format);
    }

    protected <T extends TemporalAccessor> boolean past(T val) {
        return past(DateUtil.date(val));
    }

    protected <T extends TemporalAccessor> boolean past(T val, String format) {
        return past(DateUtil.date(val), format);
    }

    protected <T extends TemporalAccessor> boolean pastThan(T val, TemporalAccessor temporalAccessor) {
        return pastThan(DateUtil.date(val), DateUtil.date(temporalAccessor));
    }

    protected <T extends TemporalAccessor> boolean pastThan(T val, TemporalAccessor temporalAccessor, String format) {
        return pastThan(DateUtil.date(val), DateUtil.date(temporalAccessor), format);
    }

    protected <T> boolean presentOrPast(T val) {
        return presentOrPast(val, NORM_DATETIME_PATTERN);
    }

    protected <T> boolean presentOrPast(T val, String format) {
        return !future0(val, format).orElse(Boolean.TRUE);
    }

    protected <T extends Date> boolean presentOrPast(T val) {
        return present(val) || past(val);
    }

    protected <T extends Date> boolean presentOrPast(T val, String format) {
        return present(val, format) || past(val, format);
    }

    protected <T extends Date> boolean presentOrPastThan(T val, Date date) {
        return presentOn(val, date) || pastThan(val, date);
    }

    protected <T extends Date> boolean presentOrPastThan(T val, Date date, String format) {
        return presentOn(val, date, format) || pastThan(val, date, format);
    }

    protected <T extends Calendar> boolean presentOrPast(T val) {
        return present(val) || past(val);
    }

    protected <T extends Calendar> boolean presentOrPast(T val, String format) {
        return present(val, format) || past(val, format);
    }

    protected <T extends Calendar> boolean presentOrPastThan(T val, Calendar calendar) {
        return presentOn(val, calendar) || pastThan(val, calendar);
    }

    protected <T extends Calendar> boolean presentOrPastThan(T val, Calendar calendar, String format) {
        return presentOn(val, calendar, format) || pastThan(val, calendar, format);
    }

    protected <T extends TemporalAccessor> boolean presentOrPast(T val) {
        return present(val) || past(val);
    }

    protected <T extends TemporalAccessor> boolean presentOrPast(T val, String format) {
        return present(val, format) || past(val, format);
    }

    protected <T extends TemporalAccessor> boolean presentOrPastThan(T val, TemporalAccessor temporalAccessor) {
        return presentOn(val, temporalAccessor) || pastThan(val, temporalAccessor);
    }

    protected <T extends TemporalAccessor> boolean presentOrPastThan(T val, TemporalAccessor temporalAccessor, String format) {
        return presentOn(val, temporalAccessor, format) || pastThan(val, temporalAccessor, format);
    }

    protected <T> boolean present(T val) {
        return present(val, NORM_DATETIME_PATTERN);
    }

    protected <T> boolean present(T val, String format) {
        return present0(val, format).orElse(Boolean.FALSE);
    }

    private <T> Optional<Boolean> present0(T val, String format) {
        if (val instanceof Date) {
            return Optional.of(present((Date) val, format));
        }
        if (val instanceof Calendar) {
            return Optional.of(present((Calendar) val, format));
        }
        if (val instanceof TemporalAccessor) {
            return Optional.of(present((TemporalAccessor) val, format));
        }
        return Optional.empty();
    }

    protected <T extends Date> boolean present(T val) {
        return presentOn(val, DateTime.now());
    }

    protected <T extends Date> boolean present(T val, String format) {
        return presentOn(val, DateTime.now(), format);
    }

    protected <T extends Date> boolean presentOn(T val, Date date) {
        return presentOn(val, date, NORM_DATETIME_PATTERN);
    }

    protected <T extends Date> boolean presentOn(T val, Date date, String format) {
        return DateUtil.compare(val, date, format) == 0;
    }

    protected <T extends Calendar> boolean present(T val) {
        return present(DateUtil.date(val));
    }

    protected <T extends Calendar> boolean present(T val, String format) {
        return present(DateUtil.date(val), format);
    }

    protected <T extends Calendar> boolean presentOn(T val, Calendar calendar) {
        return presentOn(DateUtil.date(val), DateUtil.date(calendar));
    }

    protected <T extends Calendar> boolean presentOn(T val, Calendar calendar, String format) {
        return presentOn(DateUtil.date(val), DateUtil.date(calendar), format);
    }

    protected <T extends TemporalAccessor> boolean present(T val) {
        return present(DateUtil.date(val));
    }

    protected <T extends TemporalAccessor> boolean present(T val, String format) {
        return present(DateUtil.date(val), format);
    }

    protected <T extends TemporalAccessor> boolean presentOn(T val, TemporalAccessor temporalAccessor) {
        return presentOn(DateUtil.date(val), DateUtil.date(temporalAccessor));
    }

    protected <T extends TemporalAccessor> boolean presentOn(T val, TemporalAccessor temporalAccessor, String format) {
        return presentOn(DateUtil.date(val), DateUtil.date(temporalAccessor), format);
    }

    protected <T> boolean notPresent(T val) {
        return notPresent(val, NORM_DATETIME_PATTERN);
    }

    protected <T> boolean notPresent(T val, String format) {
        return !present0(val, format).orElse(Boolean.TRUE);
    }

    protected <T> boolean future(T val) {
        return future(val, NORM_DATETIME_PATTERN);
    }

    protected <T> boolean future(T val, String format) {
        return future0(val, format).orElse(Boolean.FALSE);
    }

    private <T> Optional<Boolean> future0(T val, String format) {
        if (val instanceof Date) {
            return Optional.of(future((Date) val, format));
        }
        if (val instanceof Calendar) {
            return Optional.of(future((Calendar) val, format));
        }
        if (val instanceof TemporalAccessor) {
            return Optional.of(future((TemporalAccessor) val, format));
        }
        return Optional.empty();
    }

    protected <T extends Date> boolean future(T val) {
        return futureThan(val, DateTime.now());
    }

    protected <T extends Date> boolean future(T val, String format) {
        return futureThan(val, DateTime.now(), format);
    }

    protected <T extends Date> boolean futureThan(T val, Date date) {
        return futureThan(val, date, NORM_DATETIME_PATTERN);
    }

    protected <T extends Date> boolean futureThan(T val, Date date, String format) {
        return DateUtil.compare(val, date, format) > 0;
    }

    protected <T extends Calendar> boolean future(T val) {
        return future(DateUtil.date(val));
    }

    protected <T extends Calendar> boolean future(T val, String format) {
        return future(DateUtil.date(val), format);
    }

    protected <T extends Calendar> boolean futureThan(T val, Calendar calendar) {
        return futureThan(DateUtil.date(val), DateUtil.date(calendar));
    }

    protected <T extends Calendar> boolean futureThan(T val, Calendar calendar, String format) {
        return futureThan(DateUtil.date(val), DateUtil.date(calendar), format);
    }

    protected <T extends TemporalAccessor> boolean future(T val) {
        return future(DateUtil.date(val));
    }

    protected <T extends TemporalAccessor> boolean future(T val, String format) {
        return future(DateUtil.date(val), format);
    }

    protected <T extends TemporalAccessor> boolean futureThan(T val, TemporalAccessor temporalAccessor) {
        return futureThan(DateUtil.date(val), DateUtil.date(temporalAccessor));
    }

    protected <T extends TemporalAccessor> boolean futureThan(T val, TemporalAccessor temporalAccessor, String format) {
        return futureThan(DateUtil.date(val), DateUtil.date(temporalAccessor), format);
    }

    protected <T> boolean presentOrFuture(T val) {
        return presentOrFuture(val, NORM_DATETIME_PATTERN);
    }

    protected <T> boolean presentOrFuture(T val, String format) {
        return presentOrFuture0(val, format).orElse(Boolean.FALSE);
    }

    private <T> Optional<Boolean> presentOrFuture0(T val, String format) {
        if (val instanceof Date) {
            return Optional.of(presentOrFuture((Date) val, format));
        }
        if (val instanceof Calendar) {
            return Optional.of(presentOrFuture((Calendar) val, format));
        }
        if (val instanceof TemporalAccessor) {
            return Optional.of(presentOrFuture((TemporalAccessor) val, format));
        }
        return Optional.empty();
    }

    protected <T extends Date> boolean presentOrFuture(T val) {
        return present(val) || future(val);
    }

    protected <T extends Date> boolean presentOrFuture(T val, String format) {
        return present(val, format) || future(val, format);
    }

    protected <T extends Date> boolean presentOrFutureThan(T val, Date date) {
        return presentOn(val, date) || futureThan(val, date);
    }

    protected <T extends Date> boolean presentOrFutureThan(T val, Date date, String format) {
        return presentOn(val, date, format) || futureThan(val, date, format);
    }

    protected <T extends Calendar> boolean presentOrFuture(T val) {
        return present(val) || future(val);
    }

    protected <T extends Calendar> boolean presentOrFuture(T val, String format) {
        return present(val, format) || future(val, format);
    }

    protected <T extends Calendar> boolean presentOrFutureThan(T val, Calendar calendar) {
        return presentOn(val, calendar) || futureThan(val, calendar);
    }

    protected <T extends Calendar> boolean presentOrFutureThan(T val, Calendar calendar, String format) {
        return presentOn(val, calendar, format) || futureThan(val, calendar, format);
    }

    protected <T extends TemporalAccessor> boolean presentOrFuture(T val) {
        return present(val) || future(val);
    }

    protected <T extends TemporalAccessor> boolean presentOrFuture(T val, String format) {
        return present(val, format) || future(val, format);
    }

    protected <T extends TemporalAccessor> boolean presentOrFutureThan(T val, TemporalAccessor temporalAccessor) {
        return presentOn(val, temporalAccessor) || futureThan(val, temporalAccessor);
    }

    protected <T extends TemporalAccessor> boolean presentOrFutureThan(T val, TemporalAccessor temporalAccessor, String format) {
        return presentOn(val, temporalAccessor, format) || futureThan(val, temporalAccessor, format);
    }

    protected <T extends Date> boolean between(T val, Date min, Date max) {
        return presentOrPastThan(val, max) && presentOrFutureThan(val, min);
    }

    protected <T extends Calendar> boolean between(T val, Calendar min, Calendar max) {
        return presentOrPastThan(val, max) && presentOrFutureThan(val, min);
    }

    protected <T extends TemporalAccessor> boolean between(T val, TemporalAccessor min, TemporalAccessor max) {
        return presentOrPastThan(val, max) && presentOrFutureThan(val, min);
    }

    protected <T extends Date> boolean between(T val, Date min, Date max, String format) {
        return presentOrPastThan(val, max, format) && presentOrFutureThan(val, min, format);
    }

    protected <T extends Calendar> boolean between(T val, Calendar min, Calendar max, String format) {
        return presentOrPastThan(val, max, format) && presentOrFutureThan(val, min, format);
    }

    protected <T extends TemporalAccessor> boolean between(T val, TemporalAccessor min, TemporalAccessor max, String format) {
        return presentOrPastThan(val, max, format) && presentOrFutureThan(val, min, format);
    }
}
