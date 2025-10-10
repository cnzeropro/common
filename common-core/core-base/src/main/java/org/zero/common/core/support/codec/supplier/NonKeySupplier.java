package org.zero.common.core.support.codec.supplier;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/24
 */
public class NonKeySupplier implements KeySupplier {
    public static final NonKeySupplier INSTANCE = new NonKeySupplier();

    @Override
    public byte[] generate(KeyContext context) {
        return new byte[0];
    }
}
