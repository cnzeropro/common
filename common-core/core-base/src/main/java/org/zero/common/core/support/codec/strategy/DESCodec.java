package org.zero.common.core.support.codec.strategy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.symmetric.DES;
import lombok.Getter;
import lombok.Setter;
import org.zero.common.core.support.codec.CodecUtil;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * DES（Data Encryption Standard）加解密
 * <p>
 * 密钥长度 64 bit，但实际有效为 56 bit
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/24
 */
@Setter
@Getter
public class DESCodec extends BaseCodecStrategy {
    public static final String DEFAULT_ALGORITHM = "DES/ECB/PKCS5Padding";
    public static final String DEFAULT_KEY = "QFgizdtFJKg=";

    private final DES des;

    @Override
    public byte[] encrypt(byte[] source) {
        return des.encrypt(source);
    }

    @Override
    public byte[] decrypt(byte[] source) {
        return des.decrypt(source);
    }

    public static DESCodec of(StrategyContext context) {
        return new DESCodec(context);
    }

    protected DESCodec(StrategyContext context) {
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
        this.des = new DES(mode, padding, context.getKey(), iv);
    }
}
