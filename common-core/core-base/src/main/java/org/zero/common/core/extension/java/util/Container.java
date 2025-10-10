package org.zero.common.core.extension.java.util;

import org.zero.common.data.annotation.NonNull;
import org.zero.common.data.annotation.Null;
import org.zero.common.data.annotation.Throw;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/27
 */
public class Container<T> {
    protected static final Container<?> EMPTY = new Container<>(null);

    @Null
    protected T value;

    @Null
    protected Container(T value) {
        this.value = value;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public static <T> Container<T> empty() {
        return (Container<T>) EMPTY;
    }

    public static <T> @NonNull Container<T> of(@Null T value) {
        return Objects.isNull(value) ? empty() : new Container<>(value);
    }

    @NonNull
    public static <T> Container<T> of(Supplier<@Null T> valueSupplier) {
        T value = valueSupplier.get();
        return of(value);
    }

    @NonNull
    public static <T> Container<T> of(Optional<T> optional) {
        return optional.isPresent() ? of(optional.get()) : empty();
    }

    @NonNull
    public Container<T> filter(Predicate<@NonNull ? super T> predicate) {
        if (isEmpty()) {
            return this;
        }
        return predicate.test(value) ? this : empty();
    }

    @NonNull
    public Container<T> peek(Consumer<@NonNull ? super T> action) {
        if (nonEmpty()) {
            action.accept(value);
        }
        return this;
    }

    @NonNull
    @SafeVarargs
    public final Container<T> peeks(Consumer<@NonNull ? super T>... actions) {
        // reduce 方法第三个参数其实并不会执行到该函数式接口所以直接返回了个 null
        return Stream.of(actions).reduce(this, Container<T>::peek, (containers, container) -> null);
    }

    @NonNull
    public Container<T> or(Supplier<@NonNull ? extends Container<@Null ? extends T>> supplier) {
        if (isEmpty()) {
            @SuppressWarnings("unchecked")
            Container<T> container = (Container<T>) supplier.get();
            return container;
        }
        return this;
    }

    @NonNull
    public <U> Container<U> map(Function<@NonNull ? super T, @Null ? extends U> mapper) {
        if (isEmpty()) {
            return empty();
        }
        return of(mapper.apply(value));
    }

    @NonNull
    public <U> Container<U> flatMap(Function<@NonNull ? super T, @NonNull ? extends Container<@Null ? extends U>> mapper) {
        if (isEmpty()) {
            return empty();
        }
        @SuppressWarnings("unchecked")
        Container<U> container = (Container<U>) mapper.apply(value);
        return container;
    }

    @NonNull
    public <U> Container<U> flattedMap(Function<@NonNull ? super T, Optional<@Null ? extends U>> mapper) {
        if (isEmpty()) {
            return empty();
        }
        Optional<? extends U> optional = mapper.apply(value);
        @SuppressWarnings("unchecked")
        Container<U> container = (Container<U>) of(optional);
        return container;
    }

    public boolean isEmpty() {
        return Objects.isNull(value);
    }

    public boolean nonEmpty() {
        return !isEmpty();
    }

    public Optional<T> optional() {
        return Optional.ofNullable(value);
    }

    @NonNull
    public Stream<T> stream() {
        if (isEmpty()) {
            return Stream.empty();
        }
        return Stream.of(value);
    }

    @NonNull
    public <R> Stream<R> flatStream(Function<@NonNull ? super T, @NonNull Stream<@Null ? extends R>> mapper) {
        if (isEmpty()) {
            return Stream.empty();
        }
        @SuppressWarnings("unchecked")
        Stream<R> stream = (Stream<R>) mapper.apply(value);
        return stream;
    }

    public void ifPresent(@NonNull Consumer<@NonNull ? super T> consumer) {
        if (nonEmpty()) {
            consumer.accept(value);
        }
    }

    public void ifPresentOrElse(@NonNull Consumer<@NonNull ? super T> action, @NonNull Runnable emptyAction) {
        if (nonEmpty()) {
            action.accept(value);
        } else {
            emptyAction.run();
        }
    }

    public @NonNull Container<T> set(@Null T value) {
        if (Objects.isNull(value)) {
            return empty();
        }
        this.value = value;
        return this;
    }

    public @Null T get() {
        return value;
    }

    @Null
    public T getOrElse(T other) {
        return nonEmpty() ? value : other;
    }

    public @Null T getOrElse(@NonNull Supplier<? extends T> otherSupplier) {
        return nonEmpty() ? value : otherSupplier.get();
    }

    @Throw(NoSuchElementException.class)
    @NonNull
    public T getOrThrow() {
        if (nonEmpty()) {
            return value;
        }
        throw new NoSuchElementException("No value present");
    }

    @Throw
    @NonNull
    public <X extends Throwable> T getOrThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (nonEmpty()) {
            return value;
        }
        throw exceptionSupplier.get();
    }

    @Throw
    @NonNull
    public <X extends Throwable> T getOrThrow(Function<String, ? extends X> exceptionMapper, String message) throws X {
        if (nonEmpty()) {
            return value;
        }
        throw exceptionMapper.apply(message);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Container)) {
            return false;
        }
        Container<?> other = (Container<?>) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return nonEmpty()
                ? String.format("Container[%s]", value)
                : "Container.empty";
    }
}
