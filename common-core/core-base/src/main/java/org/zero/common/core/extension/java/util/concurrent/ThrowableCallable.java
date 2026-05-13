package org.zero.common.core.extension.java.util.concurrent;

import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.util.concurrent.Callable;

/**
 * 允许抛出 {@link Throwable} 的 {@link Callable} 变体。
 * <p>
 * 可通过 {@link #to()} 适配为标准 {@link Callable}。适配后的任务不会吞掉异常，
 * 而是通过 sneaky throw 继续向调用方传播。
 *
 * @author Zero (cnzeropro@163.com)
 * @see Callable
 * @see ThrowableUtil
 * @since 2025/5/8
 */
@FunctionalInterface
public interface ThrowableCallable<V> {
	/**
	 * 将标准 {@link Callable} 包装为可抛异常任务。
	 *
	 * @param callable 标准任务
	 * @param <V>      返回类型
	 * @return 可抛异常任务
	 */
    static <V> ThrowableCallable<V> of(Callable<V> callable) {
        return callable::call;
	}

	/**
	 * 执行任务并返回结果，允许抛出任意 {@link Throwable}。
	 *
	 * @return 任务执行结果
	 * @throws Throwable 执行过程中产生的异常或错误
	 */
    V call() throws Throwable;

	/**
	 * 适配为标准 {@link Callable}。
	 * <p>
	 * {@link #call()} 抛出的异常会通过 {@link ThrowableUtil#throwUnchecked(Throwable)}
	 * 继续传播。
	 *
	 * @return 标准任务
	 */
    default Callable<V> to() {
        return () -> {
            try {
                return this.call();
            } catch (Throwable throwable) {
                throw ThrowableUtil.throwUnchecked(throwable);
            }
        };
    }
}
