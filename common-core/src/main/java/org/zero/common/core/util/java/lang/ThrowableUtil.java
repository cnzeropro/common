package org.zero.common.core.util.java.lang;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/21
 */
public class ThrowableUtil {
    public static <E extends RuntimeException> E throwUnchecked(Throwable t) {
        Objects.requireNonNull(t, "Throwable must not be null");
        return ThrowableUtil.throwAny(t);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> E throwAny(Throwable t) throws E {
        throw (E) t;
    }

    @FunctionalInterface
    public interface ThrowThrowableFunction<T, R> {
        R apply(T t) throws Throwable;
    }

    public static <T, R> Function<T, R> sneakyThrow(ThrowThrowableFunction<T, R> function) {
        return o -> sneakyThrow(o, function);
    }

    public static <T, R> R sneakyThrow(T o, ThrowThrowableFunction<T, R> function) {
        try {
            return function.apply(o);
        } catch (Throwable t) {
            throw throwUnchecked(t);
        }
    }

    @FunctionalInterface
    public interface ThrowThrowableSupplier<T> {
        T get() throws Throwable;
    }

    public static <T> Supplier<T> sneakyThrow(ThrowThrowableSupplier<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Throwable t) {
                throw throwUnchecked(t);
            }
        };
    }

    @FunctionalInterface
    public interface ThrowThrowableConsumer<T> {
        void accept(T t) throws Throwable;
    }

    public static <T> Consumer<T> sneakyThrow(ThrowThrowableConsumer<T> consumer) {
        return o -> {
            try {
                consumer.accept(o);
            } catch (Throwable t) {
                throw throwUnchecked(t);
            }
        };
    }

    @FunctionalInterface
    public interface ThrowThrowablePredicate<T> {
        boolean test(T t) throws Throwable;
    }

    public static <T> Predicate<T> sneakyThrow(ThrowThrowablePredicate<T> predicate) {
        return o -> {
            try {
                return predicate.test(o);
            } catch (Throwable t) {
                throw throwUnchecked(t);
            }
        };
    }

    @FunctionalInterface
    public interface ThrowThrowableRunnable {
        void run() throws Throwable;
    }

    public static Runnable sneakyThrow(ThrowThrowableRunnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Throwable t) {
                throw throwUnchecked(t);
            }
        };
    }

    @FunctionalInterface
    public interface ThrowThrowableCallable<T> {
        T call() throws Throwable;
    }

    public static <T> Callable<T> sneakyThrow(ThrowThrowableCallable<T> callable) {
        return () -> {
            try {
                return callable.call();
            } catch (Throwable t) {
                throw throwUnchecked(t);
            }
        };
    }

    public static <T> T ignore(Supplier<T> supplier) {
        return ignoreOpt(supplier).orElse(null);
    }

    public static <T> Optional<T> ignoreOpt(Supplier<T> supplier) {
        return Optional.ofNullable(ignore(supplier, (T) null));
    }

    public static <T> T ignore(Supplier<T> supplier, T exceptionDefault) {
        try {
            return supplier.get();
        } catch (Throwable ignored) {
            return exceptionDefault;
        }
    }

    public static <T, R> R ignore(T o, Function<T, R> function) {
        return ThrowableUtil.ignoreOpt(o, function).orElse(null);
    }

    public static <T, R> Optional<R> ignoreOpt(T o, Function<T, R> function) {
        return Optional.ofNullable(ignore(o, function, null));
    }

    public static <T, R> R ignore(T o, Function<T, R> function, R exceptionDefault) {
        try {
            return function.apply(o);
        } catch (Throwable ignored) {
            return exceptionDefault;
        }
    }

    public static <T extends Throwable> void tryThrowNew(Runnable snippet, Function<Throwable, ? extends T> exceptionMapper) {
        try {
            snippet.run();
        } catch (Throwable e) {
            T t = exceptionMapper.apply(e);
            throw throwUnchecked(t);
        }
    }

    public static <T extends Throwable> void tryThrowNew(Runnable snippet, BiFunction<String, Throwable, ? extends T> exceptionMapper, String message) {
        try {
            snippet.run();
        } catch (Throwable e) {
            T t = exceptionMapper.apply(CharSequenceUtil.defaultIfNull(message, e.getMessage()), e);
            throw throwUnchecked(t);
        }
    }

    public static <V, T extends Throwable> V tryThrowNew(Callable<V> snippet, Function<Throwable, ? extends T> exceptionMapper) {
        try {
            return snippet.call();
        } catch (Throwable e) {
            T t = exceptionMapper.apply(e);
            throw throwUnchecked(t);
        }
    }

    public static <V, T extends Throwable> V tryThrowNew(Callable<V> snippet, BiFunction<String, Throwable, ? extends T> exceptionMapper, String message) {
        try {
            return snippet.call();
        } catch (Throwable e) {
            T t = exceptionMapper.apply(CharSequenceUtil.defaultIfNull(message, e.getMessage()), e);
            throw throwUnchecked(t);
        }
    }

    protected ThrowableUtil() {
        throw new UnsupportedOperationException();
    }
}
