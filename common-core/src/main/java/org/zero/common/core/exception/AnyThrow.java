package org.zero.common.core.exception;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/21
 */
public final class AnyThrow {
    public static RuntimeException throwUnchecked(Throwable t) {
        Objects.requireNonNull(t, "Throwable must not be null");
        return AnyThrow.throwAny(t);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> E throwAny(Throwable t) throws E {
        throw (E) t;
    }

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws Throwable;
    }

    public static <T, R> R sneakyThrow(T o, CheckedFunction<T, R> function) {
        try {
            return function.apply(o);
        } catch (Throwable t) {
            throw throwUnchecked(t);
        }
    }

    public static <T, R> Function<T, R> sneakyThrow(CheckedFunction<T, R> function) {
        return o -> {
            try {
                return function.apply(o);
            } catch (Throwable t) {
                throw throwUnchecked(t);
            }
        };
    }

    @FunctionalInterface
    public interface CheckedSupplier<T> {
        T get() throws Throwable;
    }

    public static <T> Supplier<T> sneakyThrow(CheckedSupplier<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Throwable t) {
                throw throwUnchecked(t);
            }
        };
    }

    @FunctionalInterface
    public interface CheckedConsumer<T> {
        void accept(T t) throws Throwable;
    }

    public static <T> Consumer<T> sneakyThrow(CheckedConsumer<T> consumer) {
        return o -> {
            try {
                consumer.accept(o);
            } catch (Throwable t) {
                throw throwUnchecked(t);
            }
        };
    }

    @FunctionalInterface
    public interface CheckedPredicate<T> {
        boolean test(T t) throws Throwable;
    }

    public static <T> Predicate<T> sneakyThrow(CheckedPredicate<T> predicate) {
        return o -> {
            try {
                return predicate.test(o);
            } catch (Throwable t) {
                throw throwUnchecked(t);
            }
        };
    }

    @FunctionalInterface
    public interface CheckedRunnable {
        void run() throws Throwable;
    }

    public static Runnable sneakyThrow(CheckedRunnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Throwable t) {
                throw throwUnchecked(t);
            }
        };
    }

    @FunctionalInterface
    public interface CheckedCallable<T> {
        T call() throws Throwable;
    }

    public static <T> Callable<T> sneakyThrow(CheckedCallable<T> callable) {
        return () -> {
            try {
                return callable.call();
            } catch (Throwable t) {
                throw throwUnchecked(t);
            }
        };
    }

    private AnyThrow() {
    }
}
