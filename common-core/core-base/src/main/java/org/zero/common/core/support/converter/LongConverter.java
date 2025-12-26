package org.zero.common.core.support.converter;

import org.zero.common.data.constant.ConstantPool;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class LongConverter implements Converter {
    public static final LongConverter INSTANCE = new LongConverter();

    public long convert(Object source) {
        Long l = ToLong.INSTANCE.convert(source);
        return Objects.nonNull(l) ? l : ConstantPool.LONG_ZERO;
    }
}
