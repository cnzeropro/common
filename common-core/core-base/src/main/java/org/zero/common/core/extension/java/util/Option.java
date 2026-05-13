package org.zero.common.core.extension.java.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 不可变的单值可选对象。
 * <p>
 * 该类型用于补充 Java 8 {@link Optional} 缺少的常用链式能力，例如 {@link #or(Supplier)}、
 * {@link #ifPresentOrElse(Consumer, Runnable)} 与 {@link #stream()}。与 {@link Optional} 一样，
 * {@code null} 仅表示空值，不会作为有效值保存；如需保存可变引用，请使用 {@link Holder}。
 * <p>
 * {@link #of(Object)} 用于接受可空值，并将 {@code null} 转换为 {@link #empty()}。
 *
 * @param <T> 值类型
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
public final class Option<T> {
	private static final Option<?> EMPTY = new Option<>(null);

	private final T value;

	private Option(T value) {
		this.value = value;
	}

	/**
	 * 返回空 {@code Option}。
	 * <p>
	 * 空实例是共享单例；由于 {@code Option} 不可变，共享实例不会产生状态污染。
	 *
	 * @param <T> 值类型
	 * @return 空 {@code Option}
	 */
	@SuppressWarnings("unchecked")
	public static <T> Option<T> empty() {
		return (Option<T>) EMPTY;
	}

	/**
	 * 使用可空值创建 {@code Option}。
	 *
	 * @param value 可空值
	 * @param <T>   值类型
	 * @return {@code value} 非空时返回有值 {@code Option}，否则返回空 {@code Option}
	 */
	public static <T> Option<T> of(T value) {
		return Objects.isNull(value) ? empty() : new Option<>(value);
	}

	/**
	 * 从 JDK {@link Optional} 创建 {@code Option}。
	 *
	 * @param optional 可空 {@link Optional}
	 * @param <T>      值类型
	 * @return {@code optional} 为空或不包含值时返回空 {@code Option}
	 */
	public static <T> Option<T> from(Optional<? extends T> optional) {
		return Objects.isNull(optional) || !optional.isPresent() ? empty() : of(optional.get());
	}

	/**
	 * 从 {@link Holder} 创建 {@code Option}。
	 * <p>
	 * 创建结果是当前 {@code Holder} 值的不可变快照，后续修改 {@code Holder} 不会影响已创建的 {@code Option}。
	 *
	 * @param holder 可空 {@link Holder}
	 * @param <T>    值类型
	 * @return {@code holder} 为空或不包含值时返回空 {@code Option}
	 */
	public static <T> Option<T> from(Holder<? extends T> holder) {
		return Objects.isNull(holder) || holder.isEmpty() ? empty() : of(holder.get());
	}

	/**
	 * 通过供应器延迟创建 {@code Option}。
	 *
	 * @param supplier 值供应器，供应结果允许为 {@code null}
	 * @param <T>      值类型
	 * @return 供应结果对应的 {@code Option}
	 * @throws NullPointerException 当 {@code supplier} 为 {@code null} 时抛出
	 */
	public static <T> Option<T> from(Supplier<? extends T> supplier) {
		Objects.requireNonNull(supplier, "supplier");
		return of(supplier.get());
	}

	@SuppressWarnings("unchecked")
	private static <T> Option<T> narrow(Option<? extends T> option) {
		return (Option<T>) option;
	}

	/**
	 * 判断当前对象是否为空。
	 *
	 * @return 当前对象为空时返回 {@code true}
	 */
	public boolean isEmpty() {
		return Objects.isNull(value);
	}

	/**
	 * 判断当前对象是否包含值。
	 *
	 * @return 当前对象包含值时返回 {@code true}
	 */
	public boolean isPresent() {
		return !isEmpty();
	}

	/**
	 * 在有值时按条件过滤。
	 *
	 * @param predicate 过滤条件
	 * @return 空对象或满足条件的当前对象；不满足条件时返回空 {@code Option}
	 * @throws NullPointerException 当 {@code predicate} 为 {@code null} 时抛出
	 */
	public Option<T> filter(Predicate<? super T> predicate) {
		Objects.requireNonNull(predicate, "predicate");
		if (isEmpty()) {
			return this;
		}
		return predicate.test(value) ? this : empty();
	}

	/**
	 * 在有值时按条件反向过滤。
	 *
	 * @param predicate 过滤条件
	 * @return 空对象或不满足条件的当前对象；满足条件时返回空 {@code Option}
	 * @throws NullPointerException 当 {@code predicate} 为 {@code null} 时抛出
	 */
	public Option<T> filterNot(Predicate<? super T> predicate) {
		Objects.requireNonNull(predicate, "predicate");
		return filter(value -> !predicate.test(value));
	}

	/**
	 * 判断当前对象有值且当前值满足指定条件。
	 *
	 * @param predicate 判断条件
	 * @return 有值且满足条件时返回 {@code true}
	 * @throws NullPointerException 当 {@code predicate} 为 {@code null} 时抛出
	 */
	public boolean isPresentAnd(Predicate<? super T> predicate) {
		Objects.requireNonNull(predicate, "predicate");
		return isPresent() && predicate.test(value);
	}

	/**
	 * 判断当前对象为空，或当前值满足指定条件。
	 *
	 * @param predicate 判断条件
	 * @return 为空或当前值满足条件时返回 {@code true}
	 * @throws NullPointerException 当 {@code predicate} 为 {@code null} 时抛出
	 */
	public boolean isEmptyOr(Predicate<? super T> predicate) {
		Objects.requireNonNull(predicate, "predicate");
		return isEmpty() || predicate.test(value);
	}

	/**
	 * 在有值时执行旁路动作。
	 * <p>
	 * 该方法不会改变当前对象，适合用于日志、调试或统计。
	 *
	 * @param action 旁路动作
	 * @return 当前 {@code Option}
	 * @throws NullPointerException 当 {@code action} 为 {@code null} 时抛出
	 */
	public Option<T> peek(Consumer<? super T> action) {
		Objects.requireNonNull(action, "action");
		if (isPresent()) {
			action.accept(value);
		}
		return this;
	}

	/**
	 * 在有值时依次执行多个旁路动作。
	 *
	 * @param actions 旁路动作列表
	 * @return 当前 {@code Option}
	 * @throws NullPointerException 当 {@code actions} 为 {@code null} 或包含 {@code null} 动作时抛出
	 */
	@SafeVarargs
	public final Option<T> peekAll(Consumer<? super T>... actions) {
		Objects.requireNonNull(actions, "actions");
		for (Consumer<? super T> action : actions) {
			peek(action);
		}
		return this;
	}

	/**
	 * 当前对象为空时延迟返回另一个 {@code Option}。
	 *
	 * @param supplier 备选 {@code Option} 供应器
	 * @return 当前对象有值时返回当前对象，否则返回供应器提供的 {@code Option}
	 * @throws NullPointerException 当 {@code supplier} 或供应结果为 {@code null} 时抛出
	 */
	public Option<T> or(Supplier<? extends Option<? extends T>> supplier) {
		Objects.requireNonNull(supplier, "supplier");
		if (isPresent()) {
			return this;
		}
		return narrow(Objects.requireNonNull(supplier.get(), "supplier.get()"));
	}

	/**
	 * 在有值时执行映射。
	 * <p>
	 * 映射结果允许为 {@code null}，此时返回空 {@code Option}。
	 *
	 * @param mapper 映射函数
	 * @param <U>    映射后的值类型
	 * @return 映射结果对应的 {@code Option}
	 * @throws NullPointerException 当 {@code mapper} 为 {@code null} 时抛出
	 */
	public <U> Option<U> map(Function<? super T, ? extends U> mapper) {
		Objects.requireNonNull(mapper, "mapper");
		if (isEmpty()) {
			return empty();
		}
		return of(mapper.apply(value));
	}

	/**
	 * 在有值时执行返回 {@code Option} 的映射。
	 *
	 * @param mapper 映射函数
	 * @param <U>    映射后的值类型
	 * @return 映射函数返回的 {@code Option}，当前对象为空时返回空 {@code Option}
	 * @throws NullPointerException 当 {@code mapper} 或映射结果为 {@code null} 时抛出
	 */
	public <U> Option<U> flatMap(
			Function<? super T, ? extends Option<? extends U>> mapper) {
		Objects.requireNonNull(mapper, "mapper");
		if (isEmpty()) {
			return empty();
		}
		return narrow(Objects.requireNonNull(mapper.apply(value), "mapper.apply(value)"));
	}

	/**
	 * 在有值时执行返回 JDK {@link Optional} 的映射。
	 *
	 * @param mapper 映射函数
	 * @param <U>    映射后的值类型
	 * @return 映射结果转换得到的 {@code Option}
	 * @throws NullPointerException 当 {@code mapper} 或映射结果为 {@code null} 时抛出
	 */
	public <U> Option<U> flatMapOptional(
			Function<? super T, ? extends Optional<? extends U>> mapper) {
		Objects.requireNonNull(mapper, "mapper");
		if (isEmpty()) {
			return empty();
		}
		return from(Objects.requireNonNull(mapper.apply(value), "mapper.apply(value)"));
	}

	/**
	 * 转换为 JDK {@link Optional}。
	 *
	 * @return 等价的 {@link Optional}
	 */
	public Optional<T> optional() {
		return Optional.ofNullable(value);
	}

	/**
	 * 转换为 {@link Stream}。
	 *
	 * @return 有值时返回单元素流，空时返回空流
	 */
	public Stream<T> stream() {
		if (isEmpty()) {
			return Stream.empty();
		}
		return Stream.of(value);
	}

	/**
	 * 在有值时执行返回 {@link Stream} 的映射。
	 *
	 * @param mapper 流映射函数
	 * @param <R>    流元素类型
	 * @return 映射函数返回的流，当前对象为空时返回空流
	 * @throws NullPointerException 当 {@code mapper} 或映射结果为 {@code null} 时抛出
	 */
	@SuppressWarnings("unchecked")
	public <R> Stream<R> flatMapStream(
			Function<? super T, ? extends Stream<? extends R>> mapper) {
		Objects.requireNonNull(mapper, "mapper");
		if (isEmpty()) {
			return Stream.empty();
		}
		return (Stream<R>) Objects.requireNonNull(mapper.apply(value), "mapper.apply(value)");
	}

	/**
	 * 在有值时执行消费动作。
	 *
	 * @param action 消费动作
	 * @throws NullPointerException 当 {@code action} 为 {@code null} 时抛出
	 */
	public void ifPresent(Consumer<? super T> action) {
		Objects.requireNonNull(action, "action");
		if (isPresent()) {
			action.accept(value);
		}
	}

	/**
	 * 根据当前对象是否有值执行对应动作。
	 *
	 * @param action      有值时执行的动作
	 * @param emptyAction 空时执行的动作
	 * @throws NullPointerException 当任一动作参数为 {@code null} 时抛出
	 */
	public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
		Objects.requireNonNull(action, "action");
		Objects.requireNonNull(emptyAction, "emptyAction");
		if (isPresent()) {
			action.accept(value);
		} else {
			emptyAction.run();
		}
	}

	/**
	 * 判断当前对象是否包含指定值。
	 *
	 * @param target 待比较值
	 * @return 当前对象有值且值相等时返回 {@code true}
	 */
	public boolean contains(Object target) {
		return isPresent() && Objects.equals(value, target);
	}

	/**
	 * 获取当前值。
	 *
	 * @return 当前值
	 * @throws NoSuchElementException 当前对象为空时抛出
	 */
	public T get() {
		if (isPresent()) {
			return value;
		}
		throw new NoSuchElementException("No value present");
	}

	/**
	 * 获取当前值，空时返回 {@code null}。
	 *
	 * @return 当前值或 {@code null}
	 */
	public T getOrNull() {
		return value;
	}

	/**
	 * 获取当前值，空时返回备选值。
	 *
	 * @param other 备选值
	 * @return 当前值或备选值
	 */
	public T orElse(T other) {
		return isPresent() ? value : other;
	}

	/**
	 * 获取当前值，空时延迟返回备选值。
	 *
	 * @param supplier 备选值供应器
	 * @return 当前值或供应器提供的备选值
	 * @throws NullPointerException 当 {@code supplier} 为 {@code null} 时抛出
	 */
	public T orElseGet(Supplier<? extends T> supplier) {
		if (isPresent()) {
			return value;
		}
		return Objects.requireNonNull(supplier, "supplier").get();
	}

	/**
	 * 获取当前值，空时抛出供应器提供的异常。
	 *
	 * @param exceptionSupplier 异常供应器
	 * @param <X>               异常类型
	 * @return 当前值
	 * @throws X                    当前对象为空时抛出
	 * @throws NullPointerException 当 {@code exceptionSupplier} 为 {@code null} 时抛出
	 */
	public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		if (isPresent()) {
			return value;
		}
		throw Objects.requireNonNull(exceptionSupplier, "exceptionSupplier").get();
	}

	/**
	 * 获取当前值，空时使用消息创建并抛出异常。
	 *
	 * @param exceptionMapper 异常映射函数
	 * @param message         异常消息
	 * @param <X>             异常类型
	 * @return 当前值
	 * @throws X                    当前对象为空时抛出
	 * @throws NullPointerException 当 {@code exceptionMapper} 为 {@code null} 时抛出
	 */
	public <X extends Throwable> T orElseThrow(
			Function<String, ? extends X> exceptionMapper, String message) throws X {
		if (isPresent()) {
			return value;
		}
		throw Objects.requireNonNull(exceptionMapper, "exceptionMapper").apply(message);
	}

	/**
	 * 尝试将当前值转换为指定类型。
	 *
	 * @param type 目标类型
	 * @param <U>  目标值类型
	 * @return 当前值属于目标类型时返回转换后的 {@code Option}，否则返回空 {@code Option}
	 * @throws NullPointerException 当 {@code type} 为 {@code null} 时抛出
	 */
	public <U> Option<U> cast(Class<U> type) {
		Objects.requireNonNull(type, "type");
		if (isEmpty() || !type.isInstance(value)) {
			return empty();
		}
		return of(type.cast(value));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Option)) {
			return false;
		}
		Option<?> other = (Option<?>) obj;
		return Objects.equals(value, other.value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}

	@Override
	public String toString() {
		return isPresent()
				? String.format("Option[%s]", value)
				: "Option.empty";
	}
}
