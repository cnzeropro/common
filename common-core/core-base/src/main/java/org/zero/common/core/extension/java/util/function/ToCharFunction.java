package org.zero.common.core.extension.java.util.function;

/**
 * 接收一个参数并返回 {@code char} 的函数式接口。
 * <p>
 * 用于补齐 JDK 未提供的 {@code char} 基础类型返回值函数。
 *
 * @author Zero (cnzeropro@163.com)
 * @see java.util.function.Function
 * @since 2025/4/28
 */
@FunctionalInterface
public interface ToCharFunction<T> {
	/**
	 * 将输入参数转换为 {@code char} 结果。
	 *
	 * @param value 输入参数
	 * @return 转换后的 {@code char} 结果
	 */
    char applyAsChar(T value);
}
