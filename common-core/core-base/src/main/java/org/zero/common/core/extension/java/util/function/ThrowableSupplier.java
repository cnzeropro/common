package org.zero.common.core.extension.java.util.function;

import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.util.function.Supplier;

/**
 * 允许抛出 {@link Throwable} 的 {@link Supplier} 变体。
 * <p>
 * 可通过 {@link #to()} 适配为标准 {@link Supplier}。适配后的供给函数不会吞掉异常，
 * 而是通过 sneaky throw 继续向调用方传播。
 *
 * @author Zero (cnzeropro@163.com)
 * @see Supplier
 * @see ThrowableUtil
 * @since 2025/5/8
 */
@FunctionalInterface
public interface ThrowableSupplier<T> {
	/**
	 * 将标准 {@link Supplier} 包装为可抛异常供给函数。
	 *
	 * @param supplier 标准供给函数
	 * @param <T>      返回类型
	 * @return 可抛异常供给函数
	 */
    static <T> ThrowableSupplier<T> of(Supplier<T> supplier) {
        return supplier::get;
	}

	/**
	 * 获取结果，允许抛出任意 {@link Throwable}。
	 *
	 * @return 获取到的结果
	 * @throws Throwable 执行过程中产生的异常或错误
	 */
    T get() throws Throwable;

	/**
	 * 适配为标准 {@link Supplier}。
	 * <p>
	 * {@link #get()} 抛出的异常会通过 {@link ThrowableUtil#throwUnchecked(Throwable)}
	 * 继续传播。
	 *
	 * @return 标准供给函数
	 */
    default Supplier<T> to() {
        return () -> {
            try {
                return this.get();
            } catch (Throwable throwable) {
                throw ThrowableUtil.throwUnchecked(throwable);
            }
        };
    }
}
