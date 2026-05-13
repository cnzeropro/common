package org.zero.common.core.extension.java.util.function;

import java.util.Objects;

/**
 * 接收五个参数且不返回结果的函数式接口。
 * <p>
 * 语义与 {@link java.util.function.BiConsumer} 一致，仅将入参数量扩展为五个。
 *
 * @author Zero (cnzeropro@163.com)
 * @see java.util.function.BiConsumer
 * @since 2025/4/27
 */
@FunctionalInterface
public interface QuintConsumer<A, B, C, D, E> {
	/**
	 * 对给定的五个参数执行操作。
     *
	 * @param a 第一个输入参数
	 * @param b 第二个输入参数
	 * @param c 第三个输入参数
	 * @param d 第四个输入参数
	 * @param e 第五个输入参数
     */
    void accept(A a, B b, C c, D d, E e);

    /**
	 * 返回顺序组合操作，先执行当前操作，再执行 {@code after}。
	 * <p>
	 * 如果当前操作抛出异常，{@code after} 不会执行；任一操作抛出的异常都会继续传递给调用方。
     *
	 * @param after 当前操作之后执行的操作
	 * @return 先执行当前操作再执行 {@code after} 的组合操作
	 * @throws NullPointerException 当 {@code after} 为 {@code null} 时抛出
     */
    default QuintConsumer<A, B, C, D, E> andThen(QuintConsumer<? super A, ? super B, ? super C, ? super D, ? super E> after) {
        Objects.requireNonNull(after);
        return (a, b, c, d, e) -> {
            accept(a, b, c, d, e);
            after.accept(a, b, c, d, e);
        };
    }
}
