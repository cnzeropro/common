package org.zero.common.core.util.java.codec;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 非对称加密算法类型
 *
 * @author Zero (cnzeropro@qq.com)
 * @date 2022/11/26
 */
@AllArgsConstructor
@Getter
public enum AsymmetricAlgorithm {
    /**
     * RSA算法
     */
    RSA("RSA"),
    /**
     * RSA算法，此算法用了默认补位方式为RSA/ECB/PKCS1Padding
     */
    RSA_ECB_PKCS1("RSA/ECB/PKCS1Padding"),
    /**
     * RSA算法，此算法用了默认补位方式为RSA/ECB/NoPadding
     */
    RSA_ECB("RSA/ECB/NoPadding"),
    /**
     * RSA算法，此算法用了RSA/None/NoPadding
     */
    RSA_NONE("RSA/None/NoPadding"),
    ;

    private final String name;
}
