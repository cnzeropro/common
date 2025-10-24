package org.zero.common.core.support.crypto.asymmetric;

import lombok.SneakyThrows;
import org.zero.common.core.util.javax.crypto.KeyUtil;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/20
 */
public class RSA extends AsymmetricCrypto {
	public static final String ALGORITHM = "RSA/ECB/PKCS1Padding";

	public RSA() {
		super(ALGORITHM);
	}

	public RSA(Provider provider) {
		super(ALGORITHM, provider);
	}

	public RSA(String algorithm) {
		super(algorithm);
	}

	public RSA(String algorithm, Provider provider) {
		super(algorithm, provider);
	}

	public RSA(PublicKey publicKey) {
		super(ALGORITHM, publicKey);
	}

	public RSA(Provider provider, PublicKey publicKey) {
		super(ALGORITHM, provider, publicKey);
	}

	public RSA(String algorithm, PublicKey publicKey) {
		super(algorithm, publicKey);
	}

	public RSA(String algorithm, Provider provider, PublicKey publicKey) {
		super(algorithm, provider, publicKey);
	}

	public RSA(PrivateKey privateKey) {
		super(ALGORITHM, privateKey);
	}

	public RSA(Provider provider, PrivateKey privateKey) {
		super(ALGORITHM, provider, privateKey);
	}

	public RSA(String algorithm, PrivateKey privateKey) {
		super(algorithm, privateKey);
	}

	public RSA(String algorithm, Provider provider, PrivateKey privateKey) {
		super(ALGORITHM, provider, privateKey);
	}

	public RSA(BigInteger modulus, BigInteger publicExponent, BigInteger privateExponent) {
		this(ALGORITHM, modulus, publicExponent, privateExponent);
	}

	public RSA(Provider provider, BigInteger modulus, BigInteger publicExponent, BigInteger privateExponent) {
		this(ALGORITHM, provider, modulus, publicExponent, privateExponent);
	}

	public RSA(String algorithm, BigInteger modulus, BigInteger publicExponent, BigInteger privateExponent) {
		// Java 语言限制，因此先设置公私钥为 null 再重新赋值
		super(algorithm, (PublicKey) null, null);
		this.publicKey = this.generatePublicKey(algorithm, null, modulus, publicExponent);
		this.privateKey = this.generatePrivateKey(algorithm, null, modulus, privateExponent);
	}

	public RSA(String algorithm, Provider provider, BigInteger modulus, BigInteger publicExponent, BigInteger privateExponent) {
		// Java 语言限制，因此先设置公私钥为 null 再重新赋值
		super(algorithm, provider, (PublicKey) null, null);
		this.publicKey = this.generatePublicKey(algorithm, provider, modulus, publicExponent);
		this.privateKey = this.generatePrivateKey(algorithm, provider, modulus, privateExponent);
	}

	public RSA(byte[] publicKey, byte[] privateKey) {
		super(ALGORITHM, publicKey, privateKey);
	}

	public RSA(Provider provider, byte[] publicKey, byte[] privateKey) {
		super(ALGORITHM, provider, publicKey, privateKey);
	}

	public RSA(String algorithm, byte[] publicKey, byte[] privateKey) {
		super(algorithm, publicKey, privateKey);
	}

	public RSA(String algorithm, Provider provider, byte[] publicKey, byte[] privateKey) {
		super(ALGORITHM, provider, publicKey, privateKey);
	}

	public RSA(PublicKey publicKey, PrivateKey privateKey) {
		super(ALGORITHM, publicKey, privateKey);
	}

	public RSA(Provider provider, PublicKey publicKey, PrivateKey privateKey) {
		super(ALGORITHM, provider, publicKey, privateKey);
	}

	public RSA(String algorithm, PublicKey publicKey, PrivateKey privateKey) {
		super(algorithm, publicKey, privateKey);
	}

	public RSA(String algorithm, Provider provider, PublicKey publicKey, PrivateKey privateKey) {
		super(algorithm, provider, publicKey, privateKey);
	}

	@SneakyThrows
	public PublicKey generatePublicKey(String algorithm, Provider provider, BigInteger modulus, BigInteger publicExponent) {
		String afterWithAlgorithm = KeyUtil.getAlgorithmAfterWith(algorithm);
		KeyFactory keyFactory = Objects.isNull(provider) ? KeyFactory.getInstance(afterWithAlgorithm) : KeyFactory.getInstance(afterWithAlgorithm, provider);
		KeySpec keySpec = new RSAPublicKeySpec(modulus, publicExponent);
		return keyFactory.generatePublic(keySpec);
	}

	@SneakyThrows
	public PrivateKey generatePrivateKey(String algorithm, Provider provider, BigInteger modulus, BigInteger privateExponent) {
		String afterWithAlgorithm = KeyUtil.getAlgorithmAfterWith(algorithm);
		KeyFactory keyFactory = Objects.isNull(provider) ? KeyFactory.getInstance(afterWithAlgorithm) : KeyFactory.getInstance(afterWithAlgorithm, provider);
		KeySpec keySpec = new RSAPrivateKeySpec(modulus, privateExponent);
		return keyFactory.generatePrivate(keySpec);
	}
}
