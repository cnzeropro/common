package org.zero.common.core.support.codec.supplier;

/**
 * 密钥提供者
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/24
 */
public interface KeySupplier {
    /**
     * 生成密钥
     * <p>
     * 如果对应算法是非对称加密，则根据加解密不同，返回公钥或者私钥
     *
     * @param context 上下文
     * @return 密钥
     */
    byte[] generate(KeyContext context);
}
