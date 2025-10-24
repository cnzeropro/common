package org.zero.common.core.support.crypto.symmetric;

import org.zero.common.core.support.crypto.Mode;
import org.zero.common.core.support.crypto.Padding;

import javax.crypto.SecretKey;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/20
 */
public class AES extends SymmetricCrypto {
	public static final String ALGORITHM = "AES/ECB/PKCS5Padding";

	public AES() {
		super(ALGORITHM);
	}

	public AES(Mode mode, Padding padding) {
		this(mode.name(), padding.name());
	}

	public AES(String mode, String padding) {
		super(String.format("AES/%s/%s", mode, padding));
	}

	public AES(String algorithm) {
		super(algorithm);
	}

	public AES(byte[] key) {
		super(ALGORITHM, key);
	}

	public AES(Mode mode, Padding padding, byte[] key) {
		this(mode.name(), padding.name(), key);
	}

	public AES(String mode, String padding, byte[] key) {
		super(String.format("AES/%s/%s", mode, padding), key);
	}

	public AES(String algorithm, byte[] key) {
		super(algorithm, key);
	}

	public AES(SecretKey key) {
		super(ALGORITHM, key);
	}

	public AES(Mode mode, Padding padding, SecretKey key) {
		this(mode.name(), padding.name(), key);
	}

	public AES(String mode, String padding, SecretKey key) {
		super(String.format("AES/%s/%s", mode, padding), key);
	}

	public AES(String algorithm, SecretKey key) {
		super(algorithm, key);
	}
}
