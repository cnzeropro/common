package org.zero.common.core.support.converter;

import org.zero.common.data.constant.ConstantPool;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class FloatConverter implements Converter {
    public static final FloatConverter INSTANCE = new FloatConverter();

    public float convert(Object source) {
        Float f = ToFloat.INSTANCE.convert(source);
        return Objects.nonNull(f) ? f : ConstantPool.FLOAT_ZERO;
    }
}
