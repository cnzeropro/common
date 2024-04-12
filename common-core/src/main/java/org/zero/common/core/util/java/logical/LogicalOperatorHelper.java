package org.zero.common.core.util.java.logical;

import java.util.function.Predicate;

/**
 * 逻辑操作符工具类
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2021/4/25
 */
public class LogicalOperatorHelper {
    private boolean result;

    private LogicalOperatorHelper(boolean result) {
        this.result = result;
    }

    public static LogicalOperatorHelper init(boolean result) {
        return new LogicalOperatorHelper(result);
    }

    public static <T> LogicalOperatorHelper init(Predicate<T> predicate, T obj) {
        return init(predicate.test(obj));
    }

    /**
     * 且
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper and(Predicate<T> predicate, T... objs) {
        for (T obj : objs) {
            result = result && predicate.test(obj);
        }
        return this;
    }

    /**
     * 且
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper and(T obj, Predicate<T>... predicates) {
        for (Predicate<T> predicate : predicates) {
            result = result && predicate.test(obj);
        }
        return this;
    }

    /**
     * 非、且
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper negateAnd(Predicate<T> predicate, T... objs) {
        return negate().and(predicate, objs);
    }

    /**
     * 非、且
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper negateAnd(T obj, Predicate<T>... predicates) {
        return negate().and(obj, predicates);
    }

    /**
     * 且、非
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper andNegate(Predicate<T> predicate, T... objs) {
        return and(predicate, objs).negate();
    }

    /**
     * 且、非
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper andNegate(T obj, Predicate<T>... predicates) {
        return and(obj, predicates).negate();
    }

    /**
     * 或
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper or(T obj, Predicate<T>... predicates) {
        for (Predicate<T> predicate : predicates) {
            result = result || predicate.test(obj);
        }
        return this;
    }

    /**
     * 或
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper or(Predicate<T> predicate, T... objs) {
        for (T obj : objs) {
            result = result || predicate.test(obj);
        }
        return this;
    }

    /**
     * 非、或
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper negateOr(Predicate<T> predicate, T... objs) {
        return negate().or(predicate, objs);
    }

    /**
     * 非、或
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper negateOr(T obj, Predicate<T>... predicates) {
        return  negate().or(obj, predicates);
    }

    /**
     * 或、非
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper orNegate(Predicate<T> predicate, T... objs) {
        return or(predicate, objs).negate();
    }

    /**
     * 或、非
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper orNegate(T obj, Predicate<T>... predicates) {
        return or(obj, predicates).negate();
    }

    /**
     * 非
     */
    public LogicalOperatorHelper negate() {
        result = !result;
        return this;
    }

    public boolean result() {
        return result;
    }
}
