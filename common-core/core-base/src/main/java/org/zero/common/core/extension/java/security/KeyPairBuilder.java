package org.zero.common.core.extension.java.security;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.zero.common.core.extension.java.lang.Builder;
import org.zero.common.core.util.javax.crypto.KeyUtil;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Objects;

/**
 * 用于构建 {@link KeyPair} 的 Builder。
 * <p>
 * 适用于 RSA、EC、DH、DSA 等非对称密钥对生成场景；支持通过密钥长度或单个复合
 * {@link AlgorithmParameterSpec} 指定初始化参数。
 *
 * @author Zero (cnzeropro@163.com)
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#KeyPairGenerator">KeyPairGenerator Algorithms</a>
 * @since 2025/10/17
 */
@Setter
@Accessors(chain = true, fluent = true)
public class KeyPairBuilder implements Builder<KeyPair, KeyPairBuilder> {
	/**
	 * SM2 常见默认曲线名称。
	 * <p>
	 * 该曲线依赖具体 Provider 支持，通常需要结合 {@code ECGenParameterSpec} 使用。
	 */
	public static final String SM2_DEFAULT_CURVE = "sm2p256v1";

	/**
	 * 密钥算法。
	 * <p>
	 * 构建时会按 JCA {@link KeyPairGenerator} 标准名称解析算法；若传入形如
	 * {@code SHA256withRSA} 的字符串，会在内部提取主算法部分用于生成密钥对。
	 * <table>
	 *     <caption>Java 8 常见非对称密钥算法参考</caption>
	 *     <tr>
	 *         <th>算法</th>
	 *         <th>说明</th>
	 *     </tr>
	 *     <tr>
	 *         <td>RSA</td>
	 *         <td>最常见的通用非对称算法，可用于加密、解密、签名和验签</td>
	 *     </tr>
	 *     <tr>
	 *         <td>EC</td>
	 *         <td>椭圆曲线密钥对生成算法，常用于 ECDSA、ECDH；精确曲线建议配合 {@code ECGenParameterSpec}</td>
	 *     </tr>
	 *     <tr>
	 *         <td>DSA</td>
	 *         <td>传统数字签名算法，仅用于签名和验签，不用于加密</td>
	 *     </tr>
	 *     <tr>
	 *         <td>DH</td>
	 *         <td>Diffie-Hellman 密钥协商算法，用于生成协商参数所需的密钥对</td>
	 *     </tr>
	 *     <tr>
	 *         <td>DiffieHellman</td>
	 *         <td>{@code DH} 的标准名称别名，部分 Provider 或代码更偏好这一写法</td>
	 *     </tr>
	 * </table>
	 * 部分 Provider 还可能扩展支持 SM2 等算法；这类能力通常不属于 Java 8 标准名称集合。
	 */
	protected final String algorithm;
	/**
	 * 密钥提供者。
	 * <p>
	 * 未指定时使用 JCA 默认 Provider 查找对应算法实现。
	 */
	protected Provider provider;
	/**
	 * 密钥长度。
	 * <p>
	 * 大于 0 时生效，否则表示使用算法默认长度。
	 * <p>
	 * 与 {@link #algorithmParameterSpec} 互斥；如需指定曲线、域参数等复杂配置，请改用单个复合
	 * {@link AlgorithmParameterSpec}。
	 * <table>
	 *     <caption>Java 8 常见非对称算法长度参考</caption>
	 *     <tr>
	 *         <th>密钥算法</th>
	 *         <th>长度参考</th>
	 *     </tr>
	 *     <tr>
	 *         <td>RSA</td>
	 *         <td>512（不安全）, 1024（基本淘汰）, 2048（当前常见最小安全值）, 3072, 4096, 8192, 16384（技术上限）</td>
	 *     </tr>
	 *     <tr>
	 *         <td>EC</td>
	 *         <td>
	 *             <ul>
	 *                 <li>常见位数：192、224、256、384、521</li>
	 *                 <li>常见推荐曲线：secp256r1、secp384r1、secp521r1</li>
	 *                 <li>SM2 常见曲线：sm2p256v1（依赖 Provider 支持）</li>
	 *             </ul>
	 *             如需精确指定曲线，请使用 {@code ECGenParameterSpec} 而不是仅设置 {@code keySize}。
	 *         </td>
	 *     </tr>
	 *     <tr>
	 *         <td>DH(Diffie-Hellman)</td>
	 *         <td>512..2048，且通常要求为 64 的倍数</td>
	 *     </tr>
	 *     <tr>
	 *         <td>DSA</td>
	 *         <td>512..1024（通常为 64 的倍数）或 2048</td>
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

	public KeyPairBuilder(String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * 通过 Provider 名称设置密钥实现提供者。
	 *
	 * @param providerName Provider 名称
	 * @return 当前 Builder
	 */
	public KeyPairBuilder providerName(String providerName) {
		Provider provider = Security.getProvider(providerName);
		return this.provider(provider);
	}

	/**
	 * 使用指定长度的随机种子初始化 {@link SecureRandom}。
	 *
	 * @param seedBytes 随机种子字节数
	 * @return 当前 Builder
	 */
	public KeyPairBuilder seedBytes(int seedBytes) {
		byte[] seed = SecureRandom.getSeed(seedBytes);
		return this.seed(seed);
	}

	/**
	 * 使用给定种子初始化 {@link SecureRandom}。
	 *
	 * @param seed 随机种子；为 {@code null} 时使用默认 {@link SecureRandom}
	 * @return 当前 Builder
	 */
	public KeyPairBuilder seed(byte[] seed) {
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
	public KeyPairBuilder algorithmParameterSpec(AlgorithmParameterSpec algorithmParameterSpec) {
		this.algorithmParameterSpec = algorithmParameterSpec;
		return this;
	}

	public static KeyPairBuilder builder(String algorithm) {
		return new KeyPairBuilder(algorithm);
	}

	/**
	 * 构建密钥对。
	 * <p>
	 * 初始化优先级为：
	 * <ol>
	 *     <li>若设置了 {@link #algorithmParameterSpec}，则按参数规格初始化</li>
	 *     <li>否则若 {@link #keySize} 大于 0，则按密钥长度初始化</li>
	 *     <li>否则使用底层实现默认配置</li>
	 * </ol>
	 *
	 * @return 生成后的密钥对
	 */
	@SneakyThrows
	@Override
	public KeyPair build() {
		Objects.requireNonNull(algorithm, "algorithm cannot be null");
		if (keySize > 0 && Objects.nonNull(algorithmParameterSpec)) {
			throw new IllegalStateException("keySize and algorithmParameterSpec cannot be configured at the same time");
		}
		String afterWithAlgorithm = KeyUtil.getAlgorithmAfterWith(algorithm);
		KeyPairGenerator keyPairGenerator = Objects.isNull(provider)
				? KeyPairGenerator.getInstance(afterWithAlgorithm)
				: KeyPairGenerator.getInstance(afterWithAlgorithm, provider);
		if (Objects.nonNull(algorithmParameterSpec)) {
			if (Objects.nonNull(random)) {
				keyPairGenerator.initialize(algorithmParameterSpec, random);
			} else {
				keyPairGenerator.initialize(algorithmParameterSpec);
			}
		} else if (keySize > 0) {
			if (Objects.nonNull(random)) {
				keyPairGenerator.initialize(keySize, random);
			} else {
				keyPairGenerator.initialize(keySize);
			}
		}
		return keyPairGenerator.generateKeyPair();
	}
}
