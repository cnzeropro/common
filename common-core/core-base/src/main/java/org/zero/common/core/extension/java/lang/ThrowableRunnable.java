package org.zero.common.core.extension.java.lang;

import org.zero.common.core.util.java.lang.ThrowableUtil;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/8
 */
@FunctionalInterface
public interface ThrowableRunnable {
    void run() throws Throwable;

    static ThrowableRunnable of(Runnable runnable) {
        return runnable::run;
    }

    default Runnable to() {
        return () -> {
            try {
                this.run();
            } catch (Throwable throwable) {
                throw ThrowableUtil.throwUnchecked(throwable);
            }
        };
    }
}
