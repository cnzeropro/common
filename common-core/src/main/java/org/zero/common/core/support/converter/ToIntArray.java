package org.zero.common.core.support.converter;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/17
 */
public class ToIntArray extends ToArray<Integer> {
    public ToIntArray() {
        this(ConverterComposite.getInstance());
    }

    public ToIntArray(ConverterComposite converterComposite) {
        super(Integer.class,converterComposite);
    }

    @Override
    public Integer[] convert(Object source) {
        return super.convert(source);
    }
}
