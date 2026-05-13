package org.zero.common.core.extension.java.util.function;

/**
 * 三元同类型操作符。
 * <p>
 * 三个入参和返回值均为同一类型，是 {@link TriFunction} 的特化形式。
 *
 * @author Zero (cnzeropro@163.com)
 * @see java.util.function.BinaryOperator
 * @since 2025/4/27
 */
@FunctionalInterface
public interface TriOperator<T> extends TriFunction<T, T, T, T> {
}
