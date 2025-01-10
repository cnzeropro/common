package org.zero.common.core.extension.java.converter;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class ToByte implements GenericConverter<Byte> {
    public static final ToByte INSTANCE = new ToByte();

    @Override
    public Byte convert(Object source) {
        if (source instanceof Byte) {
            return (Byte) source;
        }
        if (source instanceof Number) {
            return ((Number) source).byteValue();
        }
        if (Objects.nonNull(source)) {
            return Byte.valueOf(source.toString());
        }
        return null;
    }
}
