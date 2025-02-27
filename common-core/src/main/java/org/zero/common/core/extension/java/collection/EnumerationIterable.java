package org.zero.common.core.extension.java.collection;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/18
 */
@RequiredArgsConstructor(staticName = "of")
public class EnumerationIterable<E> implements Iterable<E> {
    private final Enumeration<E> enumeration;

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return EnumerationIterator.of(this.enumeration);
    }
}
