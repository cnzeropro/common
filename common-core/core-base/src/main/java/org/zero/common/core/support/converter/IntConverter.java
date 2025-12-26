package org.zero.common.core.support.converter;

import org.zero.common.data.constant.ConstantPool;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class IntConverter implements Converter {
    public static final IntConverter INSTANCE = new IntConverter();

    public int convert(Object source) {
        Integer i = ToInt.INSTANCE.convert(source);
        return Objects.nonNull(i) ? i : ConstantPool.INT_ZERO;
    }
}
