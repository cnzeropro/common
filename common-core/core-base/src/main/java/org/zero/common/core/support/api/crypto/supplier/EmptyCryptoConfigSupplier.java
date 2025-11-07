package org.zero.common.core.support.api.crypto.supplier;

import java.util.Collections;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/31
 */
public class EmptyCryptoConfigSupplier implements CryptoConfigSupplier {
	@Override
	public Map<String, Object> getConfig() {
		return Collections.emptyMap();
	}
}
