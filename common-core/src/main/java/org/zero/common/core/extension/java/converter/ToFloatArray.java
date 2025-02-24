package org.zero.common.core.extension.java.converter;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/17
 */
public class ToFloatArray extends ToArray<Float> {
    public ToFloatArray() {
        this(ConverterComposite.getInstance());
    }

    public ToFloatArray(ConverterComposite converterComposite) {
        super(converterComposite, Float.class);
    }

    @Override
    public Float[] convert(Object source) {
        return super.convert(source);
    }
}
