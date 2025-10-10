package org.zero.common.core.extension.java.util;

import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.util.Comparator;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/8
 */
@FunctionalInterface
public interface ThrowThrowableComparator<T> {
    int compare(T o1, T o2) throws Throwable;

    static <T> ThrowThrowableComparator<T> of(Comparator<T> comparator) {
        return comparator::compare;
    }

    default Comparator<T> to() {
        return (o1, o2) -> {
            try {
                return this.compare(o1, o2);
            } catch (Throwable throwable) {
                throw ThrowableUtil.throwUnchecked(throwable);
            }
        };
    }
}