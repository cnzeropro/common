package org.zero.common.core.util.java.lang;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/1
 */
public class ObjectUtil {
    public static boolean isNull(Object object) {
        return Objects.isNull(object);
    }

    public static boolean nonNull(Object object) {
        return !isNull(object);
    }

    public static boolean isEmpty(Object object) {
        if (isNull(object)) {
            return true;
        }
        if (object instanceof CharSequence) {
            return CharSequenceUtil.isEmpty((CharSequence) object);
        }
        if (ArrayUtil.isArray(object)) {
            return ArrayUtil.isEmpty(ArrayUtil.toArray(object));
        }
        if (object instanceof Collection) {
            return !((Collection<?>) object).isEmpty();
        }
        if (object instanceof Map) {
            return ((Map<?, ?>) object).isEmpty();
        }
        return false;
    }

    public static boolean nonEmpty(Object object) {
        return !isEmpty(object);
    }

    public static <T> T defaultIfNull(final T object, final T defaultValue) {
        return isNull(object) ? defaultValue : object;
    }

    public static <T> T defaultIfEmpty(final T object, final T defaultValue) {
        return isEmpty(object) ? defaultValue : object;
    }

    public static <T, R> R mapIfNonNull(final T object, final Function<T, R> mapper) {
        return nonNull(object) ? mapper.apply(object) : null;
    }

    public static <T, R> R mapIfNonEmpty(final T object, final Function<T, R> mapper) {
        return nonEmpty(object) ? mapper.apply(object) : null;
    }

    protected ObjectUtil() {
        throw new UnsupportedOperationException();
    }
}
