package org.zero.common.core.support.converter;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class ToFloat implements ObjectConverter<Float> {
    public static final ToFloat INSTANCE = new ToFloat();
    @Override
    public Float convert(Object source) {
        if (source instanceof Float) {
            return (Float) source;
        }
        if (source instanceof Number) {
            return ((Number) source).floatValue();
        }
        if (Objects.nonNull(source)) {
            return Float.valueOf(source.toString());
        }
        return null;
    }
}
