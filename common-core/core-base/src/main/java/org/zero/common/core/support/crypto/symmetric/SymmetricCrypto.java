package org.zero.common.core.support.crypto.symmetric;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.zero.common.core.extension.javax.crypto.SecretKeyBuilder;
import org.zero.common.core.support.crypto.BaseCrypto;
import org.zero.common.core.support.crypto.CipherMode;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/17
 */
public class SymmetricCrypto extends BaseCrypto {
	/**
	 * 密钥
	 */
	@Getter
	protected final SecretKey secretKey;
	/**
	 * 密钥参数规范
	 */
	@Setter
	protected AlgorithmParameterSpec algorithmParameterSpec;

	/**
	 * 使用算法构造
	 * <p>
	 * 默认生成随机密钥，因此记得调用 {@link #getSecretKey} 获取密钥
	 *
	 * @param algorithm 算法
	 */
	public SymmetricCrypto(String algorithm) {
		this(algorithm, (byte[]) null);
	}

	/**
	 * 使用算法和 {@link Provider} 构造
	 * <p>
	 * 默认生成随机密钥，因此记得调用 {@link #getSecretKey} 获取密钥
	 *
	 * @param algorithm 密钥算法
	 * @param provider  {@link Provider}
	 */
	public SymmetricCrypto(String algorithm, Provider provider) {
		super(algorithm, provider);
		this.secretKey = this.generateSecretKey(algorithm, provider, null);
	}

	/**
	 * 使用算法和密钥构造
	 * <p>
	 * 注意：当密钥为 null 时，会生成一个随机密钥，因此记得调用 {@link #getSecretKey} 获取密钥
	 *
	 * @param algorithm 密钥算法
	 * @param key       密钥
	 */
	public SymmetricCrypto(String algorithm, byte[] key) {
		super(algorithm);
		this.secretKey = this.generateSecretKey(algorithm, null, key);
	}

	/**
	 * 使用算法、密钥和 {@link Provider} 构造
	 * <p>
	 * 注意：当密钥为 null 时，会生成一个随机密钥，因此记得调用 {@link #getSecretKey} 获取密钥
	 *
	 * @param algorithm 密钥算法
	 * @param provider  {@link Provider}
	 * @param key       密钥
	 */
	public SymmetricCrypto(String algorithm, Provider provider, byte[] key) {
		super(algorithm, provider);
		this.secretKey = this.generateSecretKey(algorithm, provider, key);
	}

	/**
	 * 使用算法和密钥构造
	 *
	 * @param algorithm 密钥算法
	 * @param secretKey 密钥
	 */
	public SymmetricCrypto(String algorithm, SecretKey secretKey) {
		super(algorithm);
		this.secretKey = secretKey;
	}

	/**
	 * 使用算法、密钥和 {@link Provider} 构造
	 *
	 * @param algorithm 密钥算法
	 * @param provider  {@link Provider}
	 * @param secretKey 密钥
	 */
	public SymmetricCrypto(String algorithm, Provider provider, SecretKey secretKey) {
		super(algorithm, provider);
		this.secretKey = secretKey;
	}

	/**
	 * 使用 {@link Cipher} 和密钥构造
	 *
	 * @param cipher    {@link Cipher}
	 * @param secretKey 密钥
	 */
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

	public void setIv(byte[] iv) {
		AlgorithmParameterSpec algorithmParameterSpec = Objects.isNull(iv) ? null : new IvParameterSpec(iv);
		this.setAlgorithmParameterSpec(algorithmParameterSpec);
	}

	@SneakyThrows
	@Override
	protected void initMode(CipherMode cipherMode) {
		int mode = cipherMode.getMode();
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
