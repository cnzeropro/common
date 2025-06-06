package org.zero.common.core.util.java.util;

import org.zero.common.core.extension.java.util.EmptyIterator;

import java.util.Iterator;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/4
 */
public class IteratorUtil {
    public static <T> Iterator<T> empty() {
        return EmptyIterator.getInstance();
    }

    public static boolean isEmpty(Iterator<?> iterator) {
        return Objects.isNull(iterator) || !iterator.hasNext();
    }

    public static boolean nonEmpty(Iterator<?> iterator) {
        return !isEmpty(iterator);
    }

    protected IteratorUtil() {
        throw new UnsupportedOperationException();
    }
}
