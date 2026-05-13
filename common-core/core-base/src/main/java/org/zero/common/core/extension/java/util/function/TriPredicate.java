package org.zero.common.core.extension.java.util.function;

import java.util.Objects;

/**
 * 接收三个参数并返回布尔结果的断言接口。
 * <p>
 * 语义与 {@link java.util.function.BiPredicate} 一致，仅将入参数量扩展为三个。
 *
 * @author Zero (cnzeropro@163.com)
 * @see java.util.function.BiPredicate
 * @since 2025/4/27
 */
@FunctionalInterface
public interface TriPredicate<A, B, C> {
	/**
	 * 对给定的三个参数执行断言判断。
     *
	 * @param a 第一个输入参数
	 * @param b 第二个输入参数
	 * @param c 第三个输入参数
	 * @return 如果输入参数匹配断言则返回 {@code true}，否则返回 {@code false}
     */
    boolean test(A a, B b, C c);

    /**
	 * 返回短路逻辑 AND 组合断言。
     *
	 * <p>如果当前断言返回 {@code false}，{@code other} 不会执行。
	 * 任一断言抛出的异常都会继续传递给调用方。
     *
	 * @param other 与当前断言进行逻辑 AND 组合的断言
	 * @return 表示短路逻辑 AND 的组合断言
	 * @throws NullPointerException 当 {@code other} 为 {@code null} 时抛出
     */
    default TriPredicate<A, B, C> and(TriPredicate<? super A, ? super B, ? super C> other) {
        Objects.requireNonNull(other);
        return (A a, B b, C c) -> test(a, b, c) && other.test(a, b, c);
    }

	/**
	 * 返回当前断言的逻辑非断言。
     *
	 * @return 表示当前断言逻辑非的断言
     */
    default TriPredicate<A, B, C> negate() {
        return (A a, B b, C c) -> !test(a, b, c);
    }

    /**
	 * 返回短路逻辑 OR 组合断言。
     *
	 * <p>如果当前断言返回 {@code true}，{@code other} 不会执行。
	 * 任一断言抛出的异常都会继续传递给调用方。
     *
	 * @param other 与当前断言进行逻辑 OR 组合的断言
	 * @return 表示短路逻辑 OR 的组合断言
	 * @throws NullPointerException 当 {@code other} 为 {@code null} 时抛出
     */
    default TriPredicate<A, B, C> or(TriPredicate<? super A, ? super B, ? super C> other) {
        Objects.requireNonNull(other);
        return (A a, B b, C c) -> test(a, b, c) || other.test(a, b, c);
    }
}
