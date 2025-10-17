package org.zero.common.core.support.crypto;

import lombok.SneakyThrows;
import org.zero.common.core.extension.javax.crypto.SecretKeyBuilder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/17
 */
public class SymmetricCrypto extends BaseCrypto {
	protected final SecretKey secretKey;
	protected AlgorithmParameterSpec algorithmParameterSpec;

	public SymmetricCrypto(String algorithm) {
		this(algorithm, (byte[]) null);
	}

	public SymmetricCrypto(String algorithm, byte[] key) {
		super(algorithm);
		this.secretKey = this.generateKey(algorithm, key);
	}

	public SymmetricCrypto(String algorithm, byte[] key, AlgorithmParameterSpec algorithmParameterSpec) {
		super(algorithm);
		this.secretKey = this.generateKey(algorithm, key);
		this.algorithmParameterSpec = algorithmParameterSpec;
	}

	public SymmetricCrypto(String algorithm, String provider) {
		super(algorithm, provider);
		this.secretKey = this.generateKey(algorithm, null);
	}

	public SymmetricCrypto(String algorithm, String provider, byte[] key) {
		super(algorithm, provider);
		this.secretKey = this.generateKey(algorithm, key);
	}

	public SymmetricCrypto(String algorithm, String provider, byte[] key, AlgorithmParameterSpec algorithmParameterSpec) {
		super(algorithm, provider);
		this.secretKey = this.generateKey(algorithm, key);
		this.algorithmParameterSpec = algorithmParameterSpec;
	}

	public SymmetricCrypto(String algorithm, Provider provider) {
		super(algorithm, provider);
		this.secretKey = this.generateKey(algorithm, null);
	}

	public SymmetricCrypto(String algorithm, Provider provider, byte[] key) {
		super(algorithm, provider);
		this.secretKey = this.generateKey(algorithm, key);
	}

	public SymmetricCrypto(String algorithm, Provider provider, byte[] key, AlgorithmParameterSpec algorithmParameterSpec) {
		super(algorithm, provider);
		this.secretKey = this.generateKey(algorithm, key);
		this.algorithmParameterSpec = algorithmParameterSpec;
	}

	public SymmetricCrypto(Cipher cipher, SecretKey secretKey, AlgorithmParameterSpec algorithmParameterSpec) {
		super(cipher);
		this.secretKey = secretKey;
		this.algorithmParameterSpec = algorithmParameterSpec;
	}

	protected SecretKey generateKey(String algorithm, byte[] key) {
		return SecretKeyBuilder.builder()
			.algorithm(algorithm)
			.key(key)
			.random(secureRandom)
			.build();
	}

	@SneakyThrows
	@Override
	protected void initMode(int mode) {
		if (Objects.isNull(algorithmParameterSpec)) {
			if (Objects.isNull(secureRandom)) {
				cipher.init(mode, secretKey);
			} else {
				cipher.init(mode, secretKey, secureRandom);
			}
		} else {
			if (Objects.isNull(secureRandom)) {
				cipher.init(mode, secretKey, algorithmParameterSpec);
			} else {
				cipher.init(mode, secretKey, algorithmParameterSpec, secureRandom);
			}
		}
	}
}
