package org.zero.common.core.support.api.crypto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.zero.common.core.support.api.crypto.strategy.AESCodec;
import org.zero.common.core.support.api.crypto.supplier.KeySupplier;
import org.zero.common.core.support.api.crypto.supplier.NonKeySupplier;

import java.lang.reflect.Executable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/24
 */
@Setter
@Getter
@ConfigurationProperties(prefix = CodecProperties.DEFAULT_PREFIX)
public class CodecProperties {
    static final String DEFAULT_PREFIX = "sys.codec";
    public static final String DEFAULT_ALGORITHM = AESCodec.DEFAULT_ALGORITHM;
    public static final String DEFAULT_KEY = AESCodec.DEFAULT_KEY;

    /**
     * 默认配置的键名称
     */
    private String defaultConfigKey = "default";
    /**
     * 加密配置映射
     * <p>
     * 其中 key 是方法的 {@linkplain org.zero.common.core.util.java.reflect.ReflectUtil#getFullName(Executable) 全限定名称}，
     * value 是 {@linkplain CodecConfiguration 配置}
     */
    private Map<String, CodecConfiguration> encryptionConfig = new LinkedHashMap<>();
    /**
     * 解密配置映射
     * <p>
     * 其中 key 是方法名，具体 {@linkplain CodecUtil#getConfig(Executable, Map) 匹配规则}，
     * value 是 {@linkplain CodecConfiguration 配置}
     */
    private Map<String, CodecConfiguration> decryptionConfig = new LinkedHashMap<>();

    @Setter
    @Getter
    public static class CodecConfiguration {
        private boolean enabled = true;
        /**
         * 加解密算法
         * <p>
         * 注意：加解密算法建议与密钥一同配置，否则可能出现问题。
         * 比如 AES 算法需要密钥长度为 128、192、256 bit，而 DES 算法需要密钥长度为 8 的倍数。
         */
        private String algorithm = DEFAULT_ALGORITHM;
        /**
         * 密钥
         * <p>
         * 可以是 Base64 编码 和 Hex 编码字串，也可以是普通字符串。<br>
         * 但请注意保证密钥与算法匹配。
         * <p>
         * 如果是非对称加密，此处为私钥；非对称解密，此处为公钥<br>
         * 优先使用该值，如果为空则使用 {@linkplain #keySupplier 密钥提供者}
         */
        private String key = DEFAULT_KEY;
        /**
         * 密钥提供者
         */
        private Class<? extends KeySupplier> keySupplier = NonKeySupplier.class;
        /**
         * 字符串模式
         */
        private StringMode stringMode = StringMode.HEX;
        /**
         * 额外配置
         * <p>
         * 如：AES 算法可能配置 iv 等等
         */
        private Properties properties = new Properties();
    }
}