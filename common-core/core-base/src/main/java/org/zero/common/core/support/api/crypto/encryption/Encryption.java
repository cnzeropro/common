package org.zero.common.core.support.api.crypto.encryption;

import org.zero.common.core.support.api.crypto.CryptoFactory;
import org.zero.common.core.support.api.crypto.StringMode;
import org.zero.common.core.support.api.crypto.strategy.DefaultCrypto;
import org.zero.common.core.support.api.crypto.supplier.CryptoConfigSupplier;
import org.zero.common.core.support.api.crypto.supplier.KeySupplier;
import org.zero.common.core.support.crypto.Encryptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加密注解
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/11/29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Encryption {
	/**
	 * 是否启用
	 */
	boolean enable() default true;

	/**
	 * 加密器
	 * <ul>
	 *     <li>如果想单独实现，请确保存在无参构造</li>
	 *     <li>如果想使用以下属性（如：{@link #algorithm()}、{@link #keyString()} 等等），请确保有且仅有一个 {@link org.zero.common.core.support.api.crypto.CryptoContext} 参数的有参构造</li>
	 *     <li>如果想沿用 {@link DefaultCrypto}，请使用 {@link CryptoFactory#put(String, Class)} 注册，{@code cryptoClass} 对应的类要求如上</li>
	 * </ul>
	 */
	Class<? extends Encryptor> encryptor() default Encryptor.class;

	/**
	 * 算法
	 * <ul>
	 *     <li>如果为空，则使用默认配置 {@link org.zero.common.core.support.api.crypto.CryptoProperties#DEFAULT_ALGORITHM}</li>
	 * </ul>
	 */
	String algorithm() default "";

	/**
	 * 密钥字符串
	 * <ul>
	 *     <li>请指定 {@linkplain #keyStringMode() 密钥字符串模式} 来表明其如何转成密钥，默认：{@link org.zero.common.core.support.api.crypto.CryptoProperties.EncryptionProperties#keyStringMode}</li>
	 *     <li>如果为空，则默认使用 {@linkplain KeySupplier 密钥提供者} 获取密钥</li>
	 *     <li>如果算法是非对称加密算法，此处为公钥</li>
	 * </ul>
	 */
	String keyString() default "";

	/**
	 * 密钥字符串模式
	 * <ul>
	 *     <li>用于将密钥从字符串转成 byte 数组，默认：{@link org.zero.common.core.support.api.crypto.CryptoProperties.EncryptionProperties#keyStringMode}</li>
	 * </ul>
	 *
	 * @see StringMode#toBytes(String)
	 */
	Class<? extends StringMode> keyStringMode() default StringMode.class;

	/**
	 * 密钥提供者
	 * <ul>
	 *     <li>如果算法是非对称加密算法，此处为公钥提供者</li>
	 * </ul>
	 */
	Class<? extends KeySupplier> keyProvider() default KeySupplier.class;

	/**
	 * 源字符串模式
	 * <ul>
	 *     <li>如果待加密对象是字符串，则使用该指定模式将其从字符串转成 byte 数组，默认：{@link org.zero.common.core.support.api.crypto.CryptoProperties.EncryptionProperties#sourceStringMode}</li>
	 * </ul>
	 *
	 * @see StringMode#toBytes(String)
	 */
	Class<? extends StringMode> sourceStringMode() default StringMode.class;

	/**
	 * 目标字符串模式
	 * <ul>
	 *     <li>如果加密后的转换对象是字符串，则使用该指定模式将其从 byte 数组转成字符串，默认：{@link org.zero.common.core.support.api.crypto.CryptoProperties.EncryptionProperties#targetStringMode}</li>
	 * </ul>
	 *
	 * @see StringMode#toString(byte[])
	 */
	Class<? extends StringMode> targetStringMode() default StringMode.class;

	/**
	 * 配置提供者
	 * <ul>
	 *     <li>提供额外配置，如：AES 算法可能需要 iv 等等</li>
	 * </ul>
	 *
	 * @see org.zero.common.core.support.api.crypto.strategy.AesCrypto
	 * @see org.zero.common.core.support.api.crypto.strategy.RsaCrypto
	 */
	Class<? extends CryptoConfigSupplier> configSupplier() default CryptoConfigSupplier.class;
}
