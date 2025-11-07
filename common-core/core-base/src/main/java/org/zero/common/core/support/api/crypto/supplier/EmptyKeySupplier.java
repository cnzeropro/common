package org.zero.common.core.support.api.crypto.supplier;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/24
 */
public class EmptyKeySupplier implements KeySupplier {
	public static final EmptyKeySupplier INSTANCE = new EmptyKeySupplier();

	@Override
	public byte[] generate(KeyContext context) {
		return new byte[0];
	}
}
