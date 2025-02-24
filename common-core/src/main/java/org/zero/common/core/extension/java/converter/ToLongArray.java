package org.zero.common.core.extension.java.converter;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/17
 */
public class ToLongArray extends ToArray<Long> {
    public ToLongArray() {
        this(ConverterComposite.getInstance());
    }

    public ToLongArray(ConverterComposite converterComposite) {
        super(converterComposite, Long.class);
    }

    @Override
    public Long[] convert(Object source) {
        return super.convert(source);
    }
}
