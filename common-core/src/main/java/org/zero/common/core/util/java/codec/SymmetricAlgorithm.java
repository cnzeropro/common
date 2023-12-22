package org.zero.common.core.util.java.codec;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 对称加密算法类型
 *
 * @author Zero (cnzeropro@qq.com)
 * @date 2022/11/26
 */
@AllArgsConstructor
@Getter
public enum SymmetricAlgorithm {
    /**
     * 默认的AES加密方式：AES/ECB/PKCS5Padding
     */
    AES("AES"),
    ARCFOUR("ARCFOUR"),
    BLOWFISH("Blowfish"),
    /**
     * 默认的DES加密方式：DES/ECB/PKCS5Padding
     */
    DES("DES"),
    /**
     * 3DES算法，默认实现为：DESede/ECB/PKCS5Padding
     */
    DES_EDE("DESede"),
    RC2("RC2"),

    PBE_WITH_MD5_AND_DES("PBEWithMD5AndDES"),
    PBE_WITH_SHA1_AND_DES_EDE("PBEWithSHA1AndDESede"),
    PBE_WITH_SHA1_AND_RC2_40("PBEWithSHA1AndRC2_40"),
    ;

    private final String name;
}
