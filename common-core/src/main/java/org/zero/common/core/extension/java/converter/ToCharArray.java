package org.zero.common.core.extension.java.converter;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/17
 */
public class ToCharArray extends ToArray<Character> {
    public ToCharArray() {
        this(ConverterComposite.getInstance());
    }

    public ToCharArray(ConverterComposite converterComposite) {
        super(converterComposite, Character.class);
    }

    @Override
    public Character[] convert(Object source) {
        return super.convert(source);
    }
}
