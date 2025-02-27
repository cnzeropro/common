package org.zero.common.core.extension.java.collection;

import lombok.RequiredArgsConstructor;

import java.util.Iterator;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/24
 */
@RequiredArgsConstructor(staticName = "of")
public class IteratorIterable<T> implements Iterable<T> {
    private final Iterator<T> iterator;

    @Override
    public Iterator<T> iterator() {
        return iterator;
    }
}
