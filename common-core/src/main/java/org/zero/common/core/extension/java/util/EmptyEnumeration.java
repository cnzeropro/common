package org.zero.common.core.extension.java.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/27
 */
public class EmptyEnumeration<E> implements Enumeration<E> {
    static final EmptyEnumeration<Object> INSTANCE = new EmptyEnumeration<>();

    @SuppressWarnings("unchecked")
    public static <T> Enumeration<T> getInstance() {
        return (Enumeration<T>) INSTANCE;
    }

    @Override
    public boolean hasMoreElements() {
        return false;
    }

    @Override
    public E nextElement() {
        throw new NoSuchElementException();
    }
}
