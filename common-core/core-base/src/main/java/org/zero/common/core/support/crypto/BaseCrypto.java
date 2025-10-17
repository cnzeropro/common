package org.zero.common.core.support.crypto;

import lombok.Cleanup;
import lombok.Setter;
import lombok.SneakyThrows;
import org.zero.common.core.util.java.io.IoUtil;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/16
 */
public abstract class BaseCrypto implements Crypto {
	protected final Cipher cipher;
	@Setter
	protected SecureRandom secureRandom = new SecureRandom();

	protected BaseCrypto(String algorithm) {
		this.cipher = this.generateCipher(algorithm, null);
	}

	protected BaseCrypto(String algorithm, SecureRandom secureRandom) {
		this.cipher = this.generateCipher(algorithm, null);
		this.secureRandom = secureRandom;
	}

	protected BaseCrypto(String algorithm, String provider) {
		this.cipher = this.generateCipher(algorithm, Security.getProvider(provider));
	}

	protected BaseCrypto(String algorithm, String provider, SecureRandom secureRandom) {
		this.cipher = this.generateCipher(algorithm, Security.getProvider(provider));
		this.secureRandom = secureRandom;
	}

	protected BaseCrypto(String algorithm, Provider provider) {
		this.cipher = this.generateCipher(algorithm, provider);
	}

	protected BaseCrypto(String algorithm, Provider provider, SecureRandom secureRandom) {
		this.cipher = this.generateCipher(algorithm, provider);
		this.secureRandom = secureRandom;
	}

	protected BaseCrypto(Cipher cipher) {
		this.cipher = cipher;
	}

	protected BaseCrypto(Cipher cipher, SecureRandom secureRandom) {
		this.cipher = cipher;
		this.secureRandom = secureRandom;
	}

	public String getAlgorithm() {
		return cipher.getAlgorithm();
	}

	@SneakyThrows
	protected Cipher generateCipher(String algorithm, Provider provider) {
		return Objects.isNull(provider) ? Cipher.getInstance(algorithm) : Cipher.getInstance(algorithm, provider);
	}

	protected abstract void initMode(int mode);

	@SneakyThrows
	@Override
	public void encrypt(InputStream inputStream, OutputStream outputStream) {
		this.initMode(Cipher.ENCRYPT_MODE);
		@Cleanup CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
		IoUtil.copy(inputStream, cipherOutputStream);
	}

	@SneakyThrows
	@Override
	public void decrypt(InputStream inputStream, OutputStream outputStream) {
		this.initMode(Cipher.DECRYPT_MODE);
		@Cleanup CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
		IoUtil.copy(cipherInputStream, outputStream);
	}
}
