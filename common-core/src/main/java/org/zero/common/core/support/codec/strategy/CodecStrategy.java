package org.zero.common.core.support.codec.strategy;

/**
 * 加解密策略
 * <p>
 * 建议使用 {@link BaseCodecStrategy}，而非 {@link CodecStrategy}，因为需要 {@link StrategyContext} 进行配置
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/27
 */
public interface CodecStrategy {
    byte[] encrypt(byte[] source);

    byte[] decrypt(byte[] source);
}
