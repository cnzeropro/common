package org.zero.common.core.util.java.lang;

import org.zero.common.core.extension.java.lang.ThrowThrowableRunnable;
import org.zero.common.core.extension.java.util.ThrowThrowableComparator;
import org.zero.common.core.extension.java.util.concurrent.ThrowThrowableCallable;
import org.zero.common.core.extension.java.util.function.ThrowThrowableBiFunction;
import org.zero.common.core.extension.java.util.function.ThrowThrowableConsumer;
import org.zero.common.core.extension.java.util.function.ThrowThrowableFunction;
import org.zero.common.core.extension.java.util.function.ThrowThrowablePredicate;
import org.zero.common.core.extension.java.util.function.ThrowThrowableSupplier;
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

    protected static <T> Function<Throwable, T> sneakyThrowThrowableMapper() {
        return throwable -> {
            throw throwUnchecked(throwable);
        };
    }

    /* -------------------------------------------- Runnable -------------------------------------------- */
    public static Runnable sneakyThrow(ThrowThrowableRunnable runnable) {
        return runnable.to();
    }

    public static void sneakyThrow(Runnable runnable) {
        tryDo(ThrowThrowableRunnable.of(runnable), SNEAKY_THROW_THROWABLE_CONSUMER);
    }

    /* -------------------------------------------- Callable -------------------------------------------- */
    public static <V> Callable<V> sneakyThrow(ThrowThrowableCallable<V> callable) {
        return callable.to();
    }

    public static <V> V sneakyThrow(Callable<V> callable) {
        return tryReturnNew(ThrowThrowableCallable.of(callable), sneakyThrowThrowableMapper());
    }

    public static <V> Optional<V> sneakyThrowOpt(Callable<V> callable) {
        return Optional.ofNullable(sneakyThrow(callable));
    }

    /* -------------------------------------------- Function -------------------------------------------- */
    public static <T, R> Function<T, R> sneakyThrow(ThrowThrowableFunction<T, R> function) {
        return function.to();
    }

    public static <T, R> R sneakyThrow(T t, ThrowThrowableFunction<T, R> function) {
        return tryReturnNew(t, function, sneakyThrowThrowableMapper());
    }

    public static <T, R> Optional<R> sneakyThrowOpt(T t, ThrowThrowableFunction<T, R> function) {
        return Optional.ofNullable(sneakyThrow(t, function));
    }

    public static <T, R> R sneakyThrow(T t, Function<T, R> function) {
        return sneakyThrow(t, ThrowThrowableFunction.of(function));
    }

    public static <T, R> Optional<R> sneakyThrowOpt(T t, Function<T, R> function) {
        return Optional.ofNullable(sneakyThrow(t, function));
    }

    /* -------------------------------------------- BiFunction -------------------------------------------- */
    public static <T, U, R> BiFunction<T, U, R> sneakyThrow(ThrowThrowableBiFunction<T, U, R> function) {
        return function.to();
    }

    public static <T, U, R> R sneakyThrow(T t, U u, ThrowThrowableBiFunction<T, U, R> function) {
        return tryReturnNew(t, u, function, sneakyThrowThrowableMapper());
    }

    public static <T, U, R> Optional<R> sneakyThrowOpt(T t, U u, ThrowThrowableBiFunction<T, U, R> function) {
        return Optional.ofNullable(sneakyThrow(t, u, function));
    }

    public static <T, U, R> R sneakyThrow(T t, U u, BiFunction<T, U, R> function) {
        return sneakyThrow(t, u, ThrowThrowableBiFunction.of(function));
    }

    public static <T, U, R> Optional<R> sneakyThrowOpt(T t, U u, BiFunction<T, U, R> function) {
        return Optional.ofNullable(sneakyThrow(t, u, function));
    }

    /* -------------------------------------------- Supplier -------------------------------------------- */
    public static <T> Supplier<T> sneakyThrow(ThrowThrowableSupplier<T> supplier) {
        return supplier.to();
    }

    public static <T> T sneakyThrow(Supplier<T> supplier) {
        return tryReturnNew(ThrowThrowableSupplier.of(supplier), sneakyThrowThrowableMapper());
    }

    public static <T> Optional<T> sneakyThrowOpt(Supplier<T> supplier) {
        return Optional.ofNullable(sneakyThrow(supplier));
    }

    /* -------------------------------------------- Consumer -------------------------------------------- */
    public static <T> Consumer<T> sneakyThrow(ThrowThrowableConsumer<T> consumer) {
        return consumer.to();
    }

    public static <T> void sneakyThrow(T t, ThrowThrowableConsumer<T> consumer) {
        tryDo(t, consumer, IGNORE_THROWABLE_CONSUMER);
    }

    public static <T> void sneakyThrow(T t, Consumer<T> consumer) {
        sneakyThrow(t, ThrowThrowableConsumer.of(consumer));
    }

    /* -------------------------------------------- Predicate -------------------------------------------- */
    public static <T> Predicate<T> sneakyThrow(ThrowThrowablePredicate<T> predicate) {
        return predicate.to();
    }

    public static <T> boolean sneakyThrow(T t, ThrowThrowablePredicate<T> predicate) {
        return tryReturnNew(t, predicate, throwable -> {
            throw throwUnchecked(throwable);
        });
    }

    public static <T> boolean sneakyThrow(T t, Predicate<T> predicate) {
        return sneakyThrow(t, ThrowThrowablePredicate.of(predicate));
    }

    /* -------------------------------------------- Comparator -------------------------------------------- */
    public static <T> Comparator<T> sneakyThrow(ThrowThrowableComparator<T> comparator) {
        return comparator.to();
    }

    public static <T> int sneakyThrow(T t1, T t2, ThrowThrowableComparator<T> comparator) {
        return tryReturnNew(t1, t2, comparator, throwable -> {
            throw throwUnchecked(throwable);
        });
    }

    public static <T> int sneakyThrow(T t1, T t2, Comparator<T> comparator) {
        return sneakyThrow(t1, t2, ThrowThrowableComparator.of(comparator));
    }

    /* ******************************************** ignore ******************************************** */
    protected static final Consumer<Throwable> IGNORE_THROWABLE_CONSUMER = ignored -> {
    };

    /* -------------------------------------------- Runnable -------------------------------------------- */
    public static void ignore(ThrowThrowableRunnable runnable) {
        tryDo(runnable, IGNORE_THROWABLE_CONSUMER);
    }

    /* -------------------------------------------- Callable -------------------------------------------- */
    public static <V> V ignore(ThrowThrowableCallable<V> callable) {
        return tryReturnNew(callable, (V) null);
    }

    public static <V> Optional<V> ignoreOpt(ThrowThrowableCallable<V> callable) {
        return Optional.ofNullable(ignore(callable));
    }

    /* -------------------------------------------- Function -------------------------------------------- */
    public static <T, R> R ignore(T obj, ThrowThrowableFunction<T, R> function) {
        return tryReturnNew(obj, function, (R) null);
    }

    public static <T, R> Optional<R> ignoreOpt(T obj, ThrowThrowableFunction<T, R> function) {
        return Optional.ofNullable(ignore(obj, function));
    }

    /* -------------------------------------------- BiFunction -------------------------------------------- */
    public static <T, U, R> R ignore(T t, U u, ThrowThrowableBiFunction<T, U, R> function) {
        return tryReturnNew(t, u, function, (R) null);
    }

    public static <T, U, R> Optional<R> ignoreOpt(T t, U u, ThrowThrowableBiFunction<T, U, R> function) {
        return Optional.ofNullable(ignore(t, u, function));
    }

    /* -------------------------------------------- Supplier -------------------------------------------- */
    public static <T> T ignore(ThrowThrowableSupplier<T> supplier) {
        return tryReturnNew(supplier, (T) null);
    }

    public static <T> Optional<T> ignoreOpt(ThrowThrowableSupplier<T> supplier) {
        return Optional.ofNullable(ignore(supplier));
    }

    /* -------------------------------------------- Consumer -------------------------------------------- */
    public static <T> void ignore(T t, ThrowThrowableConsumer<T> consumer) {
        tryDo(t, consumer, IGNORE_THROWABLE_CONSUMER);
    }

    /* -------------------------------------------- Predicate -------------------------------------------- */
    public static <T> boolean ignore(T t, ThrowThrowablePredicate<T> predicate) {
        return tryReturnNew(t, predicate, ConstantPool.BOOLEAN_FALSE);
    }

    /* -------------------------------------------- Comparator -------------------------------------------- */
    public static <T> int ignore(T t1, T t2, ThrowThrowableComparator<T> comparator) {
        return tryReturnNew(t1, t2, comparator, ConstantPool.INT_ZERO);
    }

    /* ******************************************** tryDo ******************************************** */
    /* -------------------------------------------- Runnable -------------------------------------------- */
    public static void tryDo(ThrowThrowableRunnable runnable, Consumer<Throwable> throwableConsumer) {
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
    public static <T> void tryDo(T t, ThrowThrowableConsumer<T> consumer, Consumer<Throwable> throwableConsumer) {
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
    public static <V> V tryReturnNew(ThrowThrowableCallable<V> callable, V defaultValue) {
        return tryReturnNew(callable, defaultValueSupplier(defaultValue));
    }

    public static <V> Optional<V> tryReturnNewOpt(ThrowThrowableCallable<V> callable, V defaultValue) {
        return Optional.ofNullable(tryReturnNew(callable, defaultValue));
    }

    public static <V> V tryReturnNew(ThrowThrowableCallable<V> callable, Supplier<V> defaultValueSupplier) {
        return tryReturnNew(callable, throwableToNewValueMapper(defaultValueSupplier));
    }

    public static <V> Optional<V> tryReturnNewOpt(ThrowThrowableCallable<V> callable, Supplier<V> defaultValueSupplier) {
        return Optional.ofNullable(tryReturnNew(callable, defaultValueSupplier));
    }

    public static <V> V tryReturnNew(ThrowThrowableCallable<V> callable, Function<Throwable, V> throwableMapper) {
        try {
            return callable.call();
        } catch (Throwable throwable) {
            return throwableMapper.apply(throwable);
        }
    }

    public static <V> Optional<V> tryReturnNewOpt(ThrowThrowableCallable<V> callable, Function<Throwable, V> throwableMapper) {
        return Optional.ofNullable(tryReturnNew(callable, throwableMapper));
    }

    /* -------------------------------------------- Function -------------------------------------------- */
    public static <T, R> R tryReturnNew(T t, ThrowThrowableFunction<T, R> function, R defaultValue) {
        return tryReturnNew(t, function, defaultValueSupplier(defaultValue));
    }

    public static <T, R> Optional<R> tryReturnNewOpt(T t, ThrowThrowableFunction<T, R> function, R defaultValue) {
        return Optional.ofNullable(tryReturnNew(t, function, defaultValue));
    }

    public static <T, R> R tryReturnNew(T t, ThrowThrowableFunction<T, R> function, Supplier<R> defaultValueSupplier) {
        return tryReturnNew(t, function, throwableToNewValueMapper(defaultValueSupplier));
    }

    public static <T, R> Optional<R> tryReturnNewOpt(T t, ThrowThrowableFunction<T, R> function, Supplier<R> defaultValueSupplier) {
        return Optional.ofNullable(tryReturnNew(t, function, defaultValueSupplier));
    }

    public static <T, R> R tryReturnNew(T t, ThrowThrowableFunction<T, R> function, Function<Throwable, R> throwableMapper) {
        try {
            return function.apply(t);
        } catch (Throwable throwable) {
            return throwableMapper.apply(throwable);
        }
    }

    public static <T, R> Optional<R> tryReturnNewOpt(T t, ThrowThrowableFunction<T, R> function, Function<Throwable, R> throwableMapper) {
        return Optional.ofNullable(tryReturnNew(t, function, throwableMapper));
    }

    /* -------------------------------------------- BiFunction -------------------------------------------- */
    public static <T, U, R> R tryReturnNew(T t, U u, ThrowThrowableBiFunction<T, U, R> function, R defaultValue) {
        return tryReturnNew(t, u, function, defaultValueSupplier(defaultValue));
    }

    public static <T, U, R> Optional<R> tryReturnNewOpt(T t, U u, ThrowThrowableBiFunction<T, U, R> function, R defaultValue) {
        return Optional.ofNullable(tryReturnNew(t, u, function, defaultValue));
    }

    public static <T, U, R> R tryReturnNew(T t, U u, ThrowThrowableBiFunction<T, U, R> function, Supplier<R> defaultValueSupplier) {
        return tryReturnNew(t, u, function, throwableToNewValueMapper(defaultValueSupplier));
    }

    public static <T, U, R> Optional<R> tryReturnNewOpt(T t, U u, ThrowThrowableBiFunction<T, U, R> function, Supplier<R> defaultValueSupplier) {
        return Optional.ofNullable(tryReturnNew(t, u, function, defaultValueSupplier));
    }

    public static <T, U, R> R tryReturnNew(T t, U u, ThrowThrowableBiFunction<T, U, R> function, Function<Throwable, R> throwableMapper) {
        try {
            return function.apply(t, u);
        } catch (Throwable throwable) {
            return throwableMapper.apply(throwable);
        }
    }

    public static <T, U, R> Optional<R> tryReturnNewOpt(T t, U u, ThrowThrowableBiFunction<T, U, R> function, Function<Throwable, R> throwableMapper) {
        return Optional.ofNullable(tryReturnNew(t, u, function, throwableMapper));
    }

    /* -------------------------------------------- Supplier -------------------------------------------- */
    public static <T> T tryReturnNew(ThrowThrowableSupplier<T> supplier, T defaultValue) {
        return tryReturnNew(supplier, defaultValueSupplier(defaultValue));
    }

    public static <T> Optional<T> tryReturnNewOpt(ThrowThrowableSupplier<T> supplier, T defaultValue) {
        return Optional.ofNullable(tryReturnNew(supplier, defaultValue));
    }

    public static <T> T tryReturnNew(ThrowThrowableSupplier<T> supplier, Supplier<T> defaultValueSupplier) {
        return tryReturnNew(supplier, throwableToNewValueMapper(defaultValueSupplier));
    }

    public static <T> Optional<T> tryReturnNewOpt(ThrowThrowableSupplier<T> supplier, Supplier<T> defaultValueSupplier) {
        return Optional.ofNullable(tryReturnNew(supplier, defaultValueSupplier));
    }

    public static <T> T tryReturnNew(ThrowThrowableSupplier<T> supplier, Function<Throwable, T> throwableMapper) {
        try {
            return supplier.get();
        } catch (Throwable throwable) {
            return throwableMapper.apply(throwable);
        }
    }

    public static <T> Optional<T> tryReturnNewOpt(ThrowThrowableSupplier<T> supplier, Function<Throwable, T> throwableMapper) {
        return Optional.ofNullable(tryReturnNew(supplier, throwableMapper));
    }

    /* -------------------------------------------- Consumer -------------------------------------------- */
    // no implement

    /* -------------------------------------------- Predicate -------------------------------------------- */
    public static <T> boolean tryReturnNew(T t, ThrowThrowablePredicate<T> predicate, boolean defaultValue) {
        return tryReturnNew(t, predicate, () -> defaultValue);
    }

    public static <T> boolean tryReturnNew(T t, ThrowThrowablePredicate<T> predicate, BooleanSupplier defaultValueSupplier) {
        return tryReturnNew(t, predicate, throwable -> defaultValueSupplier.getAsBoolean());
    }

    public static <T> boolean tryReturnNew(T t, ThrowThrowablePredicate<T> predicate, ToBoolFunction<Throwable> throwableMapper) {
        try {
            return predicate.test(t);
        } catch (Throwable throwable) {
            return throwableMapper.applyAsBool(throwable);
        }
    }

    /* -------------------------------------------- Comparator -------------------------------------------- */
    public static <T> int tryReturnNew(T t1, T t2, ThrowThrowableComparator<T> comparator, int defaultValue) {
        return tryReturnNew(t1, t2, comparator, () -> defaultValue);
    }

    public static <T> int tryReturnNew(T t1, T t2, ThrowThrowableComparator<T> comparator, IntSupplier defaultValueSupplier) {
        return tryReturnNew(t1, t2, comparator, throwable -> defaultValueSupplier.getAsInt());
    }

    public static <T> int tryReturnNew(T t1, T t2, ThrowThrowableComparator<T> comparator, ToIntFunction<Throwable> throwableMapper) {
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
    public static <T extends Throwable> void tryThrowNew(ThrowThrowableRunnable runnable, T throwable) {
        tryThrowNew(runnable, defaultThrowableSupplier(throwable));
    }

    public static <T extends Throwable> void tryThrowNew(ThrowThrowableRunnable runnable, Supplier<? extends T> throwableSupplier) {
        tryThrowNew(runnable, throwableToNewMapper(throwableSupplier));
    }

    public static <T extends Throwable> void tryThrowNew(ThrowThrowableRunnable runnable, Function<Throwable, ? extends T> throwableMapper) {
        try {
            runnable.run();
        } catch (Throwable throwable) {
            T t = throwableMapper.apply(throwable);
            throw throwUnchecked(t);
        }
    }

    public static <T extends Throwable> void tryThrowNew(ThrowThrowableRunnable runnable, BiFunction<CharSequence, Throwable, ? extends T> throwableMapper) {
        tryThrowNew(runnable, throwableToNewMapper(throwableMapper));
    }

    public static <T extends Throwable> void tryThrowNew(ThrowThrowableRunnable runnable, BiFunction<CharSequence, Throwable, ? extends T> throwableMapper, CharSequence message) {
        tryThrowNew(runnable, throwableToNewMapper(throwableMapper, message));
    }

    /* -------------------------------------------- Callable -------------------------------------------- */
    public static <V, T extends Throwable> void tryThrowNew(ThrowThrowableCallable<V> callable, T throwable) {
        tryThrowNew(callable, (Supplier<Throwable>) () -> throwable);
    }

    public static <V, T extends Throwable> V tryThrowNew(ThrowThrowableCallable<V> callable, Supplier<? extends T> throwableSupplier) {
        return tryThrowNew(callable, (Function<Throwable, Throwable>) ignored -> throwableSupplier.get());
    }

    public static <V, T extends Throwable> V tryThrowNew(ThrowThrowableCallable<V> callable, Function<Throwable, ? extends T> throwableMapper) {
        try {
            return callable.call();
        } catch (Throwable throwable) {
            T t = throwableMapper.apply(throwable);
            throw throwUnchecked(t);
        }
    }

    public static <V, T extends Throwable> V tryThrowNew(ThrowThrowableCallable<V> callable, BiFunction<CharSequence, Throwable, ? extends T> throwableMapper) {
        return tryThrowNew(callable, throwableToNewMapper(throwableMapper));
    }

    public static <V, T extends Throwable> V tryThrowNew(ThrowThrowableCallable<V> callable, BiFunction<CharSequence, Throwable, ? extends T> throwableMapper, CharSequence message) {
        return tryThrowNew(callable, throwableToNewMapper(throwableMapper, message));
    }

    protected ThrowableUtil() {
        throw new UnsupportedOperationException();
    }
}
