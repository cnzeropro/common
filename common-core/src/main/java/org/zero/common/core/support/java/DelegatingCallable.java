package org.zero.common.core.support.java;

import java.util.concurrent.Callable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/23
 */
public class DelegatingCallable<V> implements Callable<V> {
    protected final Callable<V> delegate;

    public DelegatingCallable(Callable<V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public V call() throws Exception {
        return delegate.call();
    }

    @Override
    public String toString() {
        return String.format("%s -> %s", super.toString(), delegate);
    }
}
