package org.zero.common.core.support.api.crypto.strategy;

import lombok.experimental.Delegate;
import org.zero.common.core.support.api.crypto.CryptoContext;
import org.zero.common.core.support.api.crypto.CryptoFactory;
import org.zero.common.core.support.crypto.Crypto;

/**
 * @author Zero (cnzeropro@163.com)
 * @see CryptoFactory
 * @since 2025/10/30
 */
public class DefaultCrypto implements Crypto {
	protected final CryptoContext context;
	@Delegate
	protected final Crypto crypto;

	DefaultCrypto(CryptoContext context) {
		this(context, CryptoFactory.get(context));
	}

	protected DefaultCrypto(CryptoContext context, Crypto crypto) {
		this.context = context;
		this.crypto = crypto;
	}
}
