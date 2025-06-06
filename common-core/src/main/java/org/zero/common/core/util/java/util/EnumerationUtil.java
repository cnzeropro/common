package org.zero.common.core.util.java.util;

import org.zero.common.core.extension.java.util.EmptyEnumeration;
import org.zero.common.core.extension.java.util.EnumerationIterator;
import org.zero.common.core.extension.java.util.function.ThrowThrowableSupplier;
import org.zero.common.core.util.java.lang.ThrowableUtil;

import java.util.Enumeration;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/6/4
 */
public class EnumerationUtil {
    public static <T> Enumeration<T> empty() {
        return EmptyEnumeration.getInstance();
    }

    public static boolean isEmpty(Enumeration<?> enumeration) {
        return Objects.isNull(enumeration) || IteratorUtil.isEmpty(EnumerationIterator.of(enumeration));
    }

    public static boolean nonEmpty(Enumeration<?> enumeration) {
        return !isEmpty(enumeration);
    }

    public static <T> T get(final Enumeration<T> enumeration, final int index) {
        int i = index;
        while (enumeration.hasMoreElements()) {
            i--;
            T element = enumeration.nextElement();
            if (i < 0) {
                return element;
            }
        }
        throw new IndexOutOfBoundsException("Entry does not exist: " + i);
    }

    public static <T> Optional<T> getOpt(final Enumeration<T> enumeration, final int index) {
        return ThrowableUtil.ignoreOpt((ThrowThrowableSupplier<T>) () -> get(enumeration, index));
    }

    protected EnumerationUtil() {
        throw new UnsupportedOperationException();
    }
}
