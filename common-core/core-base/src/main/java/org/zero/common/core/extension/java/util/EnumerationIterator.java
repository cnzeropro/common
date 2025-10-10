package org.zero.common.core.extension.java.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/18
 */
public class EnumerationIterator<E> implements Iterator<E> {
    protected final Enumeration<E> enumeration;

    protected EnumerationIterator(Enumeration<E> enumeration) {
        this.enumeration = Objects.nonNull(enumeration) ? enumeration : EmptyEnumeration.getInstance();
    }

    public static <E> EnumerationIterator<E> of(Enumeration<E> enumeration) {
        return new EnumerationIterator<>(enumeration);
    }

    @Override
    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }

    @Override
    public E next() {
        return enumeration.nextElement();
    }
}
