package org.zero.common.core.extension.java.util.function;

import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.util.function.Consumer;

/**
 * 允许抛出 {@link Throwable} 的 {@link Consumer} 变体。
 * <p>
 * 可通过 {@link #to()} 适配为标准 {@link Consumer}。适配后的函数不会吞掉异常，
 * 而是通过 sneaky throw 继续向调用方传播。
 *
 * @author Zero (cnzeropro@163.com)
 * @see Consumer
 * @see ThrowableUtil
 * @since 2025/5/8
 */
@FunctionalInterface
public interface ThrowableConsumer<T> {
	/**
	 * 将标准 {@link Consumer} 包装为可抛异常消费函数。
	 *
	 * @param consumer 标准消费函数
	 * @param <T>      输入类型
	 * @return 可抛异常消费函数
	 */
    static <T> ThrowableConsumer<T> of(Consumer<T> consumer) {
        return consumer::accept;
	}

	/**
	 * 对给定参数执行消费操作，允许抛出任意 {@link Throwable}。
	 *
	 * @param t 输入参数
	 * @throws Throwable 执行过程中产生的异常或错误
	 */
    void accept(T t) throws Throwable;

	/**
	 * 适配为标准 {@link Consumer}。
	 * <p>
	 * {@link #accept(Object)} 抛出的异常会通过 {@link ThrowableUtil#throwUnchecked(Throwable)}
	 * 继续传播。
	 *
	 * @return 标准消费函数
	 */
    default Consumer<T> to() {
        return t -> {
            try {
                this.accept(t);
            } catch (Throwable throwable) {
                throw ThrowableUtil.throwUnchecked(throwable);
            }
        };
    }
}
