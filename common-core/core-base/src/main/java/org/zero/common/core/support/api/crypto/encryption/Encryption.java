package org.zero.common.core.support.api.crypto.encryption;

import org.zero.common.core.support.api.crypto.CodecProperties;
import org.zero.common.core.support.api.crypto.StringMode;
import org.zero.common.core.support.api.crypto.supplier.KeySupplier;
import org.zero.common.core.support.api.crypto.supplier.NonKeySupplier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加密注解，用于 ResponseBody 返回数据加密
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
	 *     <li>如果是非对称加密，此处为公钥</li>
	 * </ul>
	 */
	String key() default "";

	/**
	 * 密钥提供者
	 */
	Class<? extends KeySupplier> keyProvider() default NonKeySupplier.class;

	/**
	 * 字符串模式
	 * <p>
	 * 仅当密文需要转成字符串时才生效
	 */
	StringMode stringMode() default StringMode.HEX;
}
