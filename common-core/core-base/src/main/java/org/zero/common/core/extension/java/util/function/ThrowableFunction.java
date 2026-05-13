package org.zero.common.core.extension.java.util.function;

import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.util.function.Function;

/**
 * 允许抛出 {@link Throwable} 的 {@link Function} 变体。
 * <p>
 * 可通过 {@link #to()} 适配为标准 {@link Function}。适配后的函数不会吞掉异常，
 * 而是通过 sneaky throw 继续向调用方传播。
 *
 * @author Zero (cnzeropro@163.com)
 * @see Function
 * @see ThrowableUtil
 * @since 2025/5/8
 */
@FunctionalInterface
public interface ThrowableFunction<T, R> {
	/**
	 * 将标准 {@link Function} 包装为可抛异常函数。
	 *
	 * @param function 标准函数
	 * @param <T>      输入类型
	 * @param <R>      返回类型
	 * @return 可抛异常函数
	 */
    static <T, R> ThrowableFunction<T, R> of(Function<T, R> function) {
        return function::apply;
	}

	/**
	 * 对给定参数执行函数，允许抛出任意 {@link Throwable}。
	 *
	 * @param t 输入参数
	 * @return 函数结果
	 * @throws Throwable 执行过程中产生的异常或错误
	 */
    R apply(T t) throws Throwable;

	/**
	 * 适配为标准 {@link Function}。
	 * <p>
	 * {@link #apply(Object)} 抛出的异常会通过 {@link ThrowableUtil#throwUnchecked(Throwable)}
	 * 继续传播。
	 *
	 * @return 标准函数
     */
    default Function<T, R> to() {
        return t -> {
            try {
                return this.apply(t);
            } catch (Throwable throwable) {
                throw ThrowableUtil.throwUnchecked(throwable);
            }
        };
    }
}
