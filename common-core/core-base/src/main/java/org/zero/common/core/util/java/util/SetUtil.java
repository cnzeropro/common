package org.zero.common.core.util.java.util;


import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/25
 */
public class SetUtil {
    public static <E> Set<E> of(boolean isLinked, E... elements) {
        int capacity = MapUtil.calculateCapacity(elements.length);
        Set<E> set;
        if (isLinked) {
            set = new LinkedHashSet<>(capacity);
        } else {
            set = new HashSet<>(capacity);
        }
        Collections.addAll(set, elements);
        return set;
    }

    public static <E> Set<E> of(E... elements) {
        return of(false, elements);
    }
}
