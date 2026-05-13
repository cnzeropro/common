package org.zero.common.core.extension.java.util.function;

/**
 * 四元同类型操作符。
 * <p>
 * 四个入参和返回值均为同一类型，是 {@link QuadFunction} 的特化形式。
 *
 * @author Zero (cnzeropro@163.com)
 * @see java.util.function.BinaryOperator
 * @since 2025/4/27
 */
@FunctionalInterface
public interface QuadOperator<T> extends QuadFunction<T, T, T, T, T> {
}
