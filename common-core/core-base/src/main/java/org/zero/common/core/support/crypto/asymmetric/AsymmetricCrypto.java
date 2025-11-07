package org.zero.common.core.support.crypto.asymmetric;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.zero.common.core.extension.java.security.KeyPairBuilder;
import org.zero.common.core.support.crypto.BaseCrypto;
import org.zero.common.core.support.crypto.CipherMode;
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
 * 非对称加密算法
 *
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
	 * RSA 算法此值可选，取决于具体的 RSA 操作模式和填充方案。如：OAEP 填充模式需要指定 {@link javax.crypto.spec.OAEPParameterSpec}；PSS 签名模式需要指定 {@link java.security.spec.PSSParameterSpec}<br>
	 * 其他如 EC/ECDSA、DSA、DH(Diffie-Hellman)、SM2 等等都必须指定
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

	/**
	 * 使用算法构造
	 * <p>
	 * 默认生成随机密钥对，因此记得调用 {@link #getPublicKey} 获取公钥或者调用 {@link #getPrivateKey} 获取私钥
	 *
	 * @param algorithm 算法
	 */
	public AsymmetricCrypto(String algorithm) {
		super(algorithm);
		KeyPair keyPair = this.generateKeyPair(algorithm, null);
		this.publicKey = keyPair.getPublic();
		this.privateKey = keyPair.getPrivate();
	}

	/**
	 * 使用算法和 {@link Provider} 构造
	 * <p>
	 * 默认生成随机密钥对，因此记得调用 {@link #getPublicKey} 获取公钥或者调用 {@link #getPrivateKey} 获取私钥
	 *
	 * @param algorithm 算法
	 * @param provider  {@link Provider}
	 */
	public AsymmetricCrypto(String algorithm, Provider provider) {
		super(algorithm, provider);
		KeyPair keyPair = this.generateKeyPair(algorithm, provider);
		this.publicKey = keyPair.getPublic();
		this.privateKey = keyPair.getPrivate();
	}

	/**
	 * 使用算法和公钥构造
	 * <p>
	 * 注意：此构造实例只能用来加密
	 *
	 * @param algorithm 算法
	 * @param publicKey 公钥
	 */
	public AsymmetricCrypto(String algorithm, PublicKey publicKey) {
		super(algorithm);
		this.publicKey = publicKey;
	}

	/**
	 * 使用算法、公钥和 {@link Provider} 构造
	 * <p>
	 * 注意：此构造实例只能用来加密
	 *
	 * @param algorithm 算法
	 * @param provider  {@link Provider}
	 * @param publicKey 公钥
	 */
	public AsymmetricCrypto(String algorithm, Provider provider, PublicKey publicKey) {
		super(algorithm, provider);
		this.publicKey = publicKey;
	}

	/**
	 * 使用算法和证书构造
	 * <p>
	 * 注意：此构造实例只能用来加密
	 *
	 * @param algorithm   算法
	 * @param certificate 证书
	 */
	public AsymmetricCrypto(String algorithm, Certificate certificate) {
		super(algorithm);
		this.certificate = certificate;
	}

	/**
	 * 使用算法、证书和 {@link Provider} 构造
	 * <p>
	 * 注意：此构造实例只能用来加密
	 *
	 * @param algorithm   算法
	 * @param provider    {@link Provider}
	 * @param certificate 证书
	 */
	public AsymmetricCrypto(String algorithm, Provider provider, Certificate certificate) {
		super(algorithm, provider);
		this.certificate = certificate;
	}

	/**
	 * 使用算法和私钥构造
	 * <p>
	 * 注意：此构造实例只能用来解密
	 *
	 * @param algorithm  算法
	 * @param privateKey 私钥
	 */
	public AsymmetricCrypto(String algorithm, PrivateKey privateKey) {
		super(algorithm);
		this.privateKey = privateKey;
	}

	/**
	 * 使用算法、私钥和 {@link Provider} 构造
	 * <p>
	 * 注意：此构造实例只能用来解密
	 *
	 * @param algorithm  算法
	 * @param provider   {@link Provider}
	 * @param privateKey 私钥
	 */
	public AsymmetricCrypto(String algorithm, Provider provider, PrivateKey privateKey) {
		super(algorithm, provider);
		this.privateKey = privateKey;
	}

	/**
	 * 使用算法和密钥对构造
	 *
	 * @param algorithm 算法
	 * @param keyPair   密钥对
	 */
	public AsymmetricCrypto(String algorithm, KeyPair keyPair) {
		super(algorithm);
		this.publicKey = keyPair.getPublic();
		this.privateKey = keyPair.getPrivate();
	}

	/**
	 * 使用算法、密钥对和 {@link Provider} 构造
	 *
	 * @param algorithm 算法
	 * @param provider  {@link Provider}
	 * @param keyPair   密钥对
	 */
	public AsymmetricCrypto(String algorithm, Provider provider, KeyPair keyPair) {
		super(algorithm, provider);
		this.publicKey = keyPair.getPublic();
		this.privateKey = keyPair.getPrivate();
	}

	/**
	 * 使用算法、公钥和私钥构造
	 * <p>
	 * 其中公钥或私钥可以为 null，表示该实例只能用来加密或解密
	 *
	 * @param algorithm  算法
	 * @param publicKey  公钥
	 * @param privateKey 私钥
	 */
	public AsymmetricCrypto(String algorithm, byte[] publicKey, byte[] privateKey) {
		super(algorithm);
		this.publicKey = this.generatePublicKey(algorithm, null, publicKey);
		this.privateKey = this.generatePrivateKey(algorithm, null, privateKey);
	}

	/**
	 * 使用算法、公钥、私钥和 {@link Provider} 构造
	 *
	 * @param algorithm  算法
	 * @param provider   {@link Provider}
	 * @param publicKey  公钥
	 * @param privateKey 私钥
	 */
	public AsymmetricCrypto(String algorithm, Provider provider, byte[] publicKey, byte[] privateKey) {
		super(algorithm, provider);
		this.publicKey = this.generatePublicKey(algorithm, provider, publicKey);
		this.privateKey = this.generatePrivateKey(algorithm, provider, privateKey);
	}

	/**
	 * 使用算法、公钥和私钥构造
	 * <p>
	 * 其中公钥或私钥可以为 null，表示该实例只能用来加密或解密
	 *
	 * @param algorithm  算法
	 * @param publicKey  公钥
	 * @param privateKey 私钥
	 */
	public AsymmetricCrypto(String algorithm, PublicKey publicKey, PrivateKey privateKey) {
		super(algorithm);
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	/**
	 * 使用算法、公钥、私钥和 {@link Provider} 构造
	 * <p>
	 * 其中公钥或私钥可以为 null，表示该实例只能用来加密或解密
	 *
	 * @param algorithm  算法
	 * @param provider   {@link Provider}
	 * @param publicKey  公钥
	 * @param privateKey 私钥
	 */
	public AsymmetricCrypto(String algorithm, Provider provider, PublicKey publicKey, PrivateKey privateKey) {
		super(algorithm, provider);
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	/**
	 * 使用 {@link Cipher}、公钥和私钥构造
	 * <p>
	 * 其中公钥或私钥可以为 null，表示该实例只能用来加密或解密
	 *
	 * @param cipher     {@link Cipher}
	 * @param publicKey  公钥
	 * @param privateKey 私钥
	 */
	public AsymmetricCrypto(Cipher cipher, PublicKey publicKey, PrivateKey privateKey) {
		super(cipher);
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	public PublicKey getPublicKey() {
		return Objects.nonNull(certificate) ? certificate.getPublicKey() : publicKey;
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
	protected void initMode(CipherMode cipherMode) {
		int mode = cipherMode.getMode();
		if (cipherMode == CipherMode.ENCRYPT || cipherMode == CipherMode.WRAP) {
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
		} else if (cipherMode == CipherMode.DECRYPT || cipherMode == CipherMode.UNWRAP) {
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
