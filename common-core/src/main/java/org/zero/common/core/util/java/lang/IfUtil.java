package org.zero.common.core.util.java.lang;

import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/11/30
 */
@UtilityClass
public class IfUtil {
    /* **************************************************************** map **************************************************************** */

    /**
     * 如果表达式（conditionSuppliers）都为真，则用 val 进行映射操作
     *
     * @param val                待映射参数
     * @param function           映射函数
     * @param conditionSuppliers 表达式
     * @param <T>                待映射参数的类型
     * @param <R>                映射后的值类型
     * @return 映射后的值
     */
    public <T, R> R map(T val, Function<T, R> function, BooleanSupplier... conditionSuppliers) {
        return mapOrGet(val, function, (R) null, conditionSuppliers);
    }

    /**
     * 如果条件（conditions）都为真，则用 val 进行映射操作
     *
     * @param val        待映射参数
     * @param function   映射函数
     * @param conditions 条件
     * @param <T>        待映射参数的类型
     * @param <R>        映射后的值类型
     * @return 映射后的值
     */
    public <T, R> R map(T val, Function<T, R> function, boolean... conditions) {
        return mapOrGet(val, function, (R) null, conditions);
    }

    /**
     * 如果表达式（conditionSuppliers）都为真，则用 val 进行映射操作，否则用 otherVal 进行映射操作
     *
     * @param val                待映射参数
     * @param otherVal           条件不满足时使用的映射参数
     * @param function           映射函数
     * @param conditionSuppliers 表达式
     * @param <T>                待映射参数的类型
     * @param <R>                映射后的值类型
     * @return 映射后的值
     */
    public <T, R> R map(T val, T otherVal, Function<T, R> function, BooleanSupplier... conditionSuppliers) {
        return map(val, otherVal, function, allTrue(conditionSuppliers));
    }

    /**
     * 如果条件（conditions）都为真，则用 val 进行映射操作，否则用 otherVal 进行映射操作
     *
     * @param val        待映射参数
     * @param otherVal   条件不满足时使用的映射参数
     * @param function   映射函数
     * @param conditions 条件
     * @param <T>        待映射参数的类型
     * @param <R>        映射后的值类型
     * @return 映射后的值
     */
    public <T, R> R map(T val, T otherVal, Function<T, R> function, boolean... conditions) {
        if (Objects.isNull(function)) {
            return null;
        }
        if (allTrue(conditions)) {
            return function.apply(val);
        }
        return function.apply(otherVal);
    }

    /**
     * 如果表达式（conditionSuppliers）都为真，则用 val 进行映射操作，否则直接返回从供给函数（defaultValSupplier）获取到的值
     *
     * @param val                待映射参数
     * @param function           映射函数
     * @param defaultValSupplier 表达式不满足时的默认值
     * @param conditionSuppliers 表达式
     * @param <T>                待映射参数的类型
     * @param <R>                映射后的值类型
     * @return 映射后的值
     */
    public <T, R> R mapOrGet(T val, Function<T, R> function, Supplier<R> defaultValSupplier, BooleanSupplier... conditionSuppliers) {
        return mapOrGet(val, function, defaultValSupplier, allTrue(conditionSuppliers));
    }

    /**
     * 如果条件（conditions）都为真，则用 val 进行映射操作，否则直接返回从供给函数（defaultValSupplier）获取到的值
     *
     * @param val                待映射参数
     * @param function           映射函数
     * @param defaultValSupplier 条件不满足时的默认值
     * @param conditions         条件
     * @param <T>                待映射参数的类型
     * @param <R>                映射后的值类型
     * @return 映射后的值
     */
    public <T, R> R mapOrGet(T val, Function<T, R> function, Supplier<R> defaultValSupplier, boolean... conditions) {
        return mapOrGet(val, function, Objects.nonNull(defaultValSupplier) ? defaultValSupplier.get() : null, conditions);
    }

    /**
     * 如果表达式（conditionSuppliers）都为真，则用 val 进行映射操作，否则直接返回 defaultVal
     *
     * @param val                待映射参数
     * @param function           映射函数
     * @param defaultVal         表达式不满足时的默认值
     * @param conditionSuppliers 表达式
     * @param <T>                待映射参数的类型
     * @param <R>                映射后的值类型
     * @return 映射后的值
     */
    public <T, R> R mapOrGet(T val, Function<T, R> function, R defaultVal, BooleanSupplier... conditionSuppliers) {
        return mapOrGet(val, function, defaultVal, allTrue(conditionSuppliers));
    }

    /**
     * 如果条件（conditions）都为真，则用 val 进行映射操作，否则直接返回 defaultVal
     *
     * @param val        待映射参数
     * @param function   映射函数
     * @param defaultVal 条件不满足时的默认值
     * @param conditions 条件
     * @param <T>        待映射参数的类型
     * @param <R>        映射后的值类型
     * @return 映射后的值
     */
    public <T, R> R mapOrGet(T val, Function<T, R> function, R defaultVal, boolean... conditions) {
        if (Objects.isNull(function)) {
            return null;
        }
        if (allTrue(conditions)) {
            return function.apply(val);
        }
        return defaultVal;
    }

    /**
     * 如果表达式（conditionSuppliers）都为真，则用 val1、val2 进行映射操作
     *
     * @param val1               待映射参数1
     * @param val2               待映射参数2
     * @param function           映射函数
     * @param conditionSuppliers 表达式
     * @param <T>                待映射参数1的类型
     * @param <U>                待映射参数2的类型
     * @param <R>                映射后的值类型
     * @return 映射后的值
     */
    public <T, U, R> R map(T val1, U val2, BiFunction<T, U, R> function, BooleanSupplier... conditionSuppliers) {
        return mapOrGet(val1, val2, function, (R) null, conditionSuppliers);
    }

    /**
     * 如果条件（conditions）都为真，则用 val1、val2 进行映射操作
     *
     * @param val1       待映射参数1
     * @param val2       待映射参数2
     * @param function   映射函数
     * @param conditions 条件
     * @param <T>        待映射参数1的类型
     * @param <U>        待映射参数2的类型
     * @param <R>        映射后的值类型
     * @return 映射后的值
     */
    public <T, U, R> R map(T val1, U val2, BiFunction<T, U, R> function, boolean... conditions) {
        return mapOrGet(val1, val2, function, (R) null, conditions);
    }

    /**
     * 如果表达式（conditionSuppliers）都为真，则用 val1、val2 进行映射操作，否则用 otherVal1、otherVal2 进行映射操作
     *
     * @param val1               待映射参数1
     * @param val2               待映射参数2
     * @param otherVal1          条件不满足时使用的映射参数1
     * @param otherVal2          条件不满足时使用的映射参数2
     * @param function           映射函数
     * @param conditionSuppliers 表达式
     * @param <T>                待映射参数1的类型
     * @param <U>                待映射参数2的类型
     * @param <R>                映射后的值类型
     * @return 映射后的值
     */
    public <T, U, R> R map(T val1, U val2, T otherVal1, U otherVal2, BiFunction<T, U, R> function, BooleanSupplier... conditionSuppliers) {
        return map(val1, val2, otherVal1, otherVal2, function, allTrue(conditionSuppliers));
    }

    /**
     * 如果条件（conditions）都为真，则用 val1、val2 进行映射操作，否则用 otherVal1、otherVal2 进行映射操作
     *
     * @param val1       待映射参数1
     * @param val2       待映射参数2
     * @param otherVal1  条件不满足时使用的映射参数1
     * @param otherVal2  条件不满足时使用的映射参数2
     * @param function   映射函数
     * @param conditions 条件
     * @param <T>        待映射参数1的类型
     * @param <U>        待映射参数2的类型
     * @param <R>        映射后的值类型
     * @return 映射后的值
     */
    public <T, U, R> R map(T val1, U val2, T otherVal1, U otherVal2, BiFunction<T, U, R> function, boolean... conditions) {
        if (Objects.isNull(function)) {
            return null;
        }
        if (allTrue(conditions)) {
            return function.apply(val1, val2);
        }
        return function.apply(otherVal1, otherVal2);
    }

    /**
     * 如果条件（conditions）都为真，则用 val1 和 val2 进行映射操作，否则直接返回从供给函数（defaultValSupplier）获取到的值
     *
     * @param val1               待映射参数1
     * @param val2               待映射参数2
     * @param function           映射函数
     * @param defaultValSupplier 条件不满足时的默认值供给函数
     * @param conditions         条件
     * @param <T>                待映射参数1的类型
     * @param <U>                待映射参数2的类型
     * @param <R>                映射后的值类型
     * @return 映射后的值
     */
    public <T, U, R> R mapOrGet(T val1, U val2, BiFunction<T, U, R> function, Supplier<R> defaultValSupplier, BooleanSupplier... conditions) {
        return mapOrGet(val1, val2, function, defaultValSupplier, allTrue(conditions));
    }

    /**
     * 如果条件（conditions）都为真，则用 val1 和 val2 进行映射操作，否则直接返回从供给函数（defaultValSupplier）获取到的值
     *
     * @param val1               待映射参数1
     * @param val2               待映射参数2
     * @param function           映射函数
     * @param defaultValSupplier 条件不满足时的默认值供给函数
     * @param conditions         条件
     * @param <T>                待映射参数1的类型
     * @param <U>                待映射参数2的类型
     * @param <R>                映射后的值类型
     * @return 映射后的值
     */
    public <T, U, R> R mapOrGet(T val1, U val2, BiFunction<T, U, R> function, Supplier<R> defaultValSupplier, boolean... conditions) {
        return mapOrGet(val1, val2, function, Objects.nonNull(defaultValSupplier) ? defaultValSupplier.get() : null, conditions);
    }

    /**
     * 如果表达式（conditionSuppliers）都为真，则用 val1 和 val2 进行映射操作，否则直接返回 defaultVal
     *
     * @param val1               待映射参数1
     * @param val2               待映射参数2
     * @param function           映射函数
     * @param defaultVal         表达式不满足时的默认值
     * @param conditionSuppliers 表达式
     * @param <T>                待映射参数1的类型
     * @param <U>                待映射参数2的类型
     * @param <R>                映射后的值类型
     * @return 映射后的值
     */
    public <T, U, R> R mapOrGet(T val1, U val2, BiFunction<T, U, R> function, R defaultVal, BooleanSupplier... conditionSuppliers) {
        return mapOrGet(val1, val2, function, defaultVal, allTrue(conditionSuppliers));
    }

    /**
     * 如果条件（conditions）都为真，则用 val1 和 val2 进行映射操作，否则直接返回 defaultVal
     *
     * @param val1       待映射参数1
     * @param val2       待映射参数2
     * @param function   映射函数
     * @param defaultVal 条件不满足时的默认值
     * @param conditions 条件
     * @param <T>        待映射参数1的类型
     * @param <U>        待映射参数2的类型
     * @param <R>        映射后的值类型
     * @return 映射后的值
     */
    public <T, U, R> R mapOrGet(T val1, U val2, BiFunction<T, U, R> function, R defaultVal, boolean... conditions) {
        if (Objects.isNull(function)) {
            return null;
        }
        if (allTrue(conditions)) {
            return function.apply(val1, val2);
        }
        return defaultVal;
    }

    /* **************************************************************** provide **************************************************************** */

    /**
     * 如果表达式（conditionSuppliers）都为真，则从供给函数（supplier）中获取值
     *
     * @param supplier           供给函数
     * @param conditionSuppliers 表达式
     * @param <T>                供给的值类型
     * @return 供给的值
     */
    public <T> T provide(Supplier<T> supplier, BooleanSupplier... conditionSuppliers) {
        return provide(supplier, (T) null, conditionSuppliers);
    }

    /**
     * 如果条件（conditions）都为真，则从供给函数（supplier）中获取值
     *
     * @param supplier   供给函数
     * @param conditions 条件
     * @param <T>        供给的值类型
     * @return 供给的值
     */
    public <T> T provide(Supplier<T> supplier, boolean... conditions) {
        return provide(supplier, (T) null, conditions);
    }

    /**
     * 如果表达式（conditionSuppliers）都为真，则从供给函数（supplier）中获取值，否则返回从供给函数（defaultValSupplier）获取到的值
     *
     * @param supplier           供给函数
     * @param defaultValSupplier 表达式不满足时的默认值供给函数
     * @param conditionSuppliers 表达式
     * @param <T>                供给的值类型
     * @return 供给的值
     */
    public <T> T provide(Supplier<T> supplier, Supplier<T> defaultValSupplier, BooleanSupplier... conditionSuppliers) {
        return provide(supplier, defaultValSupplier, allTrue(conditionSuppliers));
    }

    /**
     * 如果条件（conditions）都为真，则从供给函数（supplier）中获取值，否则返回从供给函数（defaultValSupplier）获取到的值
     *
     * @param supplier           供给函数
     * @param defaultValSupplier 条件不满足时的默认值供给函数
     * @param conditions         条件
     * @param <T>                供给的值类型
     * @return 供给的值
     */
    public <T> T provide(Supplier<T> supplier, Supplier<T> defaultValSupplier, boolean... conditions) {
        return provide(supplier, Objects.nonNull(defaultValSupplier) ? defaultValSupplier.get() : null, conditions);
    }

    /**
     * 如果表达式（conditionSuppliers）都为真，则从供给函数（supplier）中获取值，否则返回 defaultVal
     *
     * @param supplier           供给函数
     * @param defaultVal         表达式不满足时的默认值
     * @param conditionSuppliers 表达式
     * @param <T>                供给的值类型
     * @return 供给的值
     */
    public <T> T provide(Supplier<T> supplier, T defaultVal, BooleanSupplier... conditionSuppliers) {
        return provide(supplier, defaultVal, allTrue(conditionSuppliers));
    }

    /**
     * 如果条件（conditions）都为真，则从供给函数（supplier）中获取值，否则返回 defaultVal
     *
     * @param supplier   供给函数
     * @param defaultVal 条件不满足时的默认值
     * @param conditions 条件
     * @param <T>        供给的值类型
     * @return 供给的值
     */
    public <T> T provide(Supplier<T> supplier, T defaultVal, boolean... conditions) {
        if (Objects.isNull(supplier)) {
            return null;
        }
        if (allTrue(conditions)) {
            return supplier.get();
        }
        return defaultVal;
    }

    /* **************************************************************** consume **************************************************************** */

    /**
     * 如果表达式（conditionSuppliers）都为真，则消费 val
     *
     * @param val                待消费的值
     * @param consumer           消费函数
     * @param conditionSuppliers 表达式
     * @param <T>                待消费的值类型
     */
    public <T> void consume(T val, Consumer<T> consumer, BooleanSupplier... conditionSuppliers) {
        consume(val, consumer, allTrue(conditionSuppliers));
    }

    /**
     * 如果条件（conditions）都为真，则消费 val
     *
     * @param val        待消费的值
     * @param consumer   消费函数
     * @param conditions 条件
     * @param <T>        待消费的值类型
     */
    public <T> void consume(T val, Consumer<T> consumer, boolean... conditions) {
        if (Objects.nonNull(consumer) && allTrue(conditions)) {
            consumer.accept(val);
        }
    }

    /**
     * 如果表达式（conditionSuppliers）都为真，则消费 val，否则消费 otherVal
     *
     * @param val                待消费的值
     * @param otherVal           表达式不满足时的待消费值
     * @param consumer           消费函数
     * @param conditionSuppliers 表达式
     * @param <T>                待消费的值类型
     */
    public <T> void consume(T val, T otherVal, Consumer<T> consumer, BooleanSupplier... conditionSuppliers) {
        consume(val, otherVal, consumer, allTrue(conditionSuppliers));
    }

    /**
     * 如果条件（conditions）都为真，则消费 val，否则消费 otherVal
     *
     * @param val        待消费的值
     * @param otherVal   表达式不满足时的待消费值
     * @param consumer   消费函数
     * @param conditions 条件
     * @param <T>        待消费的值类型
     */
    public <T> void consume(T val, T otherVal, Consumer<T> consumer, boolean... conditions) {
        if (Objects.nonNull(consumer)) {
            if (allTrue(conditions)) {
                consumer.accept(val);
            } else {
                consumer.accept(otherVal);
            }
        }
    }

    /**
     * 如果表达式（conditionSuppliers）都为真，则消费 val1、val2
     *
     * @param val1               待消费参数1
     * @param val2               待消费参数2
     * @param consumer           消费函数
     * @param conditionSuppliers 表达式
     * @param <T>                待消费参数1的类型
     * @param <U>                待消费参数2的类型
     */
    public <T, U> void consume(T val1, U val2, BiConsumer<T, U> consumer, BooleanSupplier... conditionSuppliers) {
        consume(val1, val2, consumer, allTrue(conditionSuppliers));
    }

    /**
     * 如果条件（conditions）都为真，则消费 val1、val2
     *
     * @param val1       待消费参数1
     * @param val2       待消费参数2
     * @param consumer   消费函数
     * @param conditions 条件
     * @param <T>        待消费参数1的类型
     * @param <U>        待消费参数2的类型
     */
    public <T, U> void consume(T val1, U val2, BiConsumer<T, U> consumer, boolean... conditions) {
        if (Objects.nonNull(consumer) && allTrue(conditions)) {
            consumer.accept(val1, val2);
        }
    }

    /**
     * 如果表达式（conditionSuppliers）都为真，则消费 val1、val2，否则消费 otherVal1、otherVal2
     *
     * @param val1               待消费参数1
     * @param val2               待消费参数2
     * @param otherVal1          表达式不满足时的待消费参数1
     * @param otherVal2          表达式不满足时的待消费参数2
     * @param consumer           消费函数
     * @param conditionSuppliers 表达式
     * @param <T>                待消费参数1的类型
     * @param <U>                待消费参数2的类型
     */
    public <T, U> void consume(T val1, U val2, T otherVal1, U otherVal2, BiConsumer<T, U> consumer, BooleanSupplier... conditionSuppliers) {
        consume(val1, val2, otherVal1, otherVal2, consumer, allTrue(conditionSuppliers));
    }

    /**
     * 如果条件（conditions）都为真，则消费 val1、val2，否则消费 otherVal1、otherVal2
     *
     * @param val1       待消费参数1
     * @param val2       待消费参数2
     * @param otherVal1  条件不满足时的待消费参数1
     * @param otherVal2  条件不满足时的待消费参数2
     * @param consumer   消费函数
     * @param conditions 条件
     * @param <T>        待消费参数1的类型
     * @param <U>        待消费参数2的类型
     */
    public <T, U> void consume(T val1, U val2, T otherVal1, U otherVal2, BiConsumer<T, U> consumer, boolean... conditions) {
        if (Objects.nonNull(consumer)) {
            if (allTrue(conditions)) {
                consumer.accept(val1, val2);
            } else {
                consumer.accept(otherVal1, otherVal2);
            }
        }
    }

    /* **************************************************************** consume **************************************************************** */

    /**
     * 如果表达式（conditionSuppliers）都为真，则测试 val
     *
     * @param val                待测试的值
     * @param predicate          测试函数
     * @param conditionSuppliers 表达式
     * @param <T>                待测试的值类型
     * @return 测试结果
     */
    public <T> boolean test(T val, Predicate<T> predicate, BooleanSupplier... conditionSuppliers) {
        return test(val, predicate, allTrue(conditionSuppliers));
    }

    /**
     * 如果条件（conditions）都为真，则测试 val
     *
     * @param val        待测试的值
     * @param predicate  测试函数
     * @param conditions 条件
     * @param <T>        待测试的值类型
     * @return 测试结果
     */
    public <T> boolean test(T val, Predicate<T> predicate, boolean... conditions) {
        if (Objects.isNull(predicate)) {
            return false;
        }
        if (allTrue(conditions)) {
            return predicate.test(val);
        }
        return false;
    }

    /**
     * 如果表达式（conditionSuppliers）都为真，则测试 val，否则测试 otherVal
     *
     * @param val                待测试的值
     * @param otherVal           表达式不满足时的待测试的值
     * @param predicate          测试函数
     * @param conditionSuppliers 表达式
     * @param <T>                待测试的值类型
     * @return 测试结果
     */
    public <T> boolean test(T val, T otherVal, Predicate<T> predicate, BooleanSupplier... conditionSuppliers) {
        return test(val, otherVal, predicate, allTrue(conditionSuppliers));
    }

    /**
     * 如果条件（conditions）都为真，则测试 val，否则测试 otherVal
     *
     * @param val        待测试的值
     * @param otherVal   表达式不满足时的待测试的值
     * @param predicate  测试函数
     * @param conditions 条件
     * @param <T>        待测试的值类型
     * @return 测试结果
     */
    public <T> boolean test(T val, T otherVal, Predicate<T> predicate, boolean... conditions) {
        if (Objects.isNull(predicate)) {
            return false;
        }
        if (allTrue(conditions)) {
            return predicate.test(val);
        }
        return predicate.test(otherVal);
    }

    /**
     * 如果表达式（conditionSuppliers）都为真，则测试 val1、val2
     *
     * @param val1               待测试的值1
     * @param val2               待测试的值2
     * @param predicate          测试函数
     * @param conditionSuppliers 表达式
     * @param <T>                待测试的值1的类型
     * @param <U>                待测试的值2的类型
     * @return 测试结果
     */
    public <T, U> boolean test(T val1, U val2, BiPredicate<T, U> predicate, BooleanSupplier... conditionSuppliers) {
        return test(val1, val2, predicate, allTrue(conditionSuppliers));
    }

    /**
     * 如果条件（conditions）都为真，则测试 val1、val2
     *
     * @param val1       待测试的值1
     * @param val2       待测试的值2
     * @param predicate  测试函数
     * @param conditions 条件
     * @param <T>        待测试的值1的类型
     * @param <U>        待测试的值2的类型
     * @return 测试结果
     */
    public <T, U> boolean test(T val1, U val2, BiPredicate<T, U> predicate, boolean... conditions) {
        if (Objects.isNull(predicate)) {
            return false;
        }
        if (allTrue(conditions)) {
            return predicate.test(val1, val2);
        }
        return false;
    }

    /**
     * 如果表达式（conditionSuppliers）都为真，则测试 val1、val2，否则测试 otherVal1、otherVal2
     *
     * @param val1               待测试的值1
     * @param val2               待测试的值2
     * @param otherVal1          表达式不满足时的待测试的值1
     * @param otherVal2          表达式不满足时的待测试的值2
     * @param predicate          测试函数
     * @param conditionSuppliers 表达式
     * @param <T>                待测试的值1的类型
     * @param <U>                待测试的值2的类型
     * @return 测试结果
     */
    public <T, U> boolean test(T val1, U val2, T otherVal1, U otherVal2, BiPredicate<T, U> predicate, BooleanSupplier... conditionSuppliers) {
        return test(val1, val2, otherVal1, otherVal2, predicate, allTrue(conditionSuppliers));
    }

    /**
     * 如果条件（conditions）都为真，则测试 val1、val2，否则测试 otherVal1、otherVal2
     *
     * @param val1       待测试的值1
     * @param val2       待测试的值2
     * @param otherVal1  表达式不满足时的待测试的值1
     * @param otherVal2  表达式不满足时的待测试的值2
     * @param predicate  测试函数
     * @param conditions 条件
     * @param <T>        待测试的值1的类型
     * @param <U>        待测试的值2的类型
     * @return 测试结果
     */
    public <T, U> boolean test(T val1, U val2, T otherVal1, U otherVal2, BiPredicate<T, U> predicate, boolean... conditions) {
        if (Objects.isNull(predicate)) {
            return false;
        }
        if (allTrue(conditions)) {
            return predicate.test(val1, val2);
        }
        return predicate.test(otherVal1, otherVal2);
    }

    /* **************************************************************** other **************************************************************** */

    /**
     * 判断表达式列表（conditionSuppliers）是否全部为真，如果提供的表达式列表为空，默认为真
     * <p>
     * 跳过判断为 null 的表达式
     */
    public boolean allTrue(BooleanSupplier... conditionSuppliers) {
        if (Objects.nonNull(conditionSuppliers)) {
            for (BooleanSupplier supplier : conditionSuppliers) {
                if (Objects.nonNull(supplier) && !supplier.getAsBoolean()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断条件列表（conditions）是否全部为真，如果提供的表达式列表为空，默认为真
     */
    public boolean allTrue(boolean... conditions) {
        if (Objects.nonNull(conditions)) {
            for (boolean condition : conditions) {
                if (!condition) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断表达式列表（conditionSuppliers）是否全部为假，如果提供的表达式列表为空，默认为假
     * <p>
     * 跳过判断为 null 的表达式
     */
    public boolean allFalse(BooleanSupplier... conditionSuppliers) {
        if (Objects.nonNull(conditionSuppliers)) {
            for (BooleanSupplier supplier : conditionSuppliers) {
                if (Objects.nonNull(supplier) && supplier.getAsBoolean()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断条件列表（conditions）是否全部为假，如果提供的表达式列表为空，默认为假
     */
    public boolean allFalse(boolean... conditions) {
        if (Objects.nonNull(conditions)) {
            for (boolean condition : conditions) {
                if (condition) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断表达式列表（conditionSuppliers）是否存在真，如果提供的表达式列表为空，默认为假
     * <p>
     * 跳过判断为 null 的表达式
     */
    public boolean anyTrue(BooleanSupplier... conditionSuppliers) {
        if (Objects.nonNull(conditionSuppliers)) {
            for (BooleanSupplier supplier : conditionSuppliers) {
                if (Objects.nonNull(supplier) && supplier.getAsBoolean()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断条件列表（conditions）是否存在真，如果提供的表达式列表为空，默认为假
     */
    public boolean anyTrue(boolean... conditions) {
        if (Objects.nonNull(conditions)) {
            for (boolean condition : conditions) {
                if (condition) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断表达式列表（conditionSuppliers）是否存在假，如果提供的表达式列表为空，默认为假
     * <p>
     * 跳过判断为 null 的表达式
     */
    public boolean anyFalse(BooleanSupplier... conditionSuppliers) {
        if (Objects.nonNull(conditionSuppliers)) {
            for (BooleanSupplier supplier : conditionSuppliers) {
                if (Objects.nonNull(supplier) && !supplier.getAsBoolean()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断条件列表（conditions）是否存在假，如果提供的表达式列表为空，默认为假
     */
    public boolean anyFalse(boolean... conditions) {
        if (Objects.nonNull(conditions)) {
            for (boolean condition : conditions) {
                if (!condition) {
                    return true;
                }
            }
        }
        return false;
    }
}
