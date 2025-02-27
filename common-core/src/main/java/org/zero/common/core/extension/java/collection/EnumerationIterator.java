package org.zero.common.core.extension.java.collection;

import lombok.RequiredArgsConstructor;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/18
 */
@RequiredArgsConstructor(staticName = "of")
public class EnumerationIterator<E> implements Iterator<E> {
    private final Enumeration<E> enumeration;

    @Override
    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }

    @Override
    public E next() {
        return enumeration.nextElement();
    }
}
