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
import java.util.Objects;

/**
 * 用于构建 {@link SecretKey} 的 Builder。
 * <p>
 * 适用于 AES、DES、DESede、RC2、Blowfish、HmacSHA* 等对称密钥或消息认证密钥生成场景。
 * 支持直接提供密钥字节、按密钥长度生成，或通过单个复合 {@link AlgorithmParameterSpec} 指定初始化参数。
 *
 * @author Zero (cnzeropro@163.com)
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#KeyGenerator">KeyGenerator Algorithms</a>
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#SecretKeyFactory">SecretKeyFactory Algorithms</a>
 * @since 2025/10/17
 */
@Setter
@Accessors(chain = true, fluent = true)
public class SecretKeyBuilder implements Builder<SecretKey, SecretKeyBuilder> {
	/**
	 * 密钥算法。
	 * <p>
	 * 构建时会按 JCA {@link KeyGenerator} 或 {@link SecretKeyFactory} 标准名称解析算法；
	 * 其中以 {@code PBE} 开头的算法优先按口令派生密钥处理。
	 * <table>
	 *     <caption>Java 8 常见对称/认证密钥算法参考</caption>
	 *     <tr>
	 *         <th>算法</th>
	 *         <th>说明</th>
	 *     </tr>
	 *     <tr>
	 *         <td>AES</td>
	 *         <td>当前最常见的分组加密算法，适合作为默认优先选择</td>
	 *     </tr>
	 *     <tr>
	 *         <td>DES</td>
	 *         <td>历史算法，安全性已不足，通常仅用于兼容旧系统</td>
	 *     </tr>
	 *     <tr>
	 *         <td>DESede / TripleDES / 3DES</td>
	 *         <td>DES 的兼容增强版本，已趋于淘汰，通常仅用于存量兼容</td>
	 *     </tr>
	 *     <tr>
	 *         <td>Blowfish</td>
	 *         <td>较老的分组加密算法，仍可见于历史系统，新设计通常优先选择 AES</td>
	 *     </tr>
	 *     <tr>
	 *         <td>RC2</td>
	 *         <td>较老的分组加密算法，主要用于兼容场景</td>
	 *     </tr>
	 *     <tr>
	 *         <td>ARCFOUR / RC4</td>
	 *         <td>流加密算法，已被广泛认为不安全，不建议新场景使用</td>
	 *     </tr>
	 *     <tr>
	 *         <td>HmacMD5 / HmacSHA1 / HmacSHA224 / HmacSHA256 / HmacSHA384 / HmacSHA512</td>
	 *         <td>消息认证码算法族，用于完整性校验与消息认证，不用于数据加密</td>
	 *     </tr>
	 *     <tr>
	 *         <td>PBE*</td>
	 *         <td>基于口令的密钥派生算法族，通常与 {@link #password} 相关方法配合使用</td>
	 *     </tr>
	 * </table>
	 */
	protected final String algorithm;
	/**
	 * 密钥工厂提供者。
	 * <p>
	 * 用于需要通过 {@link SecretKeyFactory} 生成密钥的算法，例如 PBE、DES、DESede。
	 */
	protected Provider secretKeyFactoryProvider;
	/**
	 * 密钥生成器提供者。
	 * <p>
	 * 用于通过 {@link KeyGenerator} 生成随机密钥的算法实现。
	 */
	protected Provider keyGeneratorProvider;
	/**
	 * 密钥字节。
	 * <p>
	 * 与 {@link #keySize} 二选一即可，或者都不配置，由构建器自动生成密钥。
	 */
	protected byte[] key;
	/**
	 * 密钥长度。
	 * <p>
	 * 大于 0 时生效，否则表示使用算法默认长度。
	 * <p>
	 * 与 {@link #algorithmParameterSpec} 互斥；如需指定 PBE、TLS 等复杂参数，请改用单个复合
	 * {@link AlgorithmParameterSpec}。
	 * <p>
	 * 与 {@link #key} 二选一即可，或者都不配置，由构建器自动生成密钥。
	 * <table>
	 *     <caption>Java 8 常见对称算法长度参考</caption>
	 *     <tr>
	 *         <th>密钥算法</th>
	 *         <th>长度参考</th>
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
	 *         <td>3DES/TripleDES/DESede</td>
	 *         <td>112, 168</td>
	 *     </tr>
	 *     <tr>
	 *         <td>SM4</td>
	 *         <td>128</td>
	 *     </tr>
	 *     <tr>
	 *         <td>ARCFOUR(RC4)</td>
	 *         <td>40..1024</td>
	 *     </tr>
	 *     <tr>
	 *         <td>Blowfish</td>
	 *         <td>32..448，且通常要求为 8 的倍数</td>
	 *     </tr>
	 *     <tr>
	 *         <td>RC2</td>
	 *         <td>40..1024</td>
	 *     </tr>
	 *     <tr>
	 *         <td>HmacMD5</td>
	 *         <td>SunJCE 默认 512；无 keySize 限制</td>
	 *     </tr>
	 *     <tr>
	 *         <td>HmacSHA1</td>
	 *         <td>SunJCE 默认 512；无 keySize 限制</td>
	 *     </tr>
	 *     <tr>
	 *         <td>HmacSHA224</td>
	 *         <td>SunJCE 默认 224；无 keySize 限制</td>
	 *     </tr>
	 *     <tr>
	 *         <td>HmacSHA256</td>
	 *         <td>SunJCE 默认 256；无 keySize 限制</td>
	 *     </tr>
	 *     <tr>
	 *         <td>HmacSHA384</td>
	 *         <td>SunJCE 默认 384；无 keySize 限制</td>
	 *     </tr>
	 *     <tr>
	 *         <td>HmacSHA512</td>
	 *         <td>SunJCE 默认 512；无 keySize 限制</td>
	 *     </tr>
	 * </table>
	 */
	protected int keySize;
	/**
	 * 随机数生成器。
	 * <p>
	 * 未指定时由底层 JCA 实现选择默认随机源。
	 */
	protected SecureRandom random;
	/**
	 * 算法参数规格。
	 * <p>
	 * 仅支持单个复合 {@link AlgorithmParameterSpec}；与 {@link #keySize} 互斥。
	 */
	protected AlgorithmParameterSpec algorithmParameterSpec;
	/**
	 * 字符集。
	 * <p>
	 * 如需使用 {@link #password} 相关方法设置密码，请先指定字符集。
	 */
	protected Charset charset = StandardCharsets.UTF_8;

	protected SecretKeyBuilder(String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * 通过 Provider 名称同时设置密钥工厂和密钥生成器提供者。
	 *
	 * @param providerName Provider 名称
	 * @return 当前 Builder
	 */
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

	/**
	 * 使用字符序列密码设置密钥字节。
	 *
	 * @param password 密码字符序列
	 * @return 当前 Builder
	 */
	public SecretKeyBuilder password(CharSequence password) {
		return this.password(password.toString());
	}

	/**
	 * 使用字符串密码设置密钥字节。
	 *
	 * @param password 密码字符串
	 * @return 当前 Builder
	 */
	public SecretKeyBuilder password(String password) {
		return this.password(password.toCharArray());
	}

	/**
	 * 使用字符数组密码设置密钥字节。
	 * <p>
	 * 密码会按当前 {@link #charset} 编码为字节数组，并写入 {@link #key}。
	 *
	 * @param password 密码字符数组
	 * @return 当前 Builder
	 */
	@SneakyThrows
	public SecretKeyBuilder password(char[] password) {
		CharsetEncoder charsetEncoder = charset.newEncoder();
		CharBuffer charBuffer = CharBuffer.wrap(password);
		ByteBuffer byteBuffer = charsetEncoder.encode(charBuffer);
		byte[] bytes = new byte[byteBuffer.remaining()];
		byteBuffer.get(bytes);
		return this.key(bytes);
	}

	/**
	 * 使用指定长度的随机种子初始化 {@link SecureRandom}。
	 *
	 * @param seedBytes 随机种子字节数
	 * @return 当前 Builder
	 */
	public SecretKeyBuilder seedBytes(int seedBytes) {
		byte[] seed = SecureRandom.getSeed(seedBytes);
		return this.seed(seed);
	}

	/**
	 * 使用给定种子初始化 {@link SecureRandom}。
	 *
	 * @param seed 随机种子；为 {@code null} 时使用默认 {@link SecureRandom}
	 * @return 当前 Builder
	 */
	public SecretKeyBuilder seed(byte[] seed) {
		return this.random(Objects.isNull(seed) ? new SecureRandom() : new SecureRandom(seed));
	}

	/**
	 * 设置算法参数规格。
	 * <p>
	 * 该配置与 {@link #keySize} 互斥，仅支持单个复合参数对象，不支持多个独立
	 * {@link AlgorithmParameterSpec} 叠加。
	 *
	 * @param algorithmParameterSpec 算法参数规格
	 * @return 当前 Builder
	 */
	public SecretKeyBuilder algorithmParameterSpec(AlgorithmParameterSpec algorithmParameterSpec) {
		this.algorithmParameterSpec = algorithmParameterSpec;
		return this;
	}

	public static SecretKeyBuilder builder(String algorithm) {
		return new SecretKeyBuilder(algorithm);
	}

	/**
	 * 构建密钥。
	 * <p>
	 * 处理顺序如下：
	 * <ol>
	 *     <li>PBE 算法优先走 {@link SecretKeyFactory}</li>
	 *     <li>DES / DESede 且显式提供 {@link #key} 时按对应 {@link KeySpec} 构建</li>
	 *     <li>其它场景若设置了 {@link #key}，则直接包装为 {@link SecretKeySpec}</li>
	 *     <li>否则按 {@link #algorithmParameterSpec}、{@link #keySize} 或默认配置生成随机密钥</li>
	 * </ol>
	 *
	 * @return 构建后的密钥
	 */
	@Override
	public SecretKey build() {
		Objects.requireNonNull(algorithm, "algorithm cannot be null");
		if (algorithm.startsWith("PBE")) {
			// PBE 密钥
			return generatePBEKey();
		} else if (algorithm.startsWith("DES")) {
			// DES 密钥
			return generateDESKey();
		} else {
			// 其它算法密钥
			return Objects.isNull(key) ? generateKey() : new SecretKeySpec(key, algorithm);
		}
	}

	@SneakyThrows
	protected SecretKey generatePBEKey() {
		char[] password = Objects.isNull(key)
				? new RandomHelper(Objects.isNull(random) ? RandomUtil.getStrongRandom() : random).nextString(32).toCharArray()
				: new String(key, charset).toCharArray();
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
		SecretKeyFactory secretKeyFactory = Objects.isNull(secretKeyFactoryProvider)
				? SecretKeyFactory.getInstance(mainAlgorithm)
				: SecretKeyFactory.getInstance(mainAlgorithm, secretKeyFactoryProvider);
		return secretKeyFactory.generateSecret(keySpec);
	}

	protected void validateKeyGenerationParameters() {
		if (keySize > 0 && Objects.nonNull(algorithmParameterSpec)) {
			throw new IllegalStateException("keySize and algorithmParameterSpec cannot be configured at the same time");
		}
	}

	@SneakyThrows
	public SecretKey generateKey() {
		validateKeyGenerationParameters();
		String mainAlgorithm = KeyUtil.getMainAlgorithm(algorithm);
		KeyGenerator keyGenerator = Objects.isNull(keyGeneratorProvider)
				? KeyGenerator.getInstance(mainAlgorithm)
				: KeyGenerator.getInstance(mainAlgorithm, keyGeneratorProvider);
		if (Objects.nonNull(algorithmParameterSpec)) {
			if (Objects.isNull(random)) {
				keyGenerator.init(algorithmParameterSpec);
			} else {
				keyGenerator.init(algorithmParameterSpec, random);
			}
		} else if (keySize > 0) {
			if (Objects.isNull(random)) {
				keyGenerator.init(keySize);
			} else {
				keyGenerator.init(keySize, random);
			}
		}
		return keyGenerator.generateKey();
	}
}
