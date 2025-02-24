package org.zero.common.core.extension.java.converter;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/17
 */
public class ToByteArray extends ToArray<Byte> {
    public ToByteArray() {
        this(ConverterComposite.getInstance());
    }

    public ToByteArray(ConverterComposite converterComposite) {
        super(converterComposite, Byte.class);
    }

    @Override
    public Byte[] convert(Object source) {
        return super.convert(source);
    }
}
