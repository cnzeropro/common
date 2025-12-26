package org.zero.common.core.support.api.crypto.encryption;

import org.zero.common.core.support.api.crypto.CryptoFactory;
import org.zero.common.core.support.api.crypto.converter.InputConverter;
import org.zero.common.core.support.api.crypto.converter.OutputConverter;
import org.zero.common.core.support.api.crypto.converter.StringMode;
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
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Encryption {
	/**
	 * 是否启用
	 */
	boolean enable() default true;

	/**
	 * 加密器
	 * <ul>
	 *     <li>不满足 {@linkplain org.zero.common.core.support.api.crypto.CryptoUtil#canInstanced(java.lang.Class) 可实例化} 要求，默认使用 {@linkplain org.zero.common.core.support.api.crypto.CryptoProperties.EncryptionProperties#encryptor 配置值}</li>
	 *     <li>如果想单独实现，请确保存在无参构造</li>
	 *     <li>如果想使用以下属性（如：{@link #algorithm}、{@link #keyString}、{@link #keyStringMode} 等等），请确保有且仅有一个 {@linkplain org.zero.common.core.support.api.crypto.CryptoContext CryptoContext} 类型的参数的有参构造</li>
	 *     <li>如果想沿用 {@link DefaultCrypto}，请使用 {@link CryptoFactory#put(String, Class)} 注册，{@code CryptoClass} 对应的类要求如上</li>
	 * </ul>
	 */
	Class<? extends Encryptor> encryptor() default Encryptor.class;

	/**
	 * 算法
	 * <ul>
	 *     <li>如果为空，则使用 {@link org.zero.common.core.support.api.crypto.CryptoProperties.EncryptionProperties#algorithm}</li>
	 * </ul>
	 */
	String algorithm() default "";

	/**
	 * 密钥字符串
	 * <ul>
	 *     <li>如果为空，则使用 {@link org.zero.common.core.support.api.crypto.CryptoProperties.EncryptionProperties#keyString}，如果还为空，则默认使用 {@linkplain KeySupplier 密钥提供者} 获取密钥</li>
	 *     <li>请指定 {@linkplain #keyStringMode 密钥字符串模式} 来表明其如何转成密钥</li>
	 *     <li>如果算法是非对称加密算法，此处为公钥</li>
	 * </ul>
	 */
	String keyString() default "";

	/**
	 * 密钥字符串模式
	 * <ul>
	 *     <li>用于将密钥从字符串转成字节数组</li>
	 *     <li>不满足 {@linkplain org.zero.common.core.support.api.crypto.CryptoUtil#canInstanced(java.lang.Class) 可实例化} 要求，默认使用 {@linkplain org.zero.common.core.support.api.crypto.CryptoProperties.EncryptionProperties#keyStringMode 配置值}</li>
	 * </ul>
	 *
	 * @see StringMode#toBytes(CharSequence)
	 */
	Class<? extends StringMode> keyStringMode() default StringMode.class;

	/**
	 * 密钥提供者
	 * <ul>
	 *     <li>如果算法是非对称加密算法，此处为公钥提供者</li>
	 *     <li>不满足 {@linkplain org.zero.common.core.support.api.crypto.CryptoUtil#canInstanced(java.lang.Class) 可实例化} 要求，默认使用 {@linkplain org.zero.common.core.support.api.crypto.CryptoProperties.EncryptionProperties#keySupplier 配置值}</li>
	 * </ul>
	 */
	Class<? extends KeySupplier> keySupplier() default KeySupplier.class;

	/**
	 * 源转换器
	 * <ul>
	 *     <li>从源类型转成字节数组</li>
	 *     <li>不满足 {@linkplain org.zero.common.core.support.api.crypto.CryptoUtil#canInstanced(java.lang.Class) 可实例化} 要求，默认使用 {@linkplain org.zero.common.core.support.api.crypto.CryptoProperties.EncryptionProperties#sourceConverter 配置值}</li>
	 * </ul>
	 *
	 * @see InputConverter#toBytes(Object)
	 */
	Class<? extends InputConverter> sourceConverter() default InputConverter.class;

	/**
	 * 目标转换器
	 * <ul>
	 *     <li>从字节数组转成目标类型</li>
	 *     <li>不满足 {@linkplain org.zero.common.core.support.api.crypto.CryptoUtil#canInstanced(java.lang.Class) 可实例化} 要求，默认使用 {@linkplain org.zero.common.core.support.api.crypto.CryptoProperties.EncryptionProperties#targetConverter 配置值}</li>
	 * </ul>
	 *
	 * @see OutputConverter#fromBytes(byte[])
	 */
	Class<? extends OutputConverter> targetConverter() default OutputConverter.class;

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
