package org.zero.common.core.support.api.crypto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.zero.common.core.support.api.crypto.strategy.DefaultCrypto;
import org.zero.common.core.support.api.crypto.supplier.EmptyKeySupplier;
import org.zero.common.core.support.api.crypto.supplier.KeySupplier;
import org.zero.common.core.support.crypto.Decryptor;
import org.zero.common.core.support.crypto.Encryptor;
import org.zero.common.core.support.crypto.symmetric.AES;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/24
 */
@Setter
@Getter
@ConfigurationProperties(prefix = CryptoProperties.DEFAULT_PREFIX)
public class CryptoProperties {
	static final String DEFAULT_PREFIX = "system.crypto";
	public static final String DEFAULT_ALGORITHM = AES.ALGORITHM;

	/**
	 * 加密配置
	 */
	private EncryptionProperties encryption = new EncryptionProperties();
	/**
	 * 解密配置
	 */
	private DecryptionProperties decryption = new DecryptionProperties();

	@Getter
	@Setter
	public static class EncryptionProperties {
		/**
		 * 加密器
		 * <ul>
		 *     <li>如果想单独实现，请确保存在无参构造</li>
		 *     <li>如果想使用以下属性（如：{@link #algorithm}、{@link #keyString} 等等），请确保有且仅有一个 {@link org.zero.common.core.support.api.crypto.CryptoContext} 参数的有参构造</li>
		 *     <li>如果想沿用 {@link DefaultCrypto}，请使用 {@link CryptoFactory#put(String, Class)} 注册，{@code cryptoClass} 对应的类要求如上</li>
		 * </ul>
		 */
		private Class<? extends Encryptor> encryptor = DefaultCrypto.class;

		/**
		 * 算法
		 * <ul>
		 *     <li>如果为空，则使用默认配置 {@link CryptoProperties#DEFAULT_ALGORITHM}</li>
		 * </ul>
		 */
		private String algorithm = DEFAULT_ALGORITHM;

		/**
		 * 密钥字符串
		 * <ul>
		 *     <li>请指定 {@linkplain #keyStringMode 密钥字符串模式} 来表明其如何转成密钥，默认：{@link org.zero.common.core.support.api.crypto.CryptoProperties.EncryptionProperties#keyStringMode}</li>
		 *     <li>如果为空，则默认使用 {@linkplain KeySupplier 密钥提供者} 获取密钥</li>
		 *     <li>如果算法是非对称加密算法，此处为公钥</li>
		 * </ul>
		 */
		private String keyString;

		/**
		 * 密钥字符串模式
		 * <ul>
		 *     <li>用于将密钥从字符串转成 byte 数组，默认：{@link StringMode.HexLower}</li>
		 * </ul>
		 *
		 * @see StringMode#toBytes(String)
		 */
		private Class<? extends StringMode> keyStringMode = StringMode.HexLower.class;

		/**
		 * 密钥提供者
		 * <ul>
		 *     <li>如果算法是非对称加密算法，此处为公钥提供者</li>
		 * </ul>
		 */
		private Class<? extends KeySupplier> keyProvider = EmptyKeySupplier.class;

		/**
		 * 源字符串模式
		 * <ul>
		 *     <li>如果待加密对象是字符串，则使用该指定模式将其从字符串转成 byte 数组，默认：{@link StringMode.Utf8}</li>
		 * </ul>
		 *
		 * @see StringMode#toBytes(String)
		 */
		private Class<? extends StringMode> sourceStringMode = StringMode.Utf8.class;

		/**
		 * 目标字符串模式
		 * <ul>
		 *     <li>如果加密后的转换对象是字符串，则使用该指定模式将其从 byte 数组转成字符串，默认：{@link StringMode.UrlSafeBase64AndUtf8}</li>
		 * </ul>
		 *
		 * @see StringMode#toString(byte[])
		 */
		private Class<? extends StringMode> targetStringMode = StringMode.UrlSafeBase64AndUtf8.class;

		/**
		 * 额外配置
		 * <p>
		 * 如：AES 算法可能配置 iv 等等
		 *
		 * @see org.zero.common.core.support.api.crypto.strategy.AesCrypto
		 * @see org.zero.common.core.support.api.crypto.strategy.RsaCrypto
		 */
		private Map<String, Object> properties = new HashMap<>();
	}

	@Getter
	@Setter
	public static class DecryptionProperties {
		/**
		 * 解密器
		 * <ul>
		 *     <li>如果想单独实现，请确保存在无参构造</li>
		 *     <li>如果想使用以下属性（如：{@link #algorithm}、{@link #keyString} 等等），请确保有且仅有一个 {@link org.zero.common.core.support.api.crypto.CryptoContext} 参数的有参构造</li>
		 *     <li>如果想沿用 {@link DefaultCrypto}，请使用 {@link CryptoFactory#put(String, Class)} 注册，{@code cryptoClass} 对应的类要求如上</li>
		 * </ul>
		 */
		private Class<? extends Decryptor> decryptor = DefaultCrypto.class;

		/**
		 * 算法
		 * <ul>
		 *     <li>如果为空，则使用默认配置 {@link #DEFAULT_ALGORITHM}</li>
		 * </ul>
		 */
		private String algorithm = DEFAULT_ALGORITHM;

		/**
		 * 密钥字符串
		 * <ul>
		 *     <li>请指定 {@linkplain #keyStringMode 密钥字符串模式} 来表明其如何转成密钥，默认：{@link StringMode.HexLower}</li>
		 *     <li>如果为空，则默认使用 {@linkplain KeySupplier 密钥提供者} 获取密钥</li>
		 *     <li>如果算法是非对称加密算法，此处为私钥</li>
		 * </ul>
		 */
		private String keyString;

		/**
		 * 密钥字符串模式
		 * <ul>
		 *     <li>用于将密钥从字符串转成 byte 数组，默认：{@link StringMode.HexLower}</li>
		 * </ul>
		 *
		 * @see StringMode#toBytes(String)
		 */
		private Class<? extends StringMode> keyStringMode = StringMode.HexLower.class;

		/**
		 * 密钥提供者
		 * <ul>
		 *     <li>如果算法是非对称加密算法，此处为私钥提供者</li>
		 * </ul>
		 */
		private Class<? extends KeySupplier> keyProvider = EmptyKeySupplier.class;

		/**
		 * 源字符串模式
		 * <ul>
		 *     <li>如果待解密对象是字符串，则使用该指定模式将其从字符串转成 byte 数组，默认：{@link StringMode.UrlSafeBase64AndUtf8}</li>
		 * </ul>
		 *
		 * @see StringMode#toBytes(String)
		 */
		private Class<? extends StringMode> sourceStringMode = StringMode.UrlSafeBase64AndUtf8.class;

		/**
		 * 目标字符串模式
		 * <ul>
		 *     <li>如果解密后的转换对象是字符串，则使用该指定模式将其从 byte 数组转成字符串，默认：{@link StringMode.Utf8}</li>
		 * </ul>
		 *
		 * @see StringMode#toString(byte[])
		 */
		private Class<? extends StringMode> targetStringMode = StringMode.Utf8.class;

		/**
		 * 额外配置
		 * <p>
		 * 如：AES 算法可能配置 iv 等等
		 *
		 * @see org.zero.common.core.support.api.crypto.strategy.AesCrypto
		 * @see org.zero.common.core.support.api.crypto.strategy.RsaCrypto
		 */
		private Map<String, Object> properties = new HashMap<>();
	}
}