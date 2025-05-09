package org.zero.common.core.extension.java.util.function;

import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.util.function.BiFunction;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/8
 */
@FunctionalInterface
public interface ThrowThrowableBiFunction<T, U, R> {
    R apply(T t, U u) throws Throwable;

    static <T, U, R> ThrowThrowableBiFunction<T, U, R> of(BiFunction<T, U, R> function) {
        return function::apply;
    }

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
