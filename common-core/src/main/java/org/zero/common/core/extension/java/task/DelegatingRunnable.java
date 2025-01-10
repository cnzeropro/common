package org.zero.common.core.extension.java.task;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/23
 */
public class DelegatingRunnable implements Runnable {
    protected final Runnable delegate;

    public DelegatingRunnable(Runnable delegate) {
        this.delegate = delegate;
    }

    @Override
    public void run() {
        delegate.run();
    }

    @Override
    public String toString() {
        return String.format("%s -> %s", super.toString(), delegate);
    }
}
