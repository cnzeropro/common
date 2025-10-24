package org.zero.common.core.support.api.crypto.strategy;

import cn.hutool.crypto.symmetric.ChaCha20;
import lombok.Getter;
import lombok.Setter;
import org.zero.common.core.support.api.crypto.CodecUtil;

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
public class ChaCha20Codec extends BaseCodecStrategy {
    private final ChaCha20 chacha20;

    @Override
    public byte[] encrypt(byte[] source) {
        return chacha20.encrypt(source);
    }

    @Override
    public byte[] decrypt(byte[] source) {
        return chacha20.decrypt(source);
    }

    public static ChaCha20Codec of(StrategyContext context) {
        return new ChaCha20Codec(context);
    }

    protected ChaCha20Codec(StrategyContext context) {
        super(context);
        Properties properties = context.getProperties();
        byte[] iv = Optional.ofNullable(properties.getProperty("iv"))
                .map(CodecUtil::decode)
                .orElse(null);
        this.chacha20 = new ChaCha20(context.getKey(), iv);
    }
}
