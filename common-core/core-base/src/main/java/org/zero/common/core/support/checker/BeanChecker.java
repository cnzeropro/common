package org.zero.common.core.support.checker;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Zero
 * @since 2023/4/17
 */
public class BeanChecker<T> extends BaseChecker {
    /**
     * 待检查对象
     */
    private final T value;
    /**
     * 异常信息列表
     */
    private final List<String> errorMessages;

    /* **************************************************************** 构造 **************************************************************** */

    public BeanChecker(final T value) {
        this(value, "the object to be checked is null");
    }

    public BeanChecker(final T value, String message) {
        this.value = value;
        errorMessages = new ArrayList<>();
        if (isNull(value)) {
            errorMessages.add(message);
        }
    }

    /* **************************************************************** 基础检查 **************************************************************** */

    public BeanChecker<T> assertTrue(Function<T, Boolean> mapper, String message) {
        return passOrAddMsg(mapper, this::assertTrue, message);
    }

    public BeanChecker<T> assertFalse(Function<T, Boolean> mapper, String message) {
        return passOrAddMsg(mapper, this::assertFalse, message);
    }

    public <R> BeanChecker<T> isNull(Function<T, R> mapper, String message) {
        return passOrAddMsg(mapper, this::isNull, message);
    }

    public <R> BeanChecker<T> notNull(Function<T, R> mapper, String message) {
        return passOrAddMsg(mapper, this::notNull, message);
    }

    public <R> BeanChecker<T> equals(Function<T, R> mapper, Object other, String message) {
        return passOrAddMsg(mapper, v -> equals(v, other), message);
    }

    public <R> BeanChecker<T> notEquals(Function<T, R> mapper, Object other, String message) {
        return passOrAddMsg(mapper, v -> notEquals(v, other), message);
    }

    /* **************************************************************** 字符序列、集合检查 **************************************************************** */

    public <R> BeanChecker<T> isEmpty(Function<T, R> mapper, String message) {
        return passOrAddMsg(mapper, this::isEmpty, message);
    }

    public <R> BeanChecker<T> notEmpty(Function<T, R> mapper, String message) {
        return passOrAddMsg(mapper, this::notEmpty, message);
    }

    public <R extends CharSequence> BeanChecker<T> isBlack(Function<T, R> mapper, String message) {
        return passOrAddMsg(mapper, this::isBlack, message);
    }

    public <R extends CharSequence> BeanChecker<T> notBlack(Function<T, R> mapper, String message) {
        return passOrAddMsg(mapper, this::notBlack, message);
    }

    /* **************************************************************** 数字检查 **************************************************************** */

    public <R extends Number> BeanChecker<T> less(Function<T, R> mapper, long max, String message) {
        return passOrAddMsg(mapper, v -> less(v, max), message);
    }

    public <R extends Number> BeanChecker<T> less(Function<T, R> mapper, double max, String message) {
        return passOrAddMsg(mapper, v -> less(v, max), message);
    }

    public <R extends BigDecimal> BeanChecker<T> less(Function<T, R> mapper, BigDecimal max, String message) {
        return passOrAddMsg(mapper, v -> less(v, max), message);
    }

    public <R extends BigInteger> BeanChecker<T> less(Function<T, R> mapper, BigInteger max, String message) {
        return passOrAddMsg(mapper, v -> less(v, max), message);
    }

    public <R extends Number> BeanChecker<T> max(Function<T, R> mapper, long max, String message) {
        return passOrAddMsg(mapper, v -> max(v, max), message);
    }

    public <R extends Number> BeanChecker<T> max(Function<T, R> mapper, double max, String message) {
        return passOrAddMsg(mapper, v -> max(v, max), message);
    }

    public <R extends BigDecimal> BeanChecker<T> max(Function<T, R> mapper, BigDecimal max, String message) {
        return passOrAddMsg(mapper, v -> max(v, max), message);
    }

    public <R extends BigInteger> BeanChecker<T> max(Function<T, R> mapper, BigInteger max, String message) {
        return passOrAddMsg(mapper, v -> max(v, max), message);
    }

    public <R extends Number> BeanChecker<T> greater(Function<T, R> mapper, long min, String message) {
        return passOrAddMsg(mapper, v -> greater(v, min), message);
    }

    public <R extends Number> BeanChecker<T> greater(Function<T, R> mapper, double min, String message) {
        return passOrAddMsg(mapper, v -> greater(v, min), message);
    }

    public <R extends BigDecimal> BeanChecker<T> greater(Function<T, R> mapper, BigDecimal min, String message) {
        return passOrAddMsg(mapper, v -> greater(v, min), message);
    }

    public <R extends BigInteger> BeanChecker<T> greater(Function<T, R> mapper, BigInteger min, String message) {
        return passOrAddMsg(mapper, v -> greater(v, min), message);
    }

    public <R extends Number> BeanChecker<T> min(Function<T, R> mapper, long min, String message) {
        return passOrAddMsg(mapper, v -> min(v, min), message);
    }

    public <R extends Number> BeanChecker<T> min(Function<T, R> mapper, double min, String message) {
        return passOrAddMsg(mapper, v -> min(v, min), message);
    }

    public <R extends BigDecimal> BeanChecker<T> min(Function<T, R> mapper, BigDecimal min, String message) {
        return passOrAddMsg(mapper, v -> min(v, min), message);
    }

    public <R extends BigInteger> BeanChecker<T> min(Function<T, R> mapper, BigInteger min, String message) {
        return passOrAddMsg(mapper, v -> min(v, min), message);
    }


    public <R extends Number> BeanChecker<T> negative(Function<T, R> mapper, String message) {
        return passOrAddMsg(mapper, this::negative, message);
    }

    public <R extends Number> BeanChecker<T> negativeOrZero(Function<T, R> mapper, String message) {
        return passOrAddMsg(mapper, this::negativeOrZero, message);
    }

    public <R extends Number> BeanChecker<T> positive(Function<T, R> mapper, String message) {
        return passOrAddMsg(mapper, this::positive, message);
    }

    public <R extends Number> BeanChecker<T> positiveOrZero(Function<T, R> mapper, String message) {
        return passOrAddMsg(mapper, this::positiveOrZero, message);
    }

    /* **************************************************************** 日期、时间检查 **************************************************************** */

    public <R> BeanChecker<T> present(Function<T, R> mapper, String message) {
        return passOrAddMsg(mapper, this::present, message);
    }

    public <R> BeanChecker<T> present(Function<T, R> mapper, String format, String message) {
        return passOrAddMsg(mapper, v -> present(v, format), message);
    }

    public <R extends Date> BeanChecker<T> present(Function<T, R> mapper, Date date, String message) {
        return passOrAddMsg(mapper, v -> presentOn(v, date), message);
    }

    public <R extends Date> BeanChecker<T> present(Function<T, R> mapper, Date date, String format, String message) {
        return passOrAddMsg(mapper, v -> presentOn(v, date, format), message);
    }

    public <R extends Calendar> BeanChecker<T> present(Function<T, R> mapper, Calendar calendar, String message) {
        return passOrAddMsg(mapper, v -> presentOn(v, calendar), message);
    }

    public <R extends Calendar> BeanChecker<T> present(Function<T, R> mapper, Calendar calendar, String format, String message) {
        return passOrAddMsg(mapper, v -> presentOn(v, calendar, format), message);
    }

    public <R extends TemporalAccessor> BeanChecker<T> present(Function<T, R> mapper, TemporalAccessor temporalAccessor, String message) {
        return passOrAddMsg(mapper, v -> presentOn(v, temporalAccessor), message);
    }

    public <R extends TemporalAccessor> BeanChecker<T> present(Function<T, R> mapper, TemporalAccessor temporalAccessor, String format, String message) {
        return passOrAddMsg(mapper, v -> presentOn(v, temporalAccessor, format), message);
    }

    public <R> BeanChecker<T> notPresent(Function<T, R> mapper, String message) {
        return passOrAddMsg(mapper, this::notPresent, message);
    }

    public <R> BeanChecker<T> notPresent(Function<T, R> mapper, String format, String message) {
        return passOrAddMsg(mapper, v -> notPresent(v, format), message);
    }

    public <R extends Date> BeanChecker<T> notPresent(Function<T, R> mapper, Date date, String message) {
//        return passOrAddMsg(mapper, ((Predicate<R>) v -> pastThan(v, date)).or(v -> futureThan(v, date)), message);
//        return passOrAddMsg(mapper, v -> pastThan(v, date) || futureThan(v, date), message);
        return passOrAddMsg(mapper, v -> !presentOn(v, date), message);
    }

    public <R extends Date> BeanChecker<T> notPresent(Function<T, R> mapper, Date date, String format, String message) {
        return passOrAddMsg(mapper, v -> !presentOn(v, date, format), message);
    }

    public <R extends Calendar> BeanChecker<T> notPresent(Function<T, R> mapper, Calendar calendar, String message) {
        return passOrAddMsg(mapper, v -> !presentOn(v, calendar), message);
    }

    public <R extends Calendar> BeanChecker<T> notPresent(Function<T, R> mapper, Calendar calendar, String format, String message) {
        return passOrAddMsg(mapper, v -> !presentOn(v, calendar, format), message);
    }

    public <R extends TemporalAccessor> BeanChecker<T> notPresent(Function<T, R> mapper, TemporalAccessor temporalAccessor, String message) {
        return passOrAddMsg(mapper, v -> !presentOn(v, temporalAccessor), message);
    }

    public <R extends TemporalAccessor> BeanChecker<T> notPresent(Function<T, R> mapper, TemporalAccessor temporalAccessor, String format, String message) {
        return passOrAddMsg(mapper, v -> !presentOn(v, temporalAccessor, format), message);
    }

    public <R> BeanChecker<T> future(Function<T, R> mapper, String message) {
        return passOrAddMsg(mapper, this::future, message);
    }

    public <R> BeanChecker<T> future(Function<T, R> mapper, String format, String message) {
        return passOrAddMsg(mapper, v -> future(v, format), message);
    }

    public <R extends Date> BeanChecker<T> future(Function<T, R> mapper, Date date, String message) {
        return passOrAddMsg(mapper, v -> futureThan(v, date), message);
    }

    public <R extends Date> BeanChecker<T> future(Function<T, R> mapper, Date date, String format, String message) {
        return passOrAddMsg(mapper, v -> futureThan(v, date, format), message);
    }

    public <R extends Calendar> BeanChecker<T> future(Function<T, R> mapper, Calendar calendar, String message) {
        return passOrAddMsg(mapper, v -> futureThan(v, calendar), message);
    }

    public <R extends Calendar> BeanChecker<T> future(Function<T, R> mapper, Calendar calendar, String format, String message) {
        return passOrAddMsg(mapper, v -> futureThan(v, calendar, format), message);
    }

    public <R extends TemporalAccessor> BeanChecker<T> future(Function<T, R> mapper, TemporalAccessor temporalAccessor, String message) {
        return passOrAddMsg(mapper, v -> futureThan(v, temporalAccessor), message);
    }

    public <R extends TemporalAccessor> BeanChecker<T> future(Function<T, R> mapper, TemporalAccessor temporalAccessor, String format, String message) {
        return passOrAddMsg(mapper, v -> futureThan(v, temporalAccessor, format), message);
    }

    public <R> BeanChecker<T> past(Function<T, R> mapper, String message) {
        return passOrAddMsg(mapper, this::past, message);
    }

    public <R> BeanChecker<T> past(Function<T, R> mapper, String format, String message) {
        return passOrAddMsg(mapper, v -> past(v, format), message);
    }

    public <R extends Date> BeanChecker<T> past(Function<T, R> mapper, Date date, String message) {
        return passOrAddMsg(mapper, v -> pastThan(v, date), message);
    }

    public <R extends Date> BeanChecker<T> past(Function<T, R> mapper, Date date, String format, String message) {
        return passOrAddMsg(mapper, v -> pastThan(v, date, format), message);
    }

    public <R extends Calendar> BeanChecker<T> past(Function<T, R> mapper, Calendar calendar, String message) {
        return passOrAddMsg(mapper, v -> pastThan(v, calendar), message);
    }

    public <R extends Calendar> BeanChecker<T> past(Function<T, R> mapper, Calendar calendar, String format, String message) {
        return passOrAddMsg(mapper, v -> pastThan(v, calendar, format), message);
    }

    public <R extends TemporalAccessor> BeanChecker<T> past(Function<T, R> mapper, TemporalAccessor temporalAccessor, String message) {
        return passOrAddMsg(mapper, v -> pastThan(v, temporalAccessor), message);
    }

    public <R extends TemporalAccessor> BeanChecker<T> past(Function<T, R> mapper, TemporalAccessor temporalAccessor, String format, String message) {
        return passOrAddMsg(mapper, v -> pastThan(v, temporalAccessor, format), message);
    }

    /* **************************************************************** 其他 **************************************************************** */

    /**
     * 获取异常信息列表
     */
    public List<String> getErrorMessages() {
        return errorMessages;
    }

    /**
     * 获取异常信息，使用指定分隔符
     */
    public String getErrorMessage() {
        return getErrorMessage(",");
    }

    /**
     * 获取异常信息，使用指定分隔符
     */
    public String getErrorMessage(CharSequence delimiter) {
        return getErrorMessage(delimiter, "[", "]");
    }

    /**
     * 获取异常信息，使用指定分隔符，前缀和后缀
     */
    public String getErrorMessage(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        return errorMessages.stream().collect(Collectors.joining(delimiter, prefix, suffix));
    }

    /**
     * 是否通过检查
     */
    public boolean isPass() {
        return errorMessages.isEmpty();
    }

    /**
     * 检查，失败时默认使用全局异常信息
     *
     * @throws IllegalArgumentException 如果检查未通过则抛出非法参数异常
     */
    public void check() {
        check(getErrorMessage());
    }

    /**
     * 检查，使用给定异常信息
     *
     * @throws IllegalArgumentException 如果检查未通过则抛出非法参数异常
     */
    public void check(String message) {
        check(IllegalArgumentException::new, message);
    }

    /**
     * 检查，使用给定异常
     *
     * @throws X 如果检查未通过则抛出给定异常
     */
    public <X extends Throwable> void check(Function<String, ? extends X> exceptionMapper) throws X {
        check(exceptionMapper, getErrorMessage());
    }

    /**
     * 检查，使用给定异常并使用给定的异常信息
     *
     * @throws X 如果检查未通过则抛出给定异常
     */
    public <X extends Throwable> void check(Function<String, ? extends X> exceptionMapper, String message) throws X {
        if (!isPass()) {
            throw exceptionMapper.apply(message);
        }
    }

    /**
     * 使用给定映射出需要值
     */
    private <R> R map(Function<T, R> mapper) {
        if (Objects.nonNull(value)) {
            return mapper.apply(value);
        }
        return null;
    }

    /**
     * 通过检查或者增加异常信息（不中断检查）
     */
    private <R> BeanChecker<T> passOrAddMsg(Function<T, R> mapper, Predicate<R> predicate, String message) {
        R val = map(mapper);

//        if (predicate.negate().test(val)) {
//            errorMessages.add(message);
//        }

        if (!predicate.test(val)) {
            errorMessages.add(message);
        }
        return this;
    }

    /**
     * 通过检查或者增加异常信息（中断检查）
     */
    private <R> BeanChecker<T> passOrCheck(Function<T, R> mapper, Predicate<R> predicate, String message) {
        BeanChecker<T> checker = passOrAddMsg(mapper, predicate, message);
        checker.check();
        return checker;
    }

    /**
     * 通过检查或者增加异常信息（中断检查）
     */
    private <R, X extends Throwable> BeanChecker<T> passOrCheck(Function<T, R> mapper, Predicate<R> predicate, Function<String, ? extends X> exceptionMapper, String message) throws X {
        BeanChecker<T> checker = passOrAddMsg(mapper, predicate, message);
        checker.check(exceptionMapper);
        return checker;
    }
}