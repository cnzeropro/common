package org.zero.common.core.extension.java.util.function;

import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.util.function.BiFunction;

/**
 * 允许抛出 {@link Throwable} 的 {@link BiFunction} 变体。
 * <p>
 * 可通过 {@link #to()} 适配为标准 {@link BiFunction}。适配后的函数不会吞掉异常，
 * 而是通过 sneaky throw 继续向调用方传播。
 *
 * @author Zero (cnzeropro@163.com)
 * @see BiFunction
 * @see ThrowableUtil
 * @since 2025/5/8
 */
@FunctionalInterface
public interface ThrowableBiFunction<T, U, R> {
	/**
	 * 将标准 {@link BiFunction} 包装为可抛异常函数。
	 *
	 * @param function 标准二元函数
	 * @param <T>      第一个输入类型
	 * @param <U>      第二个输入类型
	 * @param <R>      返回类型
	 * @return 可抛异常函数
	 */
    static <T, U, R> ThrowableBiFunction<T, U, R> of(BiFunction<T, U, R> function) {
        return function::apply;
	}

	/**
	 * 对给定的两个参数执行函数，允许抛出任意 {@link Throwable}。
	 *
	 * @param t 第一个输入参数
	 * @param u 第二个输入参数
	 * @return 函数结果
	 * @throws Throwable 执行过程中产生的异常或错误
	 */
    R apply(T t, U u) throws Throwable;

	/**
	 * 适配为标准 {@link BiFunction}。
	 * <p>
	 * {@link #apply(Object, Object)} 抛出的异常会通过 {@link ThrowableUtil#throwUnchecked(Throwable)}
	 * 继续传播。
	 *
	 * @return 标准二元函数
     */
    default BiFunction<T, U, R> to() {
        return (t, u) -> {
            try {
                return this.apply(t, u);
            } catch (Throwable throwable) {
                throw ThrowableUtil.throwUnchecked(throwable);
            }
        };
    }
}
