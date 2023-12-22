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
     * 和
     */
    public <T> LogicalOperatorHelper and(Predicate<T> predicate, T... objs) {
        for (T obj : objs) {
            result = result && predicate.test(obj);
        }
        return this;
    }

    /**
     * 和
     */
    public <T> LogicalOperatorHelper and(T obj, Predicate<T>... predicates) {
        for (Predicate<T> predicate : predicates) {
            result = result && predicate.test(obj);
        }
        return this;
    }

    /**
     * 非、和
     */
    public <T> LogicalOperatorHelper negateAnd(Predicate<T> predicate, T... objs) {
        negate();
        return and(predicate, objs);
    }

    /**
     * 非、和
     */
    public <T> LogicalOperatorHelper negateAnd(T obj, Predicate<T>... predicates) {
        negate();
        return and(obj, predicates);
    }

    /**
     * 和、非
     */
    public <T> LogicalOperatorHelper andNegate(Predicate<T> predicate, T... objs) {
        and(predicate, objs);
        return negate();
    }

    /**
     * 和、非
     */
    public <T> LogicalOperatorHelper andNegate(T obj, Predicate<T>... predicates) {
        and(obj, predicates);
        return negate();
    }

    /**
     * 或
     */
    public <T> LogicalOperatorHelper or(T obj, Predicate<T>... predicates) {
        for (Predicate<T> predicate : predicates) {
            result = result || predicate.test(obj);
        }
        return this;
    }

    /**
     * 或
     */
    public <T> LogicalOperatorHelper or(Predicate<T> predicate, T... objs) {
        for (T obj : objs) {
            result = result || predicate.test(obj);
        }
        return this;
    }

    /**
     * 非、或
     */
    public <T> LogicalOperatorHelper negateOr(Predicate<T> predicate, T... objs) {
        negate();
        return or(predicate, objs);
    }

    /**
     * 非、或
     */
    public <T> LogicalOperatorHelper negateOr(T obj, Predicate<T>... predicates) {
        negate();
        return or(obj, predicates);
    }

    /**
     * 或、非
     */
    public <T> LogicalOperatorHelper orNegate(Predicate<T> predicate, T... objs) {
        or(predicate, objs);
        return negate();
    }

    /**
     * 或、非
     */
    public <T> LogicalOperatorHelper orNegate(T obj, Predicate<T>... predicates) {
        or(obj, predicates);
        return negate();
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
