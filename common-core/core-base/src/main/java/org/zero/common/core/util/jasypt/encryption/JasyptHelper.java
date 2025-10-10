package org.zero.common.core.util.jasypt.encryption;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.PBEConfig;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2021/10/3 21:41
 */
@RequiredArgsConstructor(staticName = "build")
public class JasyptHelper {
    public static final String DEFAULT_ALGORITHM = "PBEWithHMACSHA512AndAES_256";
    @Delegate
    protected final StringEncryptor encryptor;

    public static JasyptHelper build(PBEConfig config) {
        PooledPBEStringEncryptor pbeStringEncryptor = new PooledPBEStringEncryptor();
        pbeStringEncryptor.setConfig(config);
        return build(pbeStringEncryptor);
    }

    public static JasyptHelper build(String password) {
        return build(password, DEFAULT_ALGORITHM);
    }

    public static JasyptHelper build(String password, String algorithm) {
        SimpleStringPBEConfig config = customConfiguration(password, algorithm);
        return build(config);
    }

    protected static SimpleStringPBEConfig customConfiguration(String password, String algorithm) {
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        // 加密算法建议：
        // PBEWithMD5AndDES 2.x
        // PBEWithHMACSHA512AndAES_256 3.x
        config.setAlgorithm(algorithm);
        config.setKeyObtentionIterations(1000);
        config.setPoolSize(10);
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        // 随机偏移量生成器建议：
        // org.jasypt.salt.NoOpIVGenerator 2.x
        // org.jasypt.iv.RandomIvGenerator 3.x
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        return config;
    }
}
