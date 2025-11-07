package org.zero.common.core.support.crypto;

import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.zero.common.core.util.java.io.IoUtil;
import org.zero.common.core.util.java.util.RandomUtil;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html">Java Cryptography Architecture (JCA) Standard Algorithm Name Documentation for JDK 8</a>
 * @see <a href="https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html">Java Security Standard Algorithm Names</a>
 * @since 2025/10/16
 */
public abstract class BaseCrypto implements Crypto {
	@Getter
	protected final Cipher cipher;
	@Setter
	protected SecureRandom secureRandom = RandomUtil.getStrongRandom();

	protected BaseCrypto(String algorithm) {
		this.cipher = this.generateCipher(algorithm, null);
	}

	protected BaseCrypto(String algorithm, String provider) {
		this.cipher = this.generateCipher(algorithm, Security.getProvider(provider));
	}

	protected BaseCrypto(String algorithm, Provider provider) {
		this.cipher = this.generateCipher(algorithm, provider);
	}

	protected BaseCrypto(Cipher cipher) {
		this.cipher = cipher;
	}

	public String getAlgorithm() {
		return cipher.getAlgorithm();
	}

	@SneakyThrows
	protected Cipher generateCipher(String algorithm, Provider provider) {
		return Objects.isNull(provider) ? Cipher.getInstance(algorithm) : Cipher.getInstance(algorithm, provider);
	}

	protected abstract void initMode(CipherMode mode);

	@SneakyThrows
	@Override
	public void encrypt(InputStream inputStream, OutputStream outputStream) {
		this.initMode(CipherMode.ENCRYPT);
		@Cleanup CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
		IoUtil.copy(inputStream, cipherOutputStream);
	}

	@SneakyThrows
	@Override
	public void decrypt(InputStream inputStream, OutputStream outputStream) {
		this.initMode(CipherMode.DECRYPT);
		@Cleanup CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
		IoUtil.copy(cipherInputStream, outputStream);
	}

	@SneakyThrows
	public byte[] wrap(Key key) {
		this.initMode(CipherMode.WRAP);
		return cipher.wrap(key);
	}

	@SneakyThrows
	public Key unwrap(byte[] wrappedKey, String wrappedKeyAlgorithm, KeyType wrappedKeyType) {
		this.initMode(CipherMode.UNWRAP);
		return cipher.unwrap(wrappedKey, wrappedKeyAlgorithm, wrappedKeyType.getType());
	}
}
