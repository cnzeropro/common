package org.zero.common.core.support.crypto.symmetric;

import org.zero.common.core.support.crypto.Mode;
import org.zero.common.core.support.crypto.Padding;

import javax.crypto.SecretKey;
import java.security.Provider;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/20
 */
public class AES extends SymmetricCrypto {
	public static final String FORMAT = "AES/%s/%s";
	public static final String ALGORITHM = String.format(FORMAT, Mode.ECB, Padding.PKCS5Padding);

	public AES() {
		this(ALGORITHM);
	}

	public AES(Provider provider) {
		this(ALGORITHM, provider);
	}

	public AES(Mode mode, Padding padding) {
		this(mode.name(), padding.name());
	}

	public AES(Mode mode, Padding padding, Provider provider) {
		this(mode.name(), padding.name(), provider);
	}

	public AES(String mode, String padding) {
		this(mode, padding, (Provider) null);
	}

	public AES(String mode, String padding, Provider provider) {
		this(mode, padding, provider, (byte[]) null);
	}

	public AES(String algorithm) {
		super(algorithm);
	}

	public AES(String algorithm, Provider provider) {
		super(algorithm, provider);
	}

	public AES(byte[] key) {
		this(ALGORITHM, key);
	}

	public AES(Provider provider, byte[] key) {
		this(ALGORITHM, provider, key);
	}

	public AES(Mode mode, Padding padding, byte[] key) {
		this(mode.name(), padding.name(), key);
	}

	public AES(Mode mode, Padding padding, Provider provider, byte[] key) {
		this(mode.name(), padding.name(), provider, key);
	}

	public AES(String mode, String padding, byte[] key) {
		this(mode, padding, null, key);
	}

	public AES(String mode, String padding, Provider provider, byte[] key) {
		super(String.format(FORMAT, mode, padding), provider, key);
	}

	public AES(String algorithm, byte[] key) {
		super(algorithm, key);
	}

	public AES(String algorithm, Provider provider, byte[] key) {
		super(algorithm, provider, key);
	}

	public AES(SecretKey key) {
		this(ALGORITHM, key);
	}

	public AES(Provider provider, SecretKey key) {
		this(ALGORITHM, provider, key);
	}

	public AES(Mode mode, Padding padding, SecretKey key) {
		this(mode.name(), padding.name(), key);
	}

	public AES(Mode mode, Padding padding, Provider provider, SecretKey key) {
		this(mode.name(), padding.name(), provider, key);
	}

	public AES(String mode, String padding, SecretKey key) {
		this(mode, padding, null, key);
	}

	public AES(String mode, String padding, Provider provider, SecretKey key) {
		this(String.format(FORMAT, mode, padding), provider, key);
	}

	public AES(String algorithm, SecretKey key) {
		super(algorithm, key);
	}

	public AES(String algorithm, Provider provider, SecretKey key) {
		super(algorithm, provider, key);
	}
}
