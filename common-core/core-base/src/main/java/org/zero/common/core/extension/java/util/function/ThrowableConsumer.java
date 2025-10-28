package org.zero.common.core.extension.java.util.function;

import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.util.function.Consumer;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/8
 */
@FunctionalInterface
public interface ThrowableConsumer<T> {
    void accept(T t) throws Throwable;

    static <T> ThrowableConsumer<T> of(Consumer<T> consumer) {
        return consumer::accept;
    }

    default Consumer<T> to() {
        return t -> {
            try {
                this.accept(t);
            } catch (Throwable throwable) {
                throw ThrowableUtil.throwUnchecked(throwable);
            }
        };
    }
}
