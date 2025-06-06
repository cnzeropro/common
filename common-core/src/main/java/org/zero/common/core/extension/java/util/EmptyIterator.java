package org.zero.common.core.extension.java.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/27
 */
public class EmptyIterator<T> implements Iterator<T> {
    public static final Iterator<?> INSTANCE = new EmptyIterator<>();

    @SuppressWarnings("unchecked")
    public static <T> Iterator<T> getInstance() {
        return (Iterator<T>) INSTANCE;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public T next() {
        throw new NoSuchElementException();
    }
}
