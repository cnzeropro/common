package org.zero.common.core.support.converter;

import org.zero.common.data.constant.ConstantPool;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class ByteConverter implements Converter {
    public static final ByteConverter INSTANCE = new ByteConverter();

    public byte convert(Object source) {
        Byte b = ToByte.INSTANCE.convert(source);
        return Objects.nonNull(b) ? b : ConstantPool.BYTE_ZERO;
    }
}
