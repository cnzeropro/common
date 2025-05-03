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
    public static <E extends RuntimeException> E throwUnchecked(Throwable throwable) {
        Objects.requireNonNull(throwable, "Throwable must not be null");
        return ThrowableUtil.throwAny(throwable);
    }

    @SuppressWarnings("unchecked")
    protected static <E extends Throwable> E throwAny(Throwable throwable) throws E {
        throw (E) throwable;
    }

    /* ******************************************** sneakyThrow ******************************************** */
    /* -------------------------------------------- Function -------------------------------------------- */
    @FunctionalInterface
    public interface ThrowThrowableFunction<T, R> {
        R apply(T t) throws Throwable;
    }

    public static <T, R> Function<T, R> sneakyThrow(ThrowThrowableFunction<T, R> function) {
        return t -> sneakyThrow(t, function);
    }

    public static <T, R> R sneakyThrow(T t, ThrowThrowableFunction<T, R> function) {
        try {
            return function.apply(t);
        } catch (Throwable throwable) {
            throw throwUnchecked(throwable);
        }
    }

    /* -------------------------------------------- BiFunction -------------------------------------------- */
    @FunctionalInterface
    public interface ThrowThrowableBiFunction<T, U, R> {
        R apply(T t, U u) throws Throwable;
    }

    public static <T, U, R> BiFunction<T, U, R> sneakyThrow(ThrowThrowableBiFunction<T, U, R> function) {
        return (t, u) -> sneakyThrow(t, u, function);
    }

    public static <T, U, R> R sneakyThrow(T t, U u, ThrowThrowableBiFunction<T, U, R> function) {
        try {
            return function.apply(t, u);
        } catch (Throwable throwable) {
            throw throwUnchecked(throwable);
        }
    }

    /* -------------------------------------------- Supplier -------------------------------------------- */
    @FunctionalInterface
    public interface ThrowThrowableSupplier<T> {
        T get() throws Throwable;
    }

    public static <T> Supplier<T> sneakyThrow(ThrowThrowableSupplier<T> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Throwable throwable) {
                throw throwUnchecked(throwable);
            }
        };
    }

    /* -------------------------------------------- Consumer -------------------------------------------- */
    @FunctionalInterface
    public interface ThrowThrowableConsumer<T> {
        void accept(T t) throws Throwable;
    }

    public static <T> Consumer<T> sneakyThrow(ThrowThrowableConsumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Throwable throwable) {
                throw throwUnchecked(throwable);
            }
        };
    }

    /* -------------------------------------------- Predicate -------------------------------------------- */
    @FunctionalInterface
    public interface ThrowThrowablePredicate<T> {
        boolean test(T t) throws Throwable;
    }

    public static <T> Predicate<T> sneakyThrow(ThrowThrowablePredicate<T> predicate) {
        return t -> {
            try {
                return predicate.test(t);
            } catch (Throwable throwable) {
                throw throwUnchecked(throwable);
            }
        };
    }

    /* -------------------------------------------- Runnable -------------------------------------------- */
    @FunctionalInterface
    public interface ThrowThrowableRunnable {
        void run() throws Throwable;
    }

    public static Runnable sneakyThrow(ThrowThrowableRunnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Throwable throwable) {
                throw throwUnchecked(throwable);
            }
        };
    }

    /* -------------------------------------------- Callable -------------------------------------------- */
    @FunctionalInterface
    public interface ThrowThrowableCallable<T> {
        T call() throws Throwable;
    }

    public static <T> Callable<T> sneakyThrow(ThrowThrowableCallable<T> callable) {
        return () -> {
            try {
                return callable.call();
            } catch (Throwable throwable) {
                throw throwUnchecked(throwable);
            }
        };
    }

    /* ******************************************** ignore ******************************************** */
    /* -------------------------------------------- Supplier -------------------------------------------- */
    public static <T> T ignore(ThrowThrowableSupplier<T> supplier) {
        return ignore(supplier, (T) null);
    }

    public static <T> T ignore(ThrowThrowableSupplier<T> supplier, T exceptionDefault) {
        try {
            return supplier.get();
        } catch (Throwable ignored) {
            return exceptionDefault;
        }
    }

    public static <T> Optional<T> ignoreOpt(ThrowThrowableSupplier<T> supplier) {
        return Optional.ofNullable(ignore(supplier));
    }

    /* -------------------------------------------- Predicate -------------------------------------------- */
    public static <T> boolean ignore(T obj, ThrowThrowablePredicate<T> predicate) {
        return ignore(obj, predicate, false);
    }

    public static <T> boolean ignore(T obj, ThrowThrowablePredicate<T> predicate, boolean exceptionDefault) {
        try {
            return predicate.test(obj);
        } catch (Throwable ignored) {
            return exceptionDefault;
        }
    }

    /* -------------------------------------------- Function -------------------------------------------- */
    public static <T, R> R ignore(T obj, ThrowThrowableFunction<T, R> function) {
        return ignore(obj, function, (R) null);
    }

    public static <T, R> R ignore(T obj, ThrowThrowableFunction<T, R> function, R exceptionDefault) {
        try {
            return function.apply(obj);
        } catch (Throwable ignored) {
            return exceptionDefault;
        }
    }

    public static <T, R> Optional<R> ignoreOpt(T obj, ThrowThrowableFunction<T, R> function) {
        return Optional.ofNullable(ignore(obj, function));
    }

    /* ******************************************** tryThrowNew ******************************************** */
    public static <T extends Throwable> void tryThrowNew(Runnable snippet, Supplier<? extends T> exceptionSupplier) {
        try {
            snippet.run();
        } catch (Throwable ignored) {
            T t = exceptionSupplier.get();
            throw throwUnchecked(t);
        }
    }

    public static <T extends Throwable> void tryThrowNew(Runnable snippet, Function<Throwable, ? extends T> exceptionMapper) {
        try {
            snippet.run();
        } catch (Throwable throwable) {
            T t = exceptionMapper.apply(throwable);
            throw throwUnchecked(t);
        }
    }

    public static <T extends Throwable> void tryThrowNew(Runnable snippet, BiFunction<CharSequence, Throwable, ? extends T> exceptionMapper) {
        try {
            snippet.run();
        } catch (Throwable throwable) {
            T t = exceptionMapper.apply(throwable.getMessage(), throwable);
            throw throwUnchecked(t);
        }
    }

    public static <T extends Throwable> void tryThrowNew(Runnable snippet, BiFunction<CharSequence, Throwable, ? extends T> exceptionMapper, CharSequence message) {
        try {
            snippet.run();
        } catch (Throwable throwable) {
            T t = exceptionMapper.apply(message, throwable);
            throw throwUnchecked(t);
        }
    }

    public static <V, T extends Throwable> V tryThrowNew(Callable<V> snippet, Supplier<? extends T> exceptionSupplier) {
        try {
            return snippet.call();
        } catch (Throwable ignored) {
            T t = exceptionSupplier.get();
            throw throwUnchecked(t);
        }
    }

    public static <V, T extends Throwable> V tryThrowNew(Callable<V> snippet, Function<Throwable, ? extends T> exceptionMapper) {
        try {
            return snippet.call();
        } catch (Throwable throwable) {
            T t = exceptionMapper.apply(throwable);
            throw throwUnchecked(t);
        }
    }

    public static <V, T extends Throwable> V tryThrowNew(Callable<V> snippet, BiFunction<CharSequence, Throwable, ? extends T> exceptionMapper) {
        try {
            return snippet.call();
        } catch (Throwable throwable) {
            T t = exceptionMapper.apply(throwable.getMessage(), throwable);
            throw throwUnchecked(t);
        }
    }

    public static <V, T extends Throwable> V tryThrowNew(Callable<V> snippet, BiFunction<CharSequence, Throwable, ? extends T> exceptionMapper, CharSequence message) {
        try {
            return snippet.call();
        } catch (Throwable throwable) {
            T t = exceptionMapper.apply(message, throwable);
            throw throwUnchecked(t);
        }
    }

    protected ThrowableUtil() {
        throw new UnsupportedOperationException();
    }
}
