package org.zero.common.core.support.crypto.symmetric;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.zero.common.core.extension.javax.crypto.SecretKeyBuilder;
import org.zero.common.core.support.crypto.BaseCrypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.Provider;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/17
 */
public class SymmetricCrypto extends BaseCrypto {
	@Getter
	protected final SecretKey secretKey;
	@Setter
	protected AlgorithmParameterSpec algorithmParameterSpec;

	public SymmetricCrypto(String algorithm) {
		this(algorithm, (byte[]) null);
	}

	public SymmetricCrypto(String algorithm, byte[] key) {
		super(algorithm);
		this.secretKey = this.generateSecretKey(algorithm, null, key);
	}

	public SymmetricCrypto(String algorithm, SecretKey secretKey) {
		super(algorithm);
		this.secretKey = secretKey;
	}

	public SymmetricCrypto(String algorithm, String provider) {
		super(algorithm, provider);
		this.secretKey = this.generateSecretKey(algorithm, Security.getProvider(provider), null);
	}

	public SymmetricCrypto(String algorithm, String provider, byte[] key) {
		super(algorithm, provider);
		this.secretKey = this.generateSecretKey(algorithm, Security.getProvider(provider), key);
	}

	public SymmetricCrypto(String algorithm, String provider, SecretKey secretKey) {
		super(algorithm, provider);
		this.secretKey = secretKey;
	}

	public SymmetricCrypto(String algorithm, Provider provider) {
		super(algorithm, provider);
		this.secretKey = this.generateSecretKey(algorithm, provider, null);
	}

	public SymmetricCrypto(String algorithm, Provider provider, byte[] key) {
		super(algorithm, provider);
		this.secretKey = this.generateSecretKey(algorithm, provider, key);
	}

	public SymmetricCrypto(String algorithm, Provider provider, SecretKey secretKey) {
		super(algorithm, provider);
		this.secretKey = secretKey;
	}

	public SymmetricCrypto(Cipher cipher, SecretKey secretKey) {
		super(cipher);
		this.secretKey = secretKey;
	}

	protected SecretKey generateSecretKey(String algorithm, Provider provider, byte[] key) {
		return SecretKeyBuilder.builder(algorithm)
			.provider(provider)
			.key(key)
			.random(secureRandom)
			.build();
	}

	public void setIV(byte[] iv) {
		AlgorithmParameterSpec algorithmParameterSpec = Objects.isNull(iv) ? null : new IvParameterSpec(iv);
		this.setAlgorithmParameterSpec(algorithmParameterSpec);
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
