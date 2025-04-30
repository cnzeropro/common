package org.zero.common.core.util.java.util;

import org.zero.common.core.util.java.lang.ArrayUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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

    @SafeVarargs
    public static <E> List<E> of(boolean isLinked, E... elements) {
        List<E> list;
        if (isLinked) {
            list = new LinkedList<>();
        } else {
            list = new ArrayList<>(elements.length);
        }
        Collections.addAll(list, elements);
        return list;
    }

    @SafeVarargs
    public static <E> List<E> of(E... elements) {
        return of(false, elements);
    }
}
