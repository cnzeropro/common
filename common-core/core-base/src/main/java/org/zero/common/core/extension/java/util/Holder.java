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
 * 可变的单值持有器。
 * <p>
 * 该类型用于需要延迟写入、回调中保存结果或临时替换引用的场景。{@code null} 表示空值，
 * 因此 {@link #set(Object)} 传入 {@code null} 等价于 {@link #clear()}，不能区分“显式保存 null”
 * 与“没有值”。
 * <p>
 * {@code Holder} 是可变对象，默认保持对象身份语义，未按内部值重写 {@link #equals(Object)}
 * 与 {@link #hashCode()}。不建议将其作为 {@link java.util.Map} 的 key 或集合去重元素。
 * 该类型本身不提供线程安全保证，多线程共享时应由调用方自行同步。
 *
 * @param <T> 值类型
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
public final class Holder<T> {
	private T value;

	private Holder(T value) {
		this.value = value;
	}

	/**
	 * 创建空 {@code Holder}。
	 * <p>
	 * 每次调用都会返回新实例，避免可变空对象之间共享状态。
	 *
	 * @param <T> 值类型
	 * @return 新的空 {@code Holder}
	 */
	public static <T> Holder<T> empty() {
		return new Holder<>(null);
	}

	/**
	 * 使用可空值创建 {@code Holder}。
	 *
	 * @param value 可空值
	 * @param <T>   值类型
	 * @return 新的 {@code Holder}
	 */
	public static <T> Holder<T> of(T value) {
		return new Holder<>(value);
	}

	/**
	 * 从 {@link Optional} 创建 {@code Holder}。
	 *
	 * @param optional 可空 {@link Optional}
	 * @param <T>      值类型
	 * @return {@code optional} 为空或不包含值时返回空 {@code Holder}
	 */
	public static <T> Holder<T> from(Optional<? extends T> optional) {
		return Objects.isNull(optional) || !optional.isPresent() ? empty() : of(optional.get());
	}

	/**
	 * 从 {@link Option} 创建 {@code Holder}。
	 *
	 * @param option 可空 {@link Option}
	 * @param <T>    值类型
	 * @return {@code option} 为空或不包含值时返回空 {@code Holder}
	 */
	public static <T> Holder<T> from(Option<? extends T> option) {
		return Objects.isNull(option) || option.isEmpty() ? empty() : of(option.get());
	}

	/**
	 * 通过供应器创建 {@code Holder}。
	 *
	 * @param supplier 值供应器，供应结果允许为 {@code null}
	 * @param <T>      值类型
	 * @return 新的 {@code Holder}
	 * @throws NullPointerException 当 {@code supplier} 为 {@code null} 时抛出
	 */
	public static <T> Holder<T> from(Supplier<? extends T> supplier) {
		Objects.requireNonNull(supplier, "supplier");
		return of(supplier.get());
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
	 * 设置当前值。
	 * <p>
	 * 传入 {@code null} 会清空当前持有器。
	 *
	 * @param value 新值，允许为 {@code null}
	 * @return 当前 {@code Holder}
	 */
	public Holder<T> set(T value) {
		this.value = value;
		return this;
	}

	/**
	 * 仅在当前对象为空时设置值。
	 *
	 * @param value 新值，允许为 {@code null}
	 * @return 当前 {@code Holder}
	 */
	public Holder<T> setIfEmpty(T value) {
		if (isEmpty()) {
			this.value = value;
		}
		return this;
	}

	/**
	 * 仅在当前对象为空时通过供应器设置值。
	 *
	 * @param supplier 值供应器，供应结果允许为 {@code null}
	 * @return 当前 {@code Holder}
	 * @throws NullPointerException 当需要取值且 {@code supplier} 为 {@code null} 时抛出
	 */
	public Holder<T> setIfEmpty(Supplier<? extends T> supplier) {
		if (isEmpty()) {
			this.value = Objects.requireNonNull(supplier, "supplier").get();
		}
		return this;
	}

	/**
	 * 清空当前值。
	 *
	 * @return 当前 {@code Holder}
	 */
	public Holder<T> clear() {
		this.value = null;
		return this;
	}

	/**
	 * 获取当前值。
	 *
	 * @return 当前值或 {@code null}
	 */
	public T get() {
		return value;
	}

	/**
	 * 设置新值并返回旧值。
	 *
	 * @param value 新值，允许为 {@code null}
	 * @return 旧值或 {@code null}
	 */
	public T getAndSet(T value) {
		T previousValue = this.value;
		this.value = value;
		return previousValue;
	}

	/**
	 * 清空当前值并返回旧值。
	 *
	 * @return 旧值或 {@code null}
	 */
	public T getAndClear() {
		T previousValue = this.value;
		this.value = null;
		return previousValue;
	}

	/**
	 * 使用当前值计算新值。
	 * <p>
	 * 当前为空时会向函数传入 {@code null}，函数返回 {@code null} 会清空当前持有器。
	 *
	 * @param updater 更新函数
	 * @return 当前 {@code Holder}
	 * @throws NullPointerException 当 {@code updater} 为 {@code null} 时抛出
	 */
	public Holder<T> update(Function<? super T, ? extends T> updater) {
		this.value = Objects.requireNonNull(updater, "updater").apply(value);
		return this;
	}

	/**
	 * 仅在当前对象有值时使用当前值计算新值。
	 *
	 * @param updater 更新函数，返回 {@code null} 会清空当前持有器
	 * @return 当前 {@code Holder}
	 * @throws NullPointerException 当 {@code updater} 为 {@code null} 时抛出
	 */
	public Holder<T> updateIfPresent(Function<? super T, ? extends T> updater) {
		Objects.requireNonNull(updater, "updater");
		if (isPresent()) {
			this.value = updater.apply(value);
		}
		return this;
	}

	/**
	 * 当前对象有值且不满足条件时清空。
	 *
	 * @param predicate 保留条件
	 * @return 当前 {@code Holder}
	 * @throws NullPointerException 当 {@code predicate} 为 {@code null} 时抛出
	 */
	public Holder<T> retainIf(Predicate<? super T> predicate) {
		Objects.requireNonNull(predicate, "predicate");
		if (isPresent() && !predicate.test(value)) {
			clear();
		}
		return this;
	}

	/**
	 * 当前对象有值且满足条件时清空。
	 *
	 * @param predicate 移除条件
	 * @return 当前 {@code Holder}
	 * @throws NullPointerException 当 {@code predicate} 为 {@code null} 时抛出
	 */
	public Holder<T> removeIf(Predicate<? super T> predicate) {
		Objects.requireNonNull(predicate, "predicate");
		if (isPresent() && predicate.test(value)) {
			clear();
		}
		return this;
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
	 *
	 * @param action 旁路动作
	 * @return 当前 {@code Holder}
	 * @throws NullPointerException 当 {@code action} 为 {@code null} 时抛出
	 */
	public Holder<T> peek(Consumer<? super T> action) {
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
	 * @return 当前 {@code Holder}
	 * @throws NullPointerException 当 {@code actions} 为 {@code null} 或包含 {@code null} 动作时抛出
	 */
	@SafeVarargs
	public final Holder<T> peekAll(Consumer<? super T>... actions) {
		Objects.requireNonNull(actions, "actions");
		for (Consumer<? super T> action : actions) {
			peek(action);
		}
		return this;
	}

	/**
	 * 将当前值映射为不可变 {@link Option}。
	 *
	 * @param mapper 映射函数
	 * @param <U>    映射后的值类型
	 * @return 映射结果对应的 {@link Option}
	 */
	public <U> Option<U> map(Function<? super T, ? extends U> mapper) {
		return option().map(mapper);
	}

	/**
	 * 将当前值扁平映射为不可变 {@link Option}。
	 *
	 * @param mapper 映射函数
	 * @param <U>    映射后的值类型
	 * @return 映射函数返回的 {@link Option}
	 */
	public <U> Option<U> flatMap(
			Function<? super T, ? extends Option<? extends U>> mapper) {
		return option().flatMap(mapper);
	}

	/**
	 * 将当前值扁平映射为 JDK {@link Optional} 后再转换为 {@link Option}。
	 *
	 * @param mapper 映射函数
	 * @param <U>    映射后的值类型
	 * @return 映射结果对应的 {@link Option}
	 */
	public <U> Option<U> flatMapOptional(
			Function<? super T, ? extends Optional<? extends U>> mapper) {
		return option().flatMapOptional(mapper);
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
	 * 转换为不可变 {@link Option}。
	 *
	 * @return 当前值对应的 {@link Option}
	 */
	public Option<T> option() {
		return Option.of(value);
	}

	/**
	 * 转换为 {@link Stream}。
	 *
	 * @return 有值时返回单元素流，空时返回空流
	 */
	public Stream<T> stream() {
		return option().stream();
	}

	/**
	 * 在有值时执行返回 {@link Stream} 的映射。
	 *
	 * @param mapper 流映射函数
	 * @param <R>    流元素类型
	 * @return 映射函数返回的流，当前对象为空时返回空流
	 */
	public <R> Stream<R> flatMapStream(
			Function<? super T, ? extends Stream<? extends R>> mapper) {
		return option().flatMapStream(mapper);
	}

	/**
	 * 在有值时执行消费动作。
	 *
	 * @param action 消费动作
	 */
	public void ifPresent(Consumer<? super T> action) {
		option().ifPresent(action);
	}

	/**
	 * 根据当前对象是否有值执行对应动作。
	 *
	 * @param action      有值时执行的动作
	 * @param emptyAction 空时执行的动作
	 */
	public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
		option().ifPresentOrElse(action, emptyAction);
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
	 * 获取当前值，空时抛出 {@link NoSuchElementException}。
	 *
	 * @return 当前值
	 * @throws NoSuchElementException 当前对象为空时抛出
	 */
	public T orElseThrow() {
		if (isPresent()) {
			return value;
		}
		throw new NoSuchElementException("No value present");
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
	 * 复制当前持有器。
	 *
	 * @return 包含当前值的新 {@code Holder}
	 */
	public Holder<T> copy() {
		return of(value);
	}

	@Override
	public String toString() {
		return isPresent()
				? String.format("Holder[%s]", value)
				: "Holder.empty";
	}
}
