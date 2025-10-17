package org.zero.common.core.support.api.cipher.strategy;

import cn.hutool.crypto.symmetric.XXTEA;
import lombok.Getter;
import lombok.Setter;

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
public class XXTEACodec extends BaseCodecStrategy {
    private final XXTEA xxtea;

    @Override
    public byte[] encrypt(byte[] source) {
        return xxtea.encrypt(source);
    }

    @Override
    public byte[] decrypt(byte[] source) {
        return xxtea.decrypt(source);
    }

    public static XXTEACodec of(StrategyContext context) {
        return new XXTEACodec(context);
    }

    protected XXTEACodec(StrategyContext context) {
        super(context);
        this.xxtea = new XXTEA(context.getKey());
    }
}
