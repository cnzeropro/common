package org.zero.common.core.support.converter;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/17
 */
public class ToBoolArray extends ToArray<Boolean> {
    public ToBoolArray() {
        this(ConverterComposite.getInstance());
    }

    public ToBoolArray(ConverterComposite converterComposite) {
        super(Boolean.class,converterComposite);
    }

    @Override
    public Boolean[] convert(Object source) {
        return super.convert(source);
    }
}
