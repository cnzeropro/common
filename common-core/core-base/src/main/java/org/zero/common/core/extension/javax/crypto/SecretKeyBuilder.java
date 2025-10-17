package org.zero.common.core.extension.javax.crypto;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.zero.common.core.extension.java.lang.Builder;
import org.zero.common.core.util.java.util.RandomUtil;
import org.zero.common.core.util.javax.crypto.KeyUtil;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.KeySpec;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/17
 */
@Setter
@Accessors(chain = true, fluent = true)
public class SecretKeyBuilder implements Builder<SecretKey, SecretKeyBuilder> {
	protected String algorithm;
	protected Provider provider;
	protected byte[] key;
	/**
	 * 密钥长度
	 * <p>
	 * 默认为 0，表示使用默认长度
	 */
	protected int keySize;
	protected SecureRandom random;
	protected Charset charset = StandardCharsets.UTF_8;

	public SecretKeyBuilder providerName(String providerName) {
		return this.provider(Security.getProvider(providerName));
	}

	public SecretKeyBuilder password(CharSequence password) {
		return this.password(password.toString());
	}

	public SecretKeyBuilder password(String password) {
		return this.password(password.toCharArray());
	}

	@SneakyThrows
	public SecretKeyBuilder password(char[] password) {
		CharsetEncoder charsetEncoder = charset.newEncoder();
		CharBuffer charBuffer = CharBuffer.wrap(password);
		ByteBuffer byteBuffer = charsetEncoder.encode(charBuffer);
		byte[] bytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytes);
		return this.key(bytes);
	}

	public static SecretKeyBuilder builder() {
		return new SecretKeyBuilder();
	}

	@Override
	public SecretKey build() {
		if (algorithm.startsWith("PBE")) {
			// PBE密钥
			return generatePBEKey();
		} else if (algorithm.startsWith("DES")) {
			// DES密钥
			return generateDESKey();
		} else {
			// 其它算法密钥
			return Objects.isNull(key) ? generateKey() : new SecretKeySpec(key, algorithm);
		}
	}

	@SneakyThrows
	public SecretKey generatePBEKey() {
		char[] password = Objects.isNull(key) ? RandomUtil.randomString(32).toCharArray() : new String(key, charset).toCharArray();
		KeySpec keySpec = new PBEKeySpec(password);
		return generateKey(keySpec);
	}

	@SneakyThrows
	public SecretKey generateDESKey() {
		if (Objects.isNull(key)) {
			return generateKey();
		}
		KeySpec keySpec = algorithm.startsWith("DESede") ? new DESedeKeySpec(key) : new DESKeySpec(key);
		return generateKey(keySpec);
	}

	@SneakyThrows
	public SecretKey generateKey() {
		String mainAlgorithm = KeyUtil.getMainAlgorithm(algorithm);
		final KeyGenerator keyGenerator = Objects.isNull(provider) ? KeyGenerator.getInstance(mainAlgorithm) : KeyGenerator.getInstance(mainAlgorithm, provider);
		// 对于 AES 的密钥，除非指定，否则强制使用128位
		if (keySize <= 0 && "AES".equals(mainAlgorithm)) {
			keySize = 128;
		}
		if (keySize > 0) {
			if (Objects.isNull(random)) {
				keyGenerator.init(keySize);
			} else {
				keyGenerator.init(keySize, random);
			}
		}
		return keyGenerator.generateKey();
	}

	@SneakyThrows
	protected SecretKey generateKey(KeySpec keySpec) {
		SecretKeyFactory secretKeyFactory = Objects.isNull(provider) ? SecretKeyFactory.getInstance(algorithm) : SecretKeyFactory.getInstance(KeyUtil.getMainAlgorithm(algorithm), provider);
		return secretKeyFactory.generateSecret(keySpec);
	}
}
