package org.zero.common.core.support.crypto.asymmetric;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.zero.common.core.extension.java.security.KeyPairBuilder;
import org.zero.common.core.support.crypto.BaseCrypto;
import org.zero.common.core.util.javax.crypto.KeyUtil;

import javax.crypto.Cipher;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/20
 */
public class AsymmetricCrypto extends BaseCrypto {
	/**
	 * 公钥
	 */
	protected PublicKey publicKey;
	/**
	 * 私钥
	 */
	@Getter
	protected PrivateKey privateKey;
	/**
	 * 证书
	 * <p>
	 * 与公钥二选一个即可
	 */
	protected Certificate certificate;

	/**
	 * 算法参数规范
	 * <p>
	 * RSA 算法此值可选，ECC/ECDSA，DSA，DH，SM2 等等都必须指定
	 */
	@Setter
	protected AlgorithmParameterSpec algorithmParameterSpec;
	/**
	 * 算法参数
	 * <p>
	 * 大部分算法此值皆可选
	 */
	@Setter
	protected AlgorithmParameters algorithmParameters;

	public AsymmetricCrypto(String algorithm) {
		super(algorithm);
		KeyPair keyPair = this.generateKeyPair(algorithm, null);
		this.publicKey = keyPair.getPublic();
		this.privateKey = keyPair.getPrivate();
	}

	public AsymmetricCrypto(String algorithm, Provider provider) {
		super(algorithm, provider);
		KeyPair keyPair = this.generateKeyPair(algorithm, provider);
		this.publicKey = keyPair.getPublic();
		this.privateKey = keyPair.getPrivate();
	}

	/**
	 * 使用公钥构造
	 * <p>
	 * 注意：此构造实例只能用来加密
	 */
	public AsymmetricCrypto(String algorithm, PublicKey publicKey) {
		super(algorithm);
		this.publicKey = publicKey;
	}

	public AsymmetricCrypto(String algorithm, Provider provider, PublicKey publicKey) {
		super(algorithm, provider);
		this.publicKey = publicKey;
	}

	/**
	 * 使用证书构造
	 * <p>
	 * 注意：此构造实例只能用来加密
	 */
	public AsymmetricCrypto(String algorithm, Certificate certificate) {
		super(algorithm);
		this.certificate = certificate;
	}

	public AsymmetricCrypto(String algorithm, Provider provider, Certificate certificate) {
		super(algorithm, provider);
		this.certificate = certificate;
	}

	/**
	 * 使用私钥构造
	 * <p>
	 * 注意：此构造实例只能用来解密
	 */
	public AsymmetricCrypto(String algorithm, PrivateKey privateKey) {
		super(algorithm);
		this.privateKey = privateKey;
	}

	public AsymmetricCrypto(String algorithm, Provider provider, PrivateKey privateKey) {
		super(algorithm, provider);
		this.privateKey = privateKey;
	}

	public AsymmetricCrypto(String algorithm, KeyPair keyPair) {
		super(algorithm);
		this.publicKey = keyPair.getPublic();
		this.privateKey = keyPair.getPrivate();
	}

	public AsymmetricCrypto(String algorithm, Provider provider, KeyPair keyPair) {
		super(algorithm, provider);
		this.publicKey = keyPair.getPublic();
		this.privateKey = keyPair.getPrivate();
	}

	public AsymmetricCrypto(String algorithm, byte[] publicKey, byte[] privateKey) {
		super(algorithm);
		this.publicKey = this.generatePublicKey(algorithm, null, publicKey);
		this.privateKey = this.generatePrivateKey(algorithm, null, privateKey);
	}

	public AsymmetricCrypto(String algorithm, Provider provider, byte[] publicKey, byte[] privateKey) {
		super(algorithm, provider);
		this.publicKey = this.generatePublicKey(algorithm, provider, publicKey);
		this.privateKey = this.generatePrivateKey(algorithm, provider, privateKey);
	}

	public AsymmetricCrypto(String algorithm, PublicKey publicKey, PrivateKey privateKey) {
		super(algorithm);
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	public AsymmetricCrypto(String algorithm, Provider provider, PublicKey publicKey, PrivateKey privateKey) {
		super(algorithm, provider);
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	public AsymmetricCrypto(Cipher cipher, PublicKey publicKey, PrivateKey privateKey) {
		super(cipher);
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	public PublicKey getPublicKey() {
		return Objects.nonNull(publicKey) ? publicKey : Objects.nonNull(certificate) ? certificate.getPublicKey() : null;
	}

	protected KeyPair generateKeyPair(String algorithm, Provider provider) {
		return KeyPairBuilder.builder(algorithm)
			.provider(provider)
			.random(secureRandom)
			.build();
	}

	@SneakyThrows
	protected PublicKey generatePublicKey(String algorithm, Provider provider, byte[] publicKey) {
		if (Objects.isNull(publicKey)) {
			return null;
		}
		String afterWithAlgorithm = KeyUtil.getAlgorithmAfterWith(algorithm);
		KeyFactory keyFactory = Objects.isNull(provider) ? KeyFactory.getInstance(afterWithAlgorithm) : KeyFactory.getInstance(afterWithAlgorithm, provider);
		// 使用 X509 证书规范
		KeySpec keySpec = new X509EncodedKeySpec(publicKey);
		return keyFactory.generatePublic(keySpec);
	}

	@SneakyThrows
	protected PrivateKey generatePrivateKey(String algorithm, Provider provider, byte[] privateKey) {
		if (Objects.isNull(privateKey)) {
			return null;
		}
		String afterWithAlgorithm = KeyUtil.getAlgorithmAfterWith(algorithm);
		KeyFactory keyFactory = Objects.isNull(provider) ? KeyFactory.getInstance(afterWithAlgorithm) : KeyFactory.getInstance(afterWithAlgorithm, provider);
		// 使用 PKCS#8 规范
		KeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
		return keyFactory.generatePrivate(keySpec);
	}

	@SneakyThrows
	@Override
	protected void initMode(int mode) {
		if (mode == Cipher.ENCRYPT_MODE || mode == Cipher.WRAP_MODE) {
			if (Objects.nonNull(publicKey)) {
				if (Objects.nonNull(algorithmParameterSpec)) {
					if (Objects.nonNull(secureRandom)) {
						cipher.init(mode, publicKey, algorithmParameterSpec, secureRandom);
					} else {
						cipher.init(mode, publicKey, algorithmParameterSpec);
					}
				} else if (Objects.nonNull(algorithmParameters)) {
					if (Objects.nonNull(secureRandom)) {
						cipher.init(mode, publicKey, algorithmParameters, secureRandom);
					} else {
						cipher.init(mode, publicKey, algorithmParameters);
					}
				} else {
					if (Objects.nonNull(secureRandom)) {
						cipher.init(mode, publicKey, secureRandom);
					} else {
						cipher.init(mode, publicKey);
					}
				}
			} else if (Objects.nonNull(certificate)) {
				if (Objects.nonNull(secureRandom)) {
					cipher.init(mode, certificate, secureRandom);
				} else {
					cipher.init(mode, certificate);
				}
			}
		} else if (mode == Cipher.DECRYPT_MODE || mode == Cipher.UNWRAP_MODE) {
			if (Objects.nonNull(privateKey)) {
				if (Objects.nonNull(algorithmParameterSpec)) {
					if (Objects.nonNull(secureRandom)) {
						cipher.init(mode, privateKey, algorithmParameterSpec, secureRandom);
					} else {
						cipher.init(mode, privateKey, algorithmParameterSpec);
					}
				} else if (Objects.nonNull(algorithmParameters)) {
					if (Objects.nonNull(secureRandom)) {
						cipher.init(mode, privateKey, algorithmParameters, secureRandom);
					} else {
						cipher.init(mode, privateKey, algorithmParameters);
					}
				} else {
					if (Objects.nonNull(secureRandom)) {
						cipher.init(mode, privateKey, secureRandom);
					} else {
						cipher.init(mode, privateKey);
					}
				}
			} else if (Objects.nonNull(certificate)) {
				if (Objects.nonNull(secureRandom)) {
					cipher.init(mode, certificate, secureRandom);
				} else {
					cipher.init(mode, certificate);
				}
			}
		}
	}
}
