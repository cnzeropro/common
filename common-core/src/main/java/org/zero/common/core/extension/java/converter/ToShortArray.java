package org.zero.common.core.extension.java.converter;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/17
 */
public class ToShortArray extends ToArray<Short> {
    public ToShortArray() {
        this(ConverterComposite.getInstance());
    }

    public ToShortArray(ConverterComposite converterComposite) {
        super(converterComposite, Short.class);
    }

    @Override
    public Short[] convert(Object source) {
        return super.convert(source);
    }
}
