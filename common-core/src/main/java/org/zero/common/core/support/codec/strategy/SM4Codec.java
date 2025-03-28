package org.zero.common.core.support.codec.strategy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.symmetric.SM4;
import lombok.Getter;
import lombok.Setter;
import org.zero.common.core.support.codec.CodecUtil;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * SM4 加解密
 * <p>
 * 密钥长度 128 bit
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/27
 */
@Getter
@Setter
public class SM4Codec extends BaseCodecStrategy {
    public static final String DEFAULT_ALGORITHM = "SM4/ECB/PKCS5Padding";
    public static final String DEFAULT_KEY = "dX9naqiFuXRXABCD";
    private final SM4 sm4;

    @Override
    public byte[] encrypt(byte[] source) {
        return sm4.encrypt(source);
    }

    @Override
    public byte[] decrypt(byte[] source) {
        return sm4.decrypt(source);
    }

    public static SM4Codec of(StrategyContext context) {
        return new SM4Codec(context);
    }

    protected SM4Codec(StrategyContext context) {
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
        this.sm4 = new SM4(mode, padding, context.getKey(), iv);
    }
}
