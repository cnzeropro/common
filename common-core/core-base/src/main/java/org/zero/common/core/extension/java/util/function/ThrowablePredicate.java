package org.zero.common.core.extension.java.util.function;

import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.util.function.Predicate;

/**
 * 允许抛出 {@link Throwable} 的 {@link Predicate} 变体。
 * <p>
 * 可通过 {@link #to()} 适配为标准 {@link Predicate}。适配后的断言不会吞掉异常，
 * 而是通过 sneaky throw 继续向调用方传播。
 *
 * @author Zero (cnzeropro@163.com)
 * @see Predicate
 * @see ThrowableUtil
 * @since 2025/5/8
 */
@FunctionalInterface
public interface ThrowablePredicate<T> {
	/**
	 * 将标准 {@link Predicate} 包装为可抛异常断言。
	 *
	 * @param predicate 标准断言
	 * @param <T>       输入类型
	 * @return 可抛异常断言
	 */
    static <T> ThrowablePredicate<T> of(Predicate<T> predicate) {
        return predicate::test;
	}

	/**
	 * 对给定参数执行断言判断，允许抛出任意 {@link Throwable}。
	 *
	 * @param t 输入参数
	 * @return 如果输入参数匹配断言则返回 {@code true}，否则返回 {@code false}
	 * @throws Throwable 执行过程中产生的异常或错误
	 */
    boolean test(T t) throws Throwable;

	/**
	 * 适配为标准 {@link Predicate}。
	 * <p>
	 * {@link #test(Object)} 抛出的异常会通过 {@link ThrowableUtil#throwUnchecked(Throwable)}
	 * 继续传播。
	 *
	 * @return 标准断言
	 */
    default Predicate<T> to() {
        return t -> {
            try {
                return this.test(t);
            } catch (Throwable throwable) {
                throw ThrowableUtil.throwUnchecked(throwable);
            }
        };
    }
}
