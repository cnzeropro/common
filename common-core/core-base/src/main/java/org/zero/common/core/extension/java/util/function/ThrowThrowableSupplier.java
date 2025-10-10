package org.zero.common.core.extension.java.util.function;

import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.util.function.Supplier;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/8
 */
@FunctionalInterface
public interface ThrowThrowableSupplier<T> {
    T get() throws Throwable;

    static <T> ThrowThrowableSupplier<T> of(Supplier<T> supplier) {
        return supplier::get;
    }

    default Supplier<T> to() {
        return () -> {
            try {
                return this.get();
            } catch (Throwable throwable) {
                throw ThrowableUtil.throwUnchecked(throwable);
            }
        };
    }
}
