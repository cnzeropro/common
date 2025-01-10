package org.zero.common.core.extension.java.converter;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class ToShort implements GenericConverter<Short>{
    public static final ToShort INSTANCE = new ToShort();
    @Override
    public Short convert(Object source) {
        if (source instanceof Short){
            return (Short) source;
        }
        if (source instanceof Number){
            return ((Number) source).shortValue();
        }
        if (Objects.nonNull(source)){
            return Short.valueOf(source.toString());
        }
        return null;
    }
}
