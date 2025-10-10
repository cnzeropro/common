package org.zero.common.core.extension.java.util.function;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @see java.util.function.BiConsumer
 * @since 2025/4/27
 */
@FunctionalInterface
public interface QuintConsumer<A, B, C, D, E> {
    /**
     * Performs this operation on the given arguments.
     *
     * @param a the first input argument
     * @param b the second input argument
     * @param c the third input argument
     * @param d the fourth input argument
     * @param e the fifth input argument
     */
    void accept(A a, B b, C c, D d, E e);

    /**
     * Returns a composed {@code BiConsumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code BiConsumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default QuintConsumer<A, B, C, D, E> andThen(QuintConsumer<? super A, ? super B, ? super C, ? super D, ? super E> after) {
        Objects.requireNonNull(after);
        return (a, b, c, d, e) -> {
            accept(a, b, c, d, e);
            after.accept(a, b, c, d, e);
        };
    }
}
