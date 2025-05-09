package org.zero.common.core.extension.java.util.concurrent;

import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.util.concurrent.Callable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/8
 */
@FunctionalInterface
public interface ThrowThrowableCallable<V> {
    V call() throws Throwable;

    static <V> ThrowThrowableCallable<V> of(Callable<V> callable) {
        return callable::call;
    }

    default Callable<V> to() {
        return () -> {
            try {
                return this.call();
            } catch (Throwable throwable) {
                throw ThrowableUtil.throwUnchecked(throwable);
            }
        };
    }
}