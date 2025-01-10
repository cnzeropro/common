package org.zero.common.core.extension.java.converter;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class ToDouble implements GenericConverter<Double>{
    public static final ToDouble INSTANCE = new ToDouble();

    @Override
    public Double convert(Object source) {
        if (source instanceof Double){
            return (Double) source;
        }
        if (source instanceof Number){
            return ((Number) source).doubleValue();
        }
        if (Objects.nonNull(source)){
            return Double.valueOf(source.toString());
        }
        return null;
    }
}
