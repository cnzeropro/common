package org.zero.common.core.support.converter;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class ByteConverter implements Converter {
    public static final ByteConverter INSTANCE = new ByteConverter();

    public byte convert(Object source) {
        Byte b = ToByte.INSTANCE.convert(source);
        return Objects.nonNull(b) ? b : (byte) 0;
    }
}
