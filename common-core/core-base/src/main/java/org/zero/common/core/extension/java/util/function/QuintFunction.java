package org.zero.common.core.extension.java.util.function;

import java.util.Objects;
import java.util.function.Function;

/**
 * 接收五个参数并返回一个结果的函数式接口。
 * <p>
 * 语义与 {@link java.util.function.BiFunction} 一致，仅将入参数量扩展为五个。
 *
 * @author Zero (cnzeropro@163.com)
 * @see java.util.function.BiFunction
 * @since 2025/4/24
 */
@FunctionalInterface
public interface QuintFunction<A, B, C, D, E, R> {
	/**
	 * 对给定的五个参数执行函数。
     *
	 * @param a 第一个函数参数
	 * @param b 第二个函数参数
	 * @param c 第三个函数参数
	 * @param d 第四个函数参数
	 * @param e 第五个函数参数
	 * @return 函数结果
     */
    R apply(A a, B b, C c, D d, E e);

    /**
	 * 返回组合函数，先执行当前函数，再将结果交给 {@code after} 处理。
	 * <p>
	 * 任一函数抛出的异常都会继续传递给组合函数的调用方。
     *
	 * @param <V> 组合函数的返回值类型
	 * @param after 当前函数执行后应用的函数
	 * @return 先执行当前函数再执行 {@code after} 的组合函数
	 * @throws NullPointerException 当 {@code after} 为 {@code null} 时抛出
     */
    default <V> QuintFunction<A, B, C, D, E, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (A a, B b, C c, D d, E e) -> after.apply(apply(a, b, c, d, e));
    }
}
