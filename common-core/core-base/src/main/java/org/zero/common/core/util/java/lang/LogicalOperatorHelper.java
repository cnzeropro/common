package org.zero.common.core.util.java.lang;

import org.zero.common.core.extension.java.util.function.ToBoolFunction;

import java.util.function.BooleanSupplier;
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

    public static LogicalOperatorHelper init(BooleanSupplier resultSupplier) {
        return init(resultSupplier.getAsBoolean());
    }

    public static <T> LogicalOperatorHelper init(T obj, Predicate<T> predicate) {
        return init(predicate.test(obj));
    }

    /**
     * 且
     */
    public LogicalOperatorHelper and(boolean... booleans) {
        for (boolean bool : booleans) {
            result = result && bool;
        }
        return this;
    }

    /**
     * 且
     */
    public LogicalOperatorHelper and(BooleanSupplier... booleanSuppliers) {
        boolean[] booleans = ArrayUtil.map(booleanSuppliers, (ToBoolFunction<BooleanSupplier>) BooleanSupplier::getAsBoolean);
        return this.and(booleans);
    }

    /**
     * 且
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper and(Predicate<T> predicate, T... objs) {
        boolean[] booleans = ArrayUtil.map(objs, (ToBoolFunction<T>) predicate::test);
        return this.and(booleans);
    }

    /**
     * 且
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper and(T obj, Predicate<T>... predicates) {
        boolean[] booleans = ArrayUtil.map(predicates, (ToBoolFunction<Predicate<T>>) predicate -> predicate.test(obj));
        return this.and(booleans);
    }

    /**
     * 非、且
     */
    public LogicalOperatorHelper negateAnd(boolean... booleans) {
        return this.negate().and(booleans);
    }

    /**
     * 非、且
     */
    public LogicalOperatorHelper negateAnd(BooleanSupplier... booleanSuppliers) {
        return this.negate().and(booleanSuppliers);
    }

    /**
     * 非、且
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper negateAnd(Predicate<T> predicate, T... objs) {
        return this.negate().and(predicate, objs);
    }

    /**
     * 非、且
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper negateAnd(T obj, Predicate<T>... predicates) {
        return this.negate().and(obj, predicates);
    }

    /**
     * 且、非
     */
    public LogicalOperatorHelper andNegate(boolean... booleans) {
        return this.and(booleans).negate();
    }

    /**
     * 且、非
     */
    public LogicalOperatorHelper andNegate(BooleanSupplier... booleanSuppliers) {
        return this.and(booleanSuppliers).negate();
    }

    /**
     * 且、非
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper andNegate(Predicate<T> predicate, T... objs) {
        return this.and(predicate, objs).negate();
    }

    /**
     * 且、非
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper andNegate(T obj, Predicate<T>... predicates) {
        return this.and(obj, predicates).negate();
    }

    /**
     * 或
     */
    public LogicalOperatorHelper or(boolean... booleans) {
        for (boolean bool : booleans) {
            result = result || bool;
        }
        return this;
    }

    /**
     * 或
     */
    public LogicalOperatorHelper or(BooleanSupplier... booleanSuppliers) {
        boolean[] booleans = ArrayUtil.map(booleanSuppliers, (ToBoolFunction<BooleanSupplier>) BooleanSupplier::getAsBoolean);
        return this.or(booleans);
    }

    /**
     * 或
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper or(T obj, Predicate<T>... predicates) {
        boolean[] booleans = ArrayUtil.map(predicates, (ToBoolFunction<Predicate<T>>) predicate -> predicate.test(obj));
        return this.or(booleans);
    }

    /**
     * 或
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper or(Predicate<T> predicate, T... objs) {
        boolean[] booleans = ArrayUtil.map(objs, (ToBoolFunction<T>) predicate::test);
        return this.or(booleans);
    }

    /**
     * 非、或
     */
    public LogicalOperatorHelper negateOr(boolean... booleans) {
        return this.negate().or(booleans);
    }

    /**
     * 非、或
     */
    public LogicalOperatorHelper negateOr(BooleanSupplier... booleanSuppliers) {
        return this.negate().or(booleanSuppliers);
    }

    /**
     * 非、或
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper negateOr(Predicate<T> predicate, T... objs) {
        return this.negate().or(predicate, objs);
    }

    /**
     * 非、或
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper negateOr(T obj, Predicate<T>... predicates) {
        return this.negate().or(obj, predicates);
    }

    /**
     * 或、非
     */
    public LogicalOperatorHelper orNegate(boolean... booleans) {
        return this.or(booleans).negate();
    }

    /**
     * 或、非
     */
    public LogicalOperatorHelper orNegate(BooleanSupplier... booleanSuppliers) {
        return this.or(booleanSuppliers).negate();
    }

    /**
     * 或、非
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper orNegate(Predicate<T> predicate, T... objs) {
        return this.or(predicate, objs).negate();
    }

    /**
     * 或、非
     */
    @SafeVarargs
    public final <T> LogicalOperatorHelper orNegate(T obj, Predicate<T>... predicates) {
        return this.or(obj, predicates).negate();
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
