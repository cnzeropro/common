package org.zero.common.core.extension.java.converter;

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
            int i = ((Number) source).intValue();
            if (i == 0) {
                return Boolean.FALSE;
            }
            if (i == 1) {
                return Boolean.TRUE;
            }
        }
        if (Objects.nonNull(source)) {
            // return Boolean.valueOf(source.toString());
            String string = source.toString();
            if ("true".equalsIgnoreCase(string)) {
                return Boolean.TRUE;
            }
            if ("false".equalsIgnoreCase(string)) {
                return Boolean.FALSE;
            }
        }
        return null;
    }
}
