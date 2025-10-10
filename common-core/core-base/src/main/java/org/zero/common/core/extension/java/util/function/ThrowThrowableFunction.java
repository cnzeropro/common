package org.zero.common.core.extension.java.util.function;

import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.util.function.Function;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/8
 */
@FunctionalInterface
public interface ThrowThrowableFunction<T, R> {
    R apply(T t) throws Throwable;

    static <T, R> ThrowThrowableFunction<T, R> of(Function<T, R> function) {
        return function::apply;
    }

    default Function<T, R> to() {
        return t -> {
            try {
                return this.apply(t);
            } catch (Throwable throwable) {
                throw ThrowableUtil.throwUnchecked(throwable);
            }
        };
    }
}
