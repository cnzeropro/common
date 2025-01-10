package org.zero.common.core.extension.java.converter;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class BoolConverter implements Converter {
    public static final BoolConverter INSTANCE = new BoolConverter();

    public boolean convert(Object source) {
        Boolean b = ToBool.INSTANCE.convert(source);
        return Objects.nonNull(b) && b;
    }
}
