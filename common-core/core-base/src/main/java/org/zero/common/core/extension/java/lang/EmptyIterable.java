package org.zero.common.core.extension.java.lang;

import org.zero.common.core.extension.java.util.EmptyIterator;

import java.util.Iterator;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/27
 */
public class EmptyIterable<T> implements Iterable<T> {
    public static final EmptyIterable<?> INSTANCE = new EmptyIterable<>();

    @SuppressWarnings("unchecked")
    public static <T> EmptyIterable<T> getInstance() {
        return (EmptyIterable<T>) INSTANCE;
    }

    @Override
    public Iterator<T> iterator() {
        return EmptyIterator.getInstance();
    }
}
