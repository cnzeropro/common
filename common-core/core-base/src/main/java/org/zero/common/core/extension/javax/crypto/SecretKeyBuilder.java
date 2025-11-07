package org.zero.common.core.extension.javax.crypto;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.zero.common.core.extension.java.lang.Builder;
import org.zero.common.core.util.java.util.RandomHelper;
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
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#KeyGenerator">KeyGenerator Algorithms</a>
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#SecretKeyFactory">SecretKeyFactory Algorithms</a>
 * @since 2025/10/17
 */
@Setter
@Accessors(chain = true, fluent = true)
public class SecretKeyBuilder implements Builder<SecretKey, SecretKeyBuilder> {
	/**
	 * 密钥算法
	 */
	protected final String algorithm;
	/**
	 * 密钥工厂提供者
	 */
	protected Provider secretKeyFactoryProvider;
	/**
	 * 密钥生成器提供者
	 */
	protected Provider keyGeneratorProvider;
	/**
	 * 密钥
	 * <p>
	 * 和长度二选一即可，或者都不配置，使用自动生成的密钥
	 */
	protected byte[] key;
	/**
	 * 密钥长度
	 * <p>
	 * 大于 0 生效，否则表示使用默认长度
	 * <p>
	 * 和密钥长度二选一即可，或者都不配置，使用自动生成的密钥
	 *
	 * <table>
	 *     <caption>常见算法密钥长度要求</caption>
	 *     <tr>
	 *         <th>密钥算法</th>
	 *         <th>密钥长度</th>
	 *     </tr>
	 *     <tr>
	 *         <td>AES</td>
	 *         <td>128, 192, 256</td>
	 *     </tr>
	 *     <tr>
	 *         <td>DES</td>
	 *         <td>56</td>
	 *     </tr>
	 *     <tr>
	 *         <td>3DES\TripleDES\DESede</td>
	 *         <td>112, 168</td>
	 *     </tr>
	 *     <tr>
	 *         <td>SM4</td>
	 *         <td>128</td>
	 *     </tr>
	 * </table>
	 */
	protected int keySize;
	/**
	 * 随机数生成器
	 */
	protected SecureRandom random;
	/**
	 * 密钥参数规格
	 */
	protected Collection<AlgorithmParameterSpec> algorithmParameterSpecs = new ArrayList<>();
	/**
	 * 字符集
	 * <p>
	 * 如需使用 {@link #password} 相关方法设置密码，请在这之前指定字符集
	 */
	protected Charset charset = StandardCharsets.UTF_8;

	protected SecretKeyBuilder(String algorithm) {
		this.algorithm = algorithm;
	}

	public SecretKeyBuilder providerName(String providerName) {
		Provider provider = Security.getProvider(providerName);
		return this.provider(provider);
	}

	public SecretKeyBuilder provider(Provider provider) {
		this.secretKeyFactoryProvider(provider);
		return this.keyGeneratorProvider(provider);
	}

	public SecretKeyBuilder secretKeyFactoryProviderName(String secretKeyFactoryProviderName) {
		Provider provider = Security.getProvider(secretKeyFactoryProviderName);
		return this.secretKeyFactoryProvider(provider);
	}

	public SecretKeyBuilder keyGeneratorProviderName(String keyGeneratorProviderName) {
		Provider provider = Security.getProvider(keyGeneratorProviderName);
		return this.keyGeneratorProvider(provider);
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

	public SecretKeyBuilder seedBytes(int seedBytes) {
		byte[] seed = SecureRandom.getSeed(seedBytes);
		return this.seed(seed);
	}

	public SecretKeyBuilder seed(byte[] seed) {
		return this.random(Objects.isNull(seed) ? new SecureRandom() : new SecureRandom(seed));
	}

	public SecretKeyBuilder algorithmParameterSpec(AlgorithmParameterSpec algorithmParameterSpec) {
		return this.algorithmParameterSpecs(algorithmParameterSpec);
	}

	public SecretKeyBuilder algorithmParameterSpecs(AlgorithmParameterSpec... algorithmParameterSpecs) {
		return this.algorithmParameterSpecs(Arrays.asList(algorithmParameterSpecs));
	}

	public SecretKeyBuilder algorithmParameterSpecs(Collection<AlgorithmParameterSpec> algorithmParameterSpecs) {
		this.algorithmParameterSpecs.addAll(algorithmParameterSpecs);
		return this;
	}

	public static SecretKeyBuilder builder(String algorithm) {
		return new SecretKeyBuilder(algorithm);
	}

	@Override
	public SecretKey build() {
		Objects.requireNonNull(algorithm, "algorithm cannot be null");
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
	protected SecretKey generatePBEKey() {
		char[] password = Objects.isNull(key) ? new RandomHelper(Objects.isNull(random) ? RandomUtil.getStrongRandom() : random).nextString(32).toCharArray() : new String(key, charset).toCharArray();
		KeySpec keySpec = new PBEKeySpec(password);
		return this.generateKey(keySpec);
	}

	@SneakyThrows
	protected SecretKey generateDESKey() {
		if (Objects.isNull(key)) {
			return generateKey();
		}
		KeySpec keySpec = algorithm.startsWith("DESede") ? new DESedeKeySpec(key) : new DESKeySpec(key);
		return this.generateKey(keySpec);
	}

	@SneakyThrows
	protected SecretKey generateKey(KeySpec keySpec) {
		String mainAlgorithm = KeyUtil.getMainAlgorithm(algorithm);
		SecretKeyFactory secretKeyFactory = Objects.isNull(secretKeyFactoryProvider) ? SecretKeyFactory.getInstance(mainAlgorithm) : SecretKeyFactory.getInstance(mainAlgorithm, secretKeyFactoryProvider);
		return secretKeyFactory.generateSecret(keySpec);
	}

	@SneakyThrows
	public SecretKey generateKey() {
		String mainAlgorithm = KeyUtil.getMainAlgorithm(algorithm);
		KeyGenerator keyGenerator = Objects.isNull(keyGeneratorProvider) ? KeyGenerator.getInstance(mainAlgorithm) : KeyGenerator.getInstance(mainAlgorithm, keyGeneratorProvider);
		if (keySize > 0) {
			if (Objects.isNull(random)) {
				keyGenerator.init(keySize);
			} else {
				keyGenerator.init(keySize, random);
			}
		}
		for (AlgorithmParameterSpec algorithmParameterSpec : algorithmParameterSpecs) {
			if (Objects.isNull(random)) {
				keyGenerator.init(algorithmParameterSpec);
			} else {
				keyGenerator.init(algorithmParameterSpec, random);
			}
		}
		return keyGenerator.generateKey();
	}
}
