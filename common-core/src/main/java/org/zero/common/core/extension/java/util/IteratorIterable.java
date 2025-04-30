package org.zero.common.core.extension.java.util;

import java.util.Iterator;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/24
 */
public class IteratorIterable<T> implements Iterable<T> {
    protected final Iterator<T> iterator;

    protected IteratorIterable(Iterator<T> iterator) {
        this.iterator = Objects.nonNull(iterator) ? iterator : EmptyIterator.getInstance();
    }

    public static <T> IteratorIterable<T> of(Iterator<T> iterator) {
        return new IteratorIterable<>(iterator);
    }

    @Override
    public Iterator<T> iterator() {
        return iterator;
    }
}
