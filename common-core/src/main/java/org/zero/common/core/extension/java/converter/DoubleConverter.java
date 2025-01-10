package org.zero.common.core.extension.java.converter;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class DoubleConverter implements Converter {
    public static final DoubleConverter INSTANCE = new DoubleConverter();

    public double convert(Object source) {
        Double d = ToDouble.INSTANCE.convert(source);
        return Objects.nonNull(d) ? d : 0.0D;
    }
}
