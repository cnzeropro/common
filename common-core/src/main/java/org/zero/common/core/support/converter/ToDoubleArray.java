package org.zero.common.core.support.converter;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/17
 */
public class ToDoubleArray extends ToArray<Double> {
    public ToDoubleArray() {
        this(ConverterComposite.getInstance());
    }

    public ToDoubleArray(ConverterComposite converterComposite) {
        super(Double.class, converterComposite);
    }

    @Override
    public Double[] convert(Object source) {
        return super.convert(source);
    }
}
