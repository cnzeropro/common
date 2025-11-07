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

	public String encrypt(String string) {
		byte[] bytes = this.context.getSourceStringMode().toBytes(string);
		byte[] encryptedBytes = this.encrypt(bytes);
		return this.context.getTargetStringMode().toString(encryptedBytes);
	}

	public String encryptToString(byte[] bytes) {
		byte[] encryptedBytes = this.encrypt(bytes);
		return this.context.getTargetStringMode().toString(encryptedBytes);
	}

	public byte[] encryptToBytes(String string) {
		byte[] bytes = this.context.getSourceStringMode().toBytes(string);
		return this.encrypt(bytes);
	}

	public String decrypt(String string) {
		byte[] bytes = this.context.getSourceStringMode().toBytes(string);
		byte[] encryptedBytes = this.decrypt(bytes);
		return this.context.getTargetStringMode().toString(encryptedBytes);
	}

	public String decryptToString(byte[] bytes) {
		byte[] decrypted = this.decrypt(bytes);
		return this.context.getSourceStringMode().toString(decrypted);
	}

	public byte[] decryptToBytes(String string) {
		byte[] bytes = this.context.getSourceStringMode().toBytes(string);
		return this.decrypt(bytes);
	}
}
