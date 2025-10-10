package org.zero.common.core.util.java.util.stream;

import org.zero.common.core.extension.java.util.EnumerationIterable;
import org.zero.common.core.extension.java.util.IteratorIterable;
import org.zero.common.core.util.java.lang.ArrayUtil;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/27
 */
public class StreamUtil {
    public static <T> Stream<T> of(T[] array) {
        return of(array, false);
    }

    public static <T> Stream<T> of(T[] array, boolean parallel) {
        Spliterator<T> spliterator = Spliterators.spliterator(Objects.isNull(array) ? ArrayUtil.EMPTY : array, Spliterator.ORDERED | Spliterator.IMMUTABLE);
        return StreamSupport.stream(spliterator, parallel);
    }

    public static <T> Stream<T> of(Enumeration<T> enumeration) {
        return of(enumeration, false);
    }

    public static <T> Stream<T> of(Enumeration<T> enumeration, boolean parallel) {
        return of(EnumerationIterable.of(enumeration), parallel);
    }

    public static <T> Stream<T> of(Iterator<T> iterator) {
        return of(iterator, false);
    }

    public static <T> Stream<T> of(Iterator<T> iterator, boolean parallel) {
        return of(IteratorIterable.of(iterator), parallel);
    }

    public static <T> Stream<T> of(Iterable<T> iterable) {
        return of(iterable, false);
    }

    public static <T> Stream<T> of(Iterable<T> iterable, boolean parallel) {
        if (iterable instanceof Collection) {
            Collection<T> collection = (Collection<T>) iterable;
            return parallel ? collection.parallelStream() : collection.stream();
        }
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }
}
