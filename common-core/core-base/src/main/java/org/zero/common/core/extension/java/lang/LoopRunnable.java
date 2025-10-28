package org.zero.common.core.extension.java.lang;

import lombok.RequiredArgsConstructor;
import org.zero.common.core.util.java.lang.ThrowableUtil;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/14
 */
@RequiredArgsConstructor
public class LoopRunnable implements Runnable {
    protected final ThrowableRunnable runnable;

    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        while (!currentThread.isInterrupted()) {
            try {
                runnable.run();
            } catch (InterruptedException e) {
                currentThread.interrupt();
                break;
            } catch (Throwable t) {
                throw ThrowableUtil.throwUnchecked(t);
            }
        }
    }
}
