package org.zero.common.core.util.java.lang;

import org.zero.common.core.extension.java.lang.ThrowableRunnable;
import org.zero.common.core.extension.java.util.ThrowableComparator;
import org.zero.common.core.extension.java.util.concurrent.ThrowableCallable;
import org.zero.common.core.extension.java.util.function.ThrowableBiFunction;
import org.zero.common.core.extension.java.util.function.ThrowableConsumer;
import org.zero.common.core.extension.java.util.function.ThrowableFunction;
import org.zero.common.core.extension.java.util.function.ThrowablePredicate;
import org.zero.common.core.extension.java.util.function.ThrowableSupplier;
import org.zero.common.core.extension.java.util.function.ToBoolFunction;
import org.zero.common.data.constant.ConstantPool;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

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
    protected static final Consumer<Throwable> SNEAKY_THROW_THROWABLE_CONSUMER = ThrowableUtil::throwUnchecked;

    protected static <T> Function<Throwable, T> sneakyThrowableMapper() {
        return throwable -> {
            throw throwUnchecked(throwable);
        };
    }

    /* -------------------------------------------- Runnable -------------------------------------------- */
    public static Runnable sneakyThrow(ThrowableRunnable runnable) {
        return runnable.to();
    }

    public static void sneakyThrow(Runnable runnable) {
        tryDo(ThrowableRunnable.of(runnable), SNEAKY_THROW_THROWABLE_CONSUMER);
    }

    /* -------------------------------------------- Callable -------------------------------------------- */
    public static <V> Callable<V> sneakyThrow(ThrowableCallable<V> callable) {
        return callable.to();
    }

    public static <V> V sneakyThrow(Callable<V> callable) {
        return tryReturnNew(ThrowableCallable.of(callable), sneakyThrowableMapper());
    }

    public static <V> Optional<V> sneakyThrowOpt(Callable<V> callable) {
        return Optional.ofNullable(sneakyThrow(callable));
    }

    /* -------------------------------------------- Function -------------------------------------------- */
    public static <T, R> Function<T, R> sneakyThrow(ThrowableFunction<T, R> function) {
        return function.to();
    }

    public static <T, R> R sneakyThrow(T t, ThrowableFunction<T, R> function) {
        return tryReturnNew(t, function, sneakyThrowableMapper());
    }

    public static <T, R> Optional<R> sneakyThrowOpt(T t, ThrowableFunction<T, R> function) {
        return Optional.ofNullable(sneakyThrow(t, function));
    }

    public static <T, R> R sneakyThrow(T t, Function<T, R> function) {
        return sneakyThrow(t, ThrowableFunction.of(function));
    }

    public static <T, R> Optional<R> sneakyThrowOpt(T t, Function<T, R> function) {
        return Optional.ofNullable(sneakyThrow(t, function));
    }

    /* -------------------------------------------- BiFunction -------------------------------------------- */
    public static <T, U, R> BiFunction<T, U, R> sneakyThrow(ThrowableBiFunction<T, U, R> function) {
        return function.to();
    }

    public static <T, U, R> R sneakyThrow(T t, U u, ThrowableBiFunction<T, U, R> function) {
        return tryReturnNew(t, u, function, sneakyThrowableMapper());
    }

    public static <T, U, R> Optional<R> sneakyThrowOpt(T t, U u, ThrowableBiFunction<T, U, R> function) {
        return Optional.ofNullable(sneakyThrow(t, u, function));
    }

    public static <T, U, R> R sneakyThrow(T t, U u, BiFunction<T, U, R> function) {
        return sneakyThrow(t, u, ThrowableBiFunction.of(function));
    }

    public static <T, U, R> Optional<R> sneakyThrowOpt(T t, U u, BiFunction<T, U, R> function) {
        return Optional.ofNullable(sneakyThrow(t, u, function));
    }

    /* -------------------------------------------- Supplier -------------------------------------------- */
    public static <T> Supplier<T> sneakyThrow(ThrowableSupplier<T> supplier) {
        return supplier.to();
    }

    public static <T> T sneakyThrow(Supplier<T> supplier) {
        return tryReturnNew(ThrowableSupplier.of(supplier), sneakyThrowableMapper());
    }

    public static <T> Optional<T> sneakyThrowOpt(Supplier<T> supplier) {
        return Optional.ofNullable(sneakyThrow(supplier));
    }

    /* -------------------------------------------- Consumer -------------------------------------------- */
    public static <T> Consumer<T> sneakyThrow(ThrowableConsumer<T> consumer) {
        return consumer.to();
    }

    public static <T> void sneakyThrow(T t, ThrowableConsumer<T> consumer) {
        tryDo(t, consumer, IGNORE_THROWABLE_CONSUMER);
    }

    public static <T> void sneakyThrow(T t, Consumer<T> consumer) {
        sneakyThrow(t, ThrowableConsumer.of(consumer));
    }

    /* -------------------------------------------- Predicate -------------------------------------------- */
    public static <T> Predicate<T> sneakyThrow(ThrowablePredicate<T> predicate) {
        return predicate.to();
    }

    public static <T> boolean sneakyThrow(T t, ThrowablePredicate<T> predicate) {
        return tryReturnNew(t, predicate, throwable -> {
            throw throwUnchecked(throwable);
        });
    }

    public static <T> boolean sneakyThrow(T t, Predicate<T> predicate) {
        return sneakyThrow(t, ThrowablePredicate.of(predicate));
    }

    /* -------------------------------------------- Comparator -------------------------------------------- */
    public static <T> Comparator<T> sneakyThrow(ThrowableComparator<T> comparator) {
        return comparator.to();
    }

    public static <T> int sneakyThrow(T t1, T t2, ThrowableComparator<T> comparator) {
        return tryReturnNew(t1, t2, comparator, throwable -> {
            throw throwUnchecked(throwable);
        });
    }

    public static <T> int sneakyThrow(T t1, T t2, Comparator<T> comparator) {
        return sneakyThrow(t1, t2, ThrowableComparator.of(comparator));
    }

    /* ******************************************** ignore ******************************************** */
    protected static final Consumer<Throwable> IGNORE_THROWABLE_CONSUMER = ignored -> {
    };

    /* -------------------------------------------- Runnable -------------------------------------------- */
    public static void ignore(ThrowableRunnable runnable) {
        tryDo(runnable, IGNORE_THROWABLE_CONSUMER);
    }

    /* -------------------------------------------- Callable -------------------------------------------- */
    public static <V> V ignore(ThrowableCallable<V> callable) {
        return tryReturnNew(callable, (V) null);
    }

    public static <V> Optional<V> ignoreOpt(ThrowableCallable<V> callable) {
        return Optional.ofNullable(ignore(callable));
    }

    /* -------------------------------------------- Function -------------------------------------------- */
    public static <T, R> R ignore(T obj, ThrowableFunction<T, R> function) {
        return tryReturnNew(obj, function, (R) null);
    }

    public static <T, R> Optional<R> ignoreOpt(T obj, ThrowableFunction<T, R> function) {
        return Optional.ofNullable(ignore(obj, function));
    }

    /* -------------------------------------------- BiFunction -------------------------------------------- */
    public static <T, U, R> R ignore(T t, U u, ThrowableBiFunction<T, U, R> function) {
        return tryReturnNew(t, u, function, (R) null);
    }

    public static <T, U, R> Optional<R> ignoreOpt(T t, U u, ThrowableBiFunction<T, U, R> function) {
        return Optional.ofNullable(ignore(t, u, function));
    }

    /* -------------------------------------------- Supplier -------------------------------------------- */
    public static <T> T ignore(ThrowableSupplier<T> supplier) {
        return tryReturnNew(supplier, (T) null);
    }

    public static <T> Optional<T> ignoreOpt(ThrowableSupplier<T> supplier) {
        return Optional.ofNullable(ignore(supplier));
    }

    /* -------------------------------------------- Consumer -------------------------------------------- */
    public static <T> void ignore(T t, ThrowableConsumer<T> consumer) {
        tryDo(t, consumer, IGNORE_THROWABLE_CONSUMER);
    }

    /* -------------------------------------------- Predicate -------------------------------------------- */
    public static <T> boolean ignore(T t, ThrowablePredicate<T> predicate) {
        return tryReturnNew(t, predicate, ConstantPool.BOOLEAN_FALSE);
    }

    /* -------------------------------------------- Comparator -------------------------------------------- */
    public static <T> int ignore(T t1, T t2, ThrowableComparator<T> comparator) {
        return tryReturnNew(t1, t2, comparator, ConstantPool.INT_ZERO);
    }

    /* ******************************************** tryDo ******************************************** */
    /* -------------------------------------------- Runnable -------------------------------------------- */
    public static void tryDo(ThrowableRunnable runnable, Consumer<Throwable> throwableConsumer) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            throwableConsumer.accept(throwable);
        }
    }

    /* -------------------------------------------- Callable -------------------------------------------- */
    // no implement

    /* -------------------------------------------- Function -------------------------------------------- */
    // no implement

    /* -------------------------------------------- BiFunction -------------------------------------------- */
    // no implement

    /* -------------------------------------------- Supplier -------------------------------------------- */
    // no implement

    /* -------------------------------------------- Consumer -------------------------------------------- */
    public static <T> void tryDo(T t, ThrowableConsumer<T> consumer, Consumer<Throwable> throwableConsumer) {
        try {
            consumer.accept(t);
        } catch (Throwable throwable) {
            throwableConsumer.accept(throwable);
        }
    }

    /* -------------------------------------------- Predicate -------------------------------------------- */
    // no implement

    /* -------------------------------------------- Comparator -------------------------------------------- */
    // no implement

    /* ******************************************** tryReturnNew ******************************************** */
    protected static <T> Supplier<T> defaultValueSupplier(T defaultValue) {
        return () -> defaultValue;
    }

    protected static <T> Function<Throwable, T> throwableToNewValueMapper(Supplier<T> defaultValueSupplier) {
        return throwable -> defaultValueSupplier.get();
    }

    /* -------------------------------------------- Runnable -------------------------------------------- */
    // no implement

    /* -------------------------------------------- Callable -------------------------------------------- */
    public static <V> V tryReturnNew(ThrowableCallable<V> callable, V defaultValue) {
        return tryReturnNew(callable, defaultValueSupplier(defaultValue));
    }

    public static <V> Optional<V> tryReturnNewOpt(ThrowableCallable<V> callable, V defaultValue) {
        return Optional.ofNullable(tryReturnNew(callable, defaultValue));
    }

    public static <V> V tryReturnNew(ThrowableCallable<V> callable, Supplier<V> defaultValueSupplier) {
        return tryReturnNew(callable, throwableToNewValueMapper(defaultValueSupplier));
    }

    public static <V> Optional<V> tryReturnNewOpt(ThrowableCallable<V> callable, Supplier<V> defaultValueSupplier) {
        return Optional.ofNullable(tryReturnNew(callable, defaultValueSupplier));
    }

    public static <V> V tryReturnNew(ThrowableCallable<V> callable, Function<Throwable, V> throwableMapper) {
        try {
            return callable.call();
        } catch (Throwable throwable) {
            return throwableMapper.apply(throwable);
        }
    }

    public static <V> Optional<V> tryReturnNewOpt(ThrowableCallable<V> callable, Function<Throwable, V> throwableMapper) {
        return Optional.ofNullable(tryReturnNew(callable, throwableMapper));
    }

    /* -------------------------------------------- Function -------------------------------------------- */
    public static <T, R> R tryReturnNew(T t, ThrowableFunction<T, R> function, R defaultValue) {
        return tryReturnNew(t, function, defaultValueSupplier(defaultValue));
    }

    public static <T, R> Optional<R> tryReturnNewOpt(T t, ThrowableFunction<T, R> function, R defaultValue) {
        return Optional.ofNullable(tryReturnNew(t, function, defaultValue));
    }

    public static <T, R> R tryReturnNew(T t, ThrowableFunction<T, R> function, Supplier<R> defaultValueSupplier) {
        return tryReturnNew(t, function, throwableToNewValueMapper(defaultValueSupplier));
    }

    public static <T, R> Optional<R> tryReturnNewOpt(T t, ThrowableFunction<T, R> function, Supplier<R> defaultValueSupplier) {
        return Optional.ofNullable(tryReturnNew(t, function, defaultValueSupplier));
    }

    public static <T, R> R tryReturnNew(T t, ThrowableFunction<T, R> function, Function<Throwable, R> throwableMapper) {
        try {
            return function.apply(t);
        } catch (Throwable throwable) {
            return throwableMapper.apply(throwable);
        }
    }

    public static <T, R> Optional<R> tryReturnNewOpt(T t, ThrowableFunction<T, R> function, Function<Throwable, R> throwableMapper) {
        return Optional.ofNullable(tryReturnNew(t, function, throwableMapper));
    }

    /* -------------------------------------------- BiFunction -------------------------------------------- */
    public static <T, U, R> R tryReturnNew(T t, U u, ThrowableBiFunction<T, U, R> function, R defaultValue) {
        return tryReturnNew(t, u, function, defaultValueSupplier(defaultValue));
    }

    public static <T, U, R> Optional<R> tryReturnNewOpt(T t, U u, ThrowableBiFunction<T, U, R> function, R defaultValue) {
        return Optional.ofNullable(tryReturnNew(t, u, function, defaultValue));
    }

    public static <T, U, R> R tryReturnNew(T t, U u, ThrowableBiFunction<T, U, R> function, Supplier<R> defaultValueSupplier) {
        return tryReturnNew(t, u, function, throwableToNewValueMapper(defaultValueSupplier));
    }

    public static <T, U, R> Optional<R> tryReturnNewOpt(T t, U u, ThrowableBiFunction<T, U, R> function, Supplier<R> defaultValueSupplier) {
        return Optional.ofNullable(tryReturnNew(t, u, function, defaultValueSupplier));
    }

    public static <T, U, R> R tryReturnNew(T t, U u, ThrowableBiFunction<T, U, R> function, Function<Throwable, R> throwableMapper) {
        try {
            return function.apply(t, u);
        } catch (Throwable throwable) {
            return throwableMapper.apply(throwable);
        }
    }

    public static <T, U, R> Optional<R> tryReturnNewOpt(T t, U u, ThrowableBiFunction<T, U, R> function, Function<Throwable, R> throwableMapper) {
        return Optional.ofNullable(tryReturnNew(t, u, function, throwableMapper));
    }

    /* -------------------------------------------- Supplier -------------------------------------------- */
    public static <T> T tryReturnNew(ThrowableSupplier<T> supplier, T defaultValue) {
        return tryReturnNew(supplier, defaultValueSupplier(defaultValue));
    }

    public static <T> Optional<T> tryReturnNewOpt(ThrowableSupplier<T> supplier, T defaultValue) {
        return Optional.ofNullable(tryReturnNew(supplier, defaultValue));
    }

    public static <T> T tryReturnNew(ThrowableSupplier<T> supplier, Supplier<T> defaultValueSupplier) {
        return tryReturnNew(supplier, throwableToNewValueMapper(defaultValueSupplier));
    }

    public static <T> Optional<T> tryReturnNewOpt(ThrowableSupplier<T> supplier, Supplier<T> defaultValueSupplier) {
        return Optional.ofNullable(tryReturnNew(supplier, defaultValueSupplier));
    }

    public static <T> T tryReturnNew(ThrowableSupplier<T> supplier, Function<Throwable, T> throwableMapper) {
        try {
            return supplier.get();
        } catch (Throwable throwable) {
            return throwableMapper.apply(throwable);
        }
    }

    public static <T> Optional<T> tryReturnNewOpt(ThrowableSupplier<T> supplier, Function<Throwable, T> throwableMapper) {
        return Optional.ofNullable(tryReturnNew(supplier, throwableMapper));
    }

    /* -------------------------------------------- Consumer -------------------------------------------- */
    // no implement

    /* -------------------------------------------- Predicate -------------------------------------------- */
    public static <T> boolean tryReturnNew(T t, ThrowablePredicate<T> predicate, boolean defaultValue) {
        return tryReturnNew(t, predicate, () -> defaultValue);
    }

    public static <T> boolean tryReturnNew(T t, ThrowablePredicate<T> predicate, BooleanSupplier defaultValueSupplier) {
        return tryReturnNew(t, predicate, throwable -> defaultValueSupplier.getAsBoolean());
    }

    public static <T> boolean tryReturnNew(T t, ThrowablePredicate<T> predicate, ToBoolFunction<Throwable> throwableMapper) {
        try {
            return predicate.test(t);
        } catch (Throwable throwable) {
            return throwableMapper.applyAsBool(throwable);
        }
    }

    /* -------------------------------------------- Comparator -------------------------------------------- */
    public static <T> int tryReturnNew(T t1, T t2, ThrowableComparator<T> comparator, int defaultValue) {
        return tryReturnNew(t1, t2, comparator, () -> defaultValue);
    }

    public static <T> int tryReturnNew(T t1, T t2, ThrowableComparator<T> comparator, IntSupplier defaultValueSupplier) {
        return tryReturnNew(t1, t2, comparator, throwable -> defaultValueSupplier.getAsInt());
    }

    public static <T> int tryReturnNew(T t1, T t2, ThrowableComparator<T> comparator, ToIntFunction<Throwable> throwableMapper) {
        try {
            return comparator.compare(t1, t2);
        } catch (Throwable throwable) {
            return throwableMapper.applyAsInt(throwable);
        }
    }

    /* ******************************************** tryThrowNew ******************************************** */
    protected static <T extends Throwable> Supplier<T> defaultThrowableSupplier(T defaultThrowable) {
        return () -> defaultThrowable;
    }

    protected static <T extends Throwable> Function<Throwable, T> throwableToNewMapper(Supplier<? extends T> throwableSupplier) {
        return throwable -> throwableSupplier.get();
    }

    protected static <T extends Throwable> Function<Throwable, T> throwableToNewMapper(BiFunction<CharSequence, Throwable, ? extends T> throwableMapper) {
        return throwable -> throwableMapper.apply(throwable.getMessage(), throwable);
    }

    protected static <T extends Throwable> Function<Throwable, T> throwableToNewMapper(BiFunction<CharSequence, Throwable, ? extends T> throwableMapper, CharSequence message) {
        return throwable -> throwableMapper.apply(message, throwable);
    }

    /* -------------------------------------------- Runnable -------------------------------------------- */
    public static <T extends Throwable> void tryThrowNew(ThrowableRunnable runnable, T throwable) {
        tryThrowNew(runnable, defaultThrowableSupplier(throwable));
    }

    public static <T extends Throwable> void tryThrowNew(ThrowableRunnable runnable, Supplier<? extends T> throwableSupplier) {
        tryThrowNew(runnable, throwableToNewMapper(throwableSupplier));
    }

    public static <T extends Throwable> void tryThrowNew(ThrowableRunnable runnable, Function<Throwable, ? extends T> throwableMapper) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            T t = throwableMapper.apply(throwable);
            throw throwUnchecked(t);
        }
    }

    public static <T extends Throwable> void tryThrowNew(ThrowableRunnable runnable, BiFunction<CharSequence, Throwable, ? extends T> throwableMapper) {
        tryThrowNew(runnable, throwableToNewMapper(throwableMapper));
    }

    public static <T extends Throwable> void tryThrowNew(ThrowableRunnable runnable, BiFunction<CharSequence, Throwable, ? extends T> throwableMapper, CharSequence message) {
        tryThrowNew(runnable, throwableToNewMapper(throwableMapper, message));
    }

    /* -------------------------------------------- Callable -------------------------------------------- */
    public static <V, T extends Throwable> void tryThrowNew(ThrowableCallable<V> callable, T throwable) {
        tryThrowNew(callable, (Supplier<Throwable>) () -> throwable);
    }

    public static <V, T extends Throwable> V tryThrowNew(ThrowableCallable<V> callable, Supplier<? extends T> throwableSupplier) {
        return tryThrowNew(callable, (Function<Throwable, Throwable>) ignored -> throwableSupplier.get());
    }

    public static <V, T extends Throwable> V tryThrowNew(ThrowableCallable<V> callable, Function<Throwable, ? extends T> throwableMapper) {
        try {
            return callable.call();
        } catch (Throwable throwable) {
            T t = throwableMapper.apply(throwable);
            throw throwUnchecked(t);
        }
    }

    public static <V, T extends Throwable> V tryThrowNew(ThrowableCallable<V> callable, BiFunction<CharSequence, Throwable, ? extends T> throwableMapper) {
        return tryThrowNew(callable, throwableToNewMapper(throwableMapper));
    }

    public static <V, T extends Throwable> V tryThrowNew(ThrowableCallable<V> callable, BiFunction<CharSequence, Throwable, ? extends T> throwableMapper, CharSequence message) {
        return tryThrowNew(callable, throwableToNewMapper(throwableMapper, message));
    }

    protected ThrowableUtil() {
        throw new UnsupportedOperationException();
    }
}
