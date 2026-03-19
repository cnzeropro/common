package org.zero.common.core.extension.java.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/18
 */
public class EnumerationIterable<E> implements Iterable<E> {
    protected final Enumeration<E> enumeration;

    protected EnumerationIterable(Enumeration<E> enumeration) {
        this.enumeration = Objects.nonNull(enumeration) ? enumeration : EmptyEnumeration.getInstance();
    }

    public static <E> EnumerationIterable<E> of(Enumeration<E> enumeration) {
        return new EnumerationIterable<>(enumeration);
    }

    @Override
    public Iterator<E> iterator() {
        return EnumerationIterator.of(this.enumeration);
    }
}
