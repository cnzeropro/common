package org.zero.common.core.extension.java.converter;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/17
 */
public class ToIntArray extends ToArray<Integer> {
    public ToIntArray() {
        this(ConverterComposite.getInstance());
    }

    public ToIntArray(ConverterComposite converterComposite) {
        super(converterComposite, Integer.class);
    }

    @Override
    public Integer[] convert(Object source) {
        return super.convert(source);
    }
}
