package org.zero.common.core.extension.java.converter;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/12/30
 */
public class ToChar implements GenericConverter<Character> {
    public static final ToChar INSTANCE = new ToChar();

    @Override
    public Character convert(Object source) {
        if (source instanceof Character) {
            return (Character) source;
        }
        if (source instanceof Number) {
            return (char) ((Number) source).intValue();
        }
        if (Objects.nonNull(source)) {
            String string = source.toString();
            if (!string.isEmpty()) {
                return string.charAt(0);
            }
        }
        return null;
    }
}
