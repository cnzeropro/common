package org.zero.common.core.extension.java.converter;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class ShortConverter implements Converter {
    public static final ShortConverter INSTANCE = new ShortConverter();

    public short convert(Object source) {
        Short s = ToShort.INSTANCE.convert(source);
        return Objects.nonNull(s) ? s : (short) 0;
    }
}
