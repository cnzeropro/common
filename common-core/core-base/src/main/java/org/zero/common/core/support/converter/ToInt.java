package org.zero.common.core.support.converter;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class ToInt implements GenericConverter<Integer> {
    public static final ToInt INSTANCE = new ToInt();

    @Override
    public Integer convert(Object source) {
        if (source instanceof Integer) {
            return (Integer) source;
        }
        if (source instanceof Number) {
            return ((Number) source).intValue();
        }
        if (Objects.nonNull(source)) {
            return Integer.valueOf(source.toString());
        }
        return null;
    }
}
