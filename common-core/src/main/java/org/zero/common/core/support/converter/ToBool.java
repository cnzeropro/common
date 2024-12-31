package org.zero.common.core.support.converter;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class ToBool implements GenericConverter<Boolean> {
    public static final ToBool INSTANCE = new ToBool();

    @Override
    public Boolean convert(Object source) {
        if (source instanceof Boolean) {
            return (Boolean) source;
        }
        if (source instanceof Number) {
            return ((Number) source).intValue() != 0;
        }
        if (Objects.nonNull(source)) {
            return Boolean.valueOf(source.toString());
        }
        return null;
    }
}
