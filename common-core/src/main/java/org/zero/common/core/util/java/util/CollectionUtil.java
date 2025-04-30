package org.zero.common.core.util.java.util;

import org.zero.common.core.util.java.lang.ObjectUtil;
import org.zero.common.data.constant.StringPool;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/16
 */
public class CollectionUtil {
    public static boolean isEmpty(Collection<?> collection) {
        return ObjectUtil.isNull(collection) || collection.isEmpty();
    }

    public static boolean nonEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    protected CollectionUtil() {
        throw new UnsupportedOperationException();
    }

    public static String join(Collection<?> collection, CharSequence delimiter) {
        if (isEmpty(collection)){
            return StringPool.EMPTY;
        }
        return collection.stream()
                .filter(Objects::nonNull)
                .map(Objects::toString)
                .collect(Collectors.joining(delimiter));
    }
}
