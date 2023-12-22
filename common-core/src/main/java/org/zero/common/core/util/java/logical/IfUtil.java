package org.zero.common.core.util.java.logical;

import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2022/11/30
 */
@UtilityClass
public class IfUtil {
    /**
     * 如果提供的表达式（suppliers）都为true，则进行转换操作
     */
    public <T, R> R map(T val, Function<T, R> function, BooleanSupplier... suppliers) {
        return mapOrGet(val, null, function, suppliers);
    }

    /**
     * 如果提供的条件（conditions）都为true，则进行转换操作
     */
    public <T, R> R map(T val, Function<T, R> function, boolean... conditions) {
        return mapOrGet(val, null, function, conditions);
    }

    /**
     * 如果提供的表达式（suppliers）都为true，则用val进行转换操作，否则用otherVal进行转换操作
     */
    public <T, R> R map(T val, T otherVal, Function<T, R> function, BooleanSupplier... suppliers) {
        if (Objects.isNull(function)) {
            return null;
        }
        if (allTrue(suppliers)) {
            return function.apply(val);
        }
        return function.apply(otherVal);
    }

    /**
     * 如果提供的条件（conditions）都为true，则用val进行转换操作，否则用otherVal进行转换操作
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
     * 如果提供的表达式（suppliers）都为true，则用val进行转换操作，否则直接返回otherVal
     */
    public <T, R> R mapOrGet(T val, R otherVal, Function<T, R> function, BooleanSupplier... suppliers) {
        if (Objects.isNull(function)) {
            return null;
        }
        if (allTrue(suppliers)) {
            return function.apply(val);
        }
        return otherVal;
    }

    /**
     * 如果提供的条件（conditions）都为true，则用val进行转换操作，否则直接返回otherVal
     */
    public <T, R> R mapOrGet(T val, R otherVal, Function<T, R> function, boolean... conditions) {
        if (Objects.isNull(function)) {
            return null;
        }
        if (allTrue(conditions)) {
            return function.apply(val);
        }
        return otherVal;
    }

    /**
     * 如果提供的表达式（suppliers）都为true，则从supplier中获取值
     */
    public <T> T get(Supplier<T> supplier, BooleanSupplier... suppliers) {
        return get(null, supplier, suppliers);
    }

    /**
     * 如果提供的条件（conditions）都为true，则从supplier中获取值
     */
    public <T> T get(Supplier<T> supplier, boolean... conditions) {
        return get(null, supplier, conditions);
    }

    /**
     * 如果提供的表达式（suppliers）都为true，则从supplier中获取值，否则返回otherVal
     */
    public <T> T get(T otherVal, Supplier<T> supplier, BooleanSupplier... suppliers) {
        if (Objects.isNull(supplier)) {
            return null;
        }
        if (allTrue(suppliers)) {
            return supplier.get();
        }
        return otherVal;
    }

    /**
     * 如果提供的条件（conditions）都为true，则从supplier中获取值，否则返回otherVal
     */
    public <T> T get(T otherVal, Supplier<T> supplier, boolean... conditions) {
        if (Objects.isNull(supplier)) {
            return null;
        }
        if (allTrue(conditions)) {
            return supplier.get();
        }
        return otherVal;
    }

    /**
     * 如果提供的表达式（suppliers）都为true，则消费val
     */
    public <T> void deal(T val, Consumer<T> consumer, BooleanSupplier... suppliers) {
        if (Objects.nonNull(consumer) && allTrue(suppliers)) {
            consumer.accept(val);
        }
    }

    /**
     * 如果提供的条件（conditions）都为true，则消费val
     */
    public <T> void deal(T val, Consumer<T> consumer, boolean... conditions) {
        if (Objects.nonNull(consumer) && allTrue(conditions)) {
            consumer.accept(val);
        }
    }

    /**
     * 如果提供的表达式（suppliers）都为true，则消费val，否则消费otherVal
     */
    public <T> void deal(T val, T otherVal, Consumer<T> consumer, BooleanSupplier... suppliers) {
        if (Objects.nonNull(consumer)) {
            if (allTrue(suppliers)) {
                consumer.accept(val);
            } else {
                consumer.accept(otherVal);
            }
        }
    }

    /**
     * 如果提供的条件（conditions）都为true，则消费val，否则消费otherVal
     */
    public <T> void deal(T val, T otherVal, Consumer<T> consumer, boolean... conditions) {
        if (Objects.nonNull(consumer)) {
            if (allTrue(conditions)) {
                consumer.accept(val);
            } else {
                consumer.accept(otherVal);
            }
        }
    }

    /**
     * 如果提供的表达式（suppliers）都为true，则消费val1和val2
     */
    public <T, U> void deal(T val1, U val2, BiConsumer<T, U> consumer, BooleanSupplier... suppliers) {
        if (Objects.nonNull(consumer) && allTrue(suppliers)) {
            consumer.accept(val1, val2);
        }
    }

    /**
     * 如果提供的条件（conditions）都为true，则消费val1和val2
     */
    public <T, U> void deal(T val1, U val2, BiConsumer<T, U> consumer, boolean... conditions) {
        if (Objects.nonNull(consumer) && allTrue(conditions)) {
            consumer.accept(val1, val2);
        }
    }

    /**
     * 如果提供的表达式（suppliers）都为true，则判断val
     */
    public <T> boolean test(T val, Predicate<T> predicate, BooleanSupplier... suppliers) {
        if (Objects.isNull(predicate)) {
            return false;
        }
        if (allTrue(suppliers)) {
            return predicate.test(val);
        }
        return false;
    }

    /**
     * 如果提供的条件（conditions）都为true，则判断val
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
     * 如果提供的表达式（suppliers）都为true，则判断val，否则判断otherVal
     */
    public <T> boolean test(T val, T otherVal, Predicate<T> predicate, BooleanSupplier... suppliers) {
        if (Objects.isNull(predicate)) {
            return false;
        }
        if (allTrue(suppliers)) {
            return predicate.test(val);
        }
        return predicate.test(otherVal);
    }

    /**
     * 如果提供的条件（conditions）都为true，则判断val，否则判断otherVal
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
     * 判断表达式列表（suppliers）是否全部为true，如果提供的表达式列表为null，默认true
     * <p>
     * 跳过判断为null的表达式
     */
    public boolean allTrue(BooleanSupplier... suppliers) {
        if (Objects.nonNull(suppliers)) {
            for (BooleanSupplier supplier : suppliers) {
                if (Objects.nonNull(supplier) && !supplier.getAsBoolean()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断条件列表（conditions）是否全部为true，如果提供的表达式列表为null，默认true
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
     * 判断表达式列表（suppliers）是否全部为false
     */
    public boolean allFalse(BooleanSupplier... suppliers) {
        if (Objects.nonNull(suppliers)) {
            for (BooleanSupplier supplier : suppliers) {
                if (Objects.nonNull(supplier) && supplier.getAsBoolean()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断条件列表（conditions）是否全部为false
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
     * 判断表达式列表（suppliers）是否有一个为true
     */
    public boolean anyTrue(BooleanSupplier... suppliers) {
        if (Objects.nonNull(suppliers)) {
            for (BooleanSupplier supplier : suppliers) {
                if (Objects.nonNull(supplier) && supplier.getAsBoolean()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断条件列表（conditions）是否有一个为true
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
     * 判断表达式列表（suppliers）是否有一个为false
     */
    public boolean anyFalse(BooleanSupplier... suppliers) {
        if (Objects.nonNull(suppliers)) {
            for (BooleanSupplier supplier : suppliers) {
                if (Objects.nonNull(supplier) && !supplier.getAsBoolean()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断条件列表（conditions）是否有一个为false
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
