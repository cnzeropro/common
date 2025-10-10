package org.zero.common.core.util.java.lang;

import org.zero.common.core.extension.java.lang.EmptyIterable;
import org.zero.common.core.util.java.util.IteratorUtil;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/4
 */
public class IterableUtil {
    public static <T> Iterable<T> empty() {
        return EmptyIterable.getInstance();
    }

    public static boolean isEmpty(Iterable<?> iterable) {
        return Objects.isNull(iterable) || IteratorUtil.isEmpty(iterable.iterator());
    }

    public static boolean nonEmpty(Iterable<?> iterable) {
        return !isEmpty(iterable);
    }

    protected IterableUtil() {
        throw new UnsupportedOperationException();
    }
}
