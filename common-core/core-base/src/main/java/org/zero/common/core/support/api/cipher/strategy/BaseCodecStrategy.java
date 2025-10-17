package org.zero.common.core.support.api.cipher.strategy;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/24
 */
public abstract class BaseCodecStrategy implements CodecStrategy {
    protected BaseCodecStrategy(StrategyContext context) {
    }

    @Override
    public abstract byte[] encrypt(byte[] source);

    @Override
    public abstract byte[] decrypt(byte[] source);
}
