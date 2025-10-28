package org.zero.common.core.extension.java.util.function;

import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.util.function.Predicate;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/8
 */
@FunctionalInterface
public interface ThrowablePredicate<T> {
    boolean test(T t) throws Throwable;

    static <T> ThrowablePredicate<T> of(Predicate<T> predicate) {
        return predicate::test;
    }

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
