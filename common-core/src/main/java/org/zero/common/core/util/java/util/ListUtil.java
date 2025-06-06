package org.zero.common.core.util.java.util;

import org.zero.common.core.extension.java.util.EmptyIterator;
import org.zero.common.core.extension.java.util.EnumerationIterator;
import org.zero.common.core.util.java.lang.ArrayUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/16
 */
public class ListUtil {
    /**
     * Default initial capacity.
     */
    public static final int DEFAULT_CAPACITY = 10;
    public static final int MAX_ARRAY_LIST_SIZE = ArrayUtil.MAX_ARRAY_SIZE;

    public static <E> List<E> create(boolean isLinked, int size) {
        return isLinked ? new LinkedList<>() : new ArrayList<>(size);
    }

    public static <E> List<E> create(boolean isLinked) {
        return create(isLinked, DEFAULT_CAPACITY);
    }

    @SafeVarargs
    public static <E> List<E> of(boolean isLinked, E... elements) {
        List<E> list = create(isLinked, elements.length);
        Collections.addAll(list, elements);
        return list;
    }

    @SafeVarargs
    public static <E> List<E> of(E... elements) {
        return of(false, elements);
    }

    public static <E> List<E> of(boolean isLinked, Iterable<E> iterable) {
        Iterator<E> iterator = Objects.isNull(iterable) ? EmptyIterator.getInstance() : iterable.iterator();
        return of(isLinked, iterator);
    }

    public static <E> List<E> of(Iterable<E> iterable) {
        return of(false, iterable);
    }

    public static <E> List<E> of(boolean isLinked, Iterator<E> iterator) {
        List<E> list = create(isLinked);
        if (Objects.nonNull(iterator)) {
            while (iterator.hasNext()) {
                list.add(iterator.next());
            }
        }
        return list;
    }

    public static <E> List<E> of(Iterator<E> iterator) {
        return of(false, iterator);
    }

    public static <E> List<E> of(boolean isLinked, Enumeration<E> enumeration) {
        return of(isLinked, EnumerationIterator.of(enumeration));
    }

    public static <E> List<E> of(Enumeration<E> enumeration) {
        return of(false, enumeration);
    }
}
