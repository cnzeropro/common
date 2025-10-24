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
 * @author Zero (cnzeropro@163.com)
 * @see <a href="https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#keypairgenerator-algorithms">KeyPairGenerator Algorithms</a>
 * @since 2025/10/17
 */
@Setter
@Accessors(chain = true, fluent = true)
public class KeyPairBuilder implements Builder<KeyPair, KeyPairBuilder> {
	/**
	 * SM2 默认曲线
	 */
	public static final String SM2_DEFAULT_CURVE = "sm2p256v1";

	/**
	 * 密钥算法
	 */
	protected final String algorithm;
	/**
	 * 密钥提供者
	 */
	protected Provider provider;
	/**
	 * 密钥长度
	 * <p>
	 * 大于 0 生效，否则表示使用默认长度
	 * <table>
	 *     <caption>常见算法密钥长度要求</caption>
	 *     <tr>
	 *         <th>密钥算法</th>
	 *         <th>密钥长度</th>
	 *     </tr>
	 *     <tr>
	 *         <td>RSA</td>
	 *         <td>512（不安全）, 1024（基本淘汰）, 2048（当前最小安全）, 3072, 4096, 8192, 16384（技术上限）</td>
	 *     </tr>
	 *     <tr>
	 *         <td>ECC</td>
	 *         <td>
	 *             <ul>
	 *                 <li>secp112r1: 112</li>
	 *                 <li>secp128r1: 128</li>
	 *                 <li>secp160r1: 160</li>
	 *                 <li>secp192r1: 192</li>
	 *                 <li>secp224r1: 224</li>
	 *                 <li>secp256r1: 256</li>
	 *                 <li>secp384r1: 384</li>
	 *                 <li>secp512r1: 512</li>
	 *                 <li>curve25519: 256（实际是253位，但通常称为256位）</li>
	 *                 <li>curve448: 448</li>
	 *             </ul>
	 *         </td>
	 *     </tr>
	 *     <tr>
	 *         <td>DH(Diffie-Hellman)</td>
	 *         <td>512（不安全）, 1024（基本淘汰）, 2048（当前最小安全）, 3072, 4096, 8192</td>
	 *     </tr>
	 *     <tr>
	 *         <td>DSA</td>
	 *         <td>512（不安全）, 768, 1024（基本淘汰）, 2048（当前最小安全）, 3072</td>
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
	protected AlgorithmParameterSpec[] algorithmParameterSpecs = {};

	public KeyPairBuilder(String algorithm) {
		this.algorithm = algorithm;
	}

	public KeyPairBuilder providerName(String providerName) {
		return this.provider(Security.getProvider(providerName));
	}

	public KeyPairBuilder seedBytes(int seedBytes) {
		return this.seed(SecureRandom.getSeed(seedBytes));
	}

	public KeyPairBuilder seed(byte[] seed) {
		return this.random(Objects.isNull(seed) ? new SecureRandom() : new SecureRandom(seed));
	}

	public KeyPairBuilder algorithmParameterSpec(AlgorithmParameterSpec algorithmParameterSpec) {
		return this.algorithmParameterSpecs(algorithmParameterSpec);
	}

	public KeyPairBuilder algorithmParameterSpecs(AlgorithmParameterSpec... algorithmParameterSpecs) {
		this.algorithmParameterSpecs = algorithmParameterSpecs;
		return this;
	}

	public static KeyPairBuilder builder(String algorithm) {
		return new KeyPairBuilder(algorithm);
	}

	@SneakyThrows
	@Override
	public KeyPair build() {
		Objects.requireNonNull(algorithm, "algorithm cannot be null");
		String afterWithAlgorithm = KeyUtil.getAlgorithmAfterWith(algorithm);
		KeyPairGenerator keyPairGenerator = Objects.isNull(provider) ? KeyPairGenerator.getInstance(afterWithAlgorithm) : KeyPairGenerator.getInstance(afterWithAlgorithm, provider);
		if (keySize > 0) {
			// 对于 EC（EllipticCurve）算法，密钥长度有限制，在此使用默认 256
			if ("EC".equalsIgnoreCase(algorithm) && keySize > 256) {
				keySize = 256;
			}
			if (Objects.nonNull(random)) {
				keyPairGenerator.initialize(keySize, random);
			} else {
				keyPairGenerator.initialize(keySize);
			}
		}
		for (AlgorithmParameterSpec algorithmParameterSpec : algorithmParameterSpecs) {
			if (Objects.nonNull(random)) {
				keyPairGenerator.initialize(algorithmParameterSpec, random);
			} else {
				keyPairGenerator.initialize(algorithmParameterSpec);
			}
		}
		return keyPairGenerator.generateKeyPair();
	}
}
