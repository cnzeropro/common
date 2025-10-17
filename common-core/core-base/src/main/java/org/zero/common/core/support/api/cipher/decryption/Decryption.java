package org.zero.common.core.support.api.cipher.decryption;

import org.zero.common.core.support.api.cipher.CodecProperties;
import org.zero.common.core.support.api.cipher.StringMode;
import org.zero.common.core.support.api.cipher.supplier.KeySupplier;
import org.zero.common.core.support.api.cipher.supplier.NonKeySupplier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/11/29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface Decryption {
	/**
	 * 是否启用
	 */
	boolean enable() default true;

	/**
	 * 算法
	 * <p>
	 * 如果为空，则使用默认配置
	 *
	 * @see CodecProperties#defaultConfigKey
	 * @see CodecProperties#encryptionConfig
	 */
	String algorithm() default "";

	/**
	 * 密钥
	 * <p>
	 * <ul>
	 *     <li>如果为空，则默认使用 {@linkplain KeySupplier 密钥提供者} 获取密钥</li>
	 *     <li>如果是非对称加密，此处为私钥</li>
	 * </ul>
	 */
	String key() default "";

	/**
	 * 密钥提供者
	 */
	Class<? extends KeySupplier> keyProvider() default NonKeySupplier.class;

	/**
	 * 字符串模式
	 */
	StringMode stringMode() default StringMode.HEX;
}
