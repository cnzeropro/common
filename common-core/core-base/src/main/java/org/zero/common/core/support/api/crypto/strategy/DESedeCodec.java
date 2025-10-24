package org.zero.common.core.support.api.crypto.strategy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.symmetric.DESede;
import lombok.Getter;
import lombok.Setter;
import org.zero.common.core.support.api.crypto.CodecUtil;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * DESede（Triple Data Encryption Algorithm，又称 Triple DES：3DES）加解密
 * <p>
 * 密钥长度支持 112/168 bit
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/27
 */
@Getter
@Setter
public class DESedeCodec extends BaseCodecStrategy {
    public static final String DEFAULT_ALGORITHM = "DESede/ECB/PKCS5Padding";
    public static final String DEFAULT_KEY = "Vh94Nx5I8T6cifAvXBuqTm5UVHS9";

    private final DESede desede;

    @Override
    public byte[] encrypt(byte[] source) {
        return desede.encrypt(source);
    }

    @Override
    public byte[] decrypt(byte[] source) {
        return desede.decrypt(source);
    }

    protected DESedeCodec(StrategyContext context) {
        super(context);
        String algorithm = context.getAlgorithm();
        Properties properties = context.getProperties();
        if (!CharSequenceUtil.contains(algorithm, CodecUtil.DEFAULT_ALGORITHM_SEPARATOR)) {
            algorithm = DEFAULT_ALGORITHM;
        }
        List<String> algorithmInfos = CharSequenceUtil.split(algorithm, CodecUtil.DEFAULT_ALGORITHM_SEPARATOR);
        String mode = CollUtil.get(algorithmInfos, 1);
        String padding = CollUtil.get(algorithmInfos, 2);
        byte[] iv = Optional.ofNullable(properties.getProperty("iv"))
                .map(CodecUtil::decode)
                .orElse(null);
        this.desede = new DESede(mode, padding, context.getKey(), iv);
    }
}
