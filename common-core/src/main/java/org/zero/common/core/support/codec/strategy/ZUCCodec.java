package org.zero.common.core.support.codec.strategy;

import cn.hutool.crypto.symmetric.ZUC;
import lombok.Getter;
import lombok.Setter;
import org.zero.common.core.support.codec.CodecUtil;

import java.util.Optional;
import java.util.Properties;

/**
 * ChaCha20 加解密
 * <p>
 * 密钥长度 256 bit（32 byte）
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/27
 */
@Setter
@Getter
public class ZUCCodec extends BaseCodecStrategy {
    public static final String DEFAULT_ALGORITHM = "ZUC-256";
    private final ZUC zuc;

    @Override
    public byte[] encrypt(byte[] source) {
        return zuc.encrypt(source);
    }

    @Override
    public byte[] decrypt(byte[] source) {
        return zuc.decrypt(source);
    }

    public static ZUCCodec of(StrategyContext context) {
        return new ZUCCodec(context);
    }

    protected ZUCCodec(StrategyContext context) {
        super(context);
        String algorithm = context.getAlgorithm();
        if ("ZUC".equalsIgnoreCase(algorithm)) {
            algorithm = DEFAULT_ALGORITHM;
        }
        ZUC.ZUCAlgorithm zucAlgorithm = ZUC.ZUCAlgorithm.ZUC_256;
        if (ZUC.ZUCAlgorithm.ZUC_128.getValue().equalsIgnoreCase(algorithm)) {
            zucAlgorithm = ZUC.ZUCAlgorithm.ZUC_128;
        } else if (ZUC.ZUCAlgorithm.ZUC_256.getValue().equalsIgnoreCase(algorithm)) {
            zucAlgorithm = ZUC.ZUCAlgorithm.ZUC_256;
        }
        Properties properties = context.getProperties();
        byte[] iv = Optional.ofNullable(properties.getProperty("iv"))
                .map(CodecUtil::decode)
                .orElse(null);
        this.zuc = new ZUC(zucAlgorithm, context.getKey(), iv);
    }
}
