package org.zero.common.core.extension.java.converter;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class CharConverter implements Converter {
    public static final CharConverter INSTANCE = new CharConverter();

    public char convert(Object source) {
        Character c = ToChar.INSTANCE.convert(source);
        return Objects.nonNull(c) ? c : '\u0000';
    }
}
