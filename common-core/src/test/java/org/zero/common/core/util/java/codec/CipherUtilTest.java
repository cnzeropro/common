package org.zero.common.core.util.java.codec;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2022/11/24
 */
class CipherUtilTest {

    @Test
    void test() {
//        PBEWithMD5AndTripleDES
//        PBEWithHmacSHA224AndAES_256
        String algorithm = "RSA";

        String content = "admin";
        String password = "你好abc@123";
        String salt = "1PysjKLY";

        Instant start = Instant.now();
        String ciphertext = CipherUtil.encrypt(algorithm, content, password, salt);
        Instant end = Instant.now();

        System.out.println("ciphertext:" + ciphertext);
        System.out.println("len:" + ciphertext.length());
        System.out.println("time:" + Duration.between(start, end));

        start = Instant.now();
        String plaintext = CipherUtil.decrypt(algorithm, ciphertext, password, salt);
        end = Instant.now();

        System.out.println("plaintext:" + plaintext);
        System.out.println("time:" + Duration.between(start, end));
    }

    /**
     * AES_192/CBC/NoPadding
     * AES_192/OFB/NoPadding
     * AES_192/CFB/NoPadding
     * AESWrap_192
     * PBEWithHmacSHA224AndAES_256
     * AES_192/ECB/NoPadding
     * AES_192/GCM/NoPadding
     * PBEWithHmacSHA384AndAES_128
     * AES_128/ECB/NoPadding
     * AES_128/OFB/NoPadding
     * AES_128/CBC/NoPadding
     * AESWrap_128
     * AES_128/CFB/NoPadding
     * AES_128/GCM/NoPadding
     * AES_256/GCM/NoPadding
     * AES_256/CFB/NoPadding
     * AESWrap_256
     * PBEWithMD5AndDES
     * AES_256/ECB/NoPadding
     * AES_256/CBC/NoPadding
     * AES_256/OFB/NoPadding
     * DESedeWrap
     * PBEWithHmacSHA224AndAES_128
     * AES
     * DESede
     * PBEWithHmacSHA512AndAES_128
     * PBEWithSHA1AndRC2_128
     * PBEWithSHA1AndRC2_40
     * PBEWithSHA1AndDESede
     * PBEWithSHA1AndRC4_128
     * PBEWithSHA1AndRC4_40
     * PBEWithHmacSHA512AndAES_256
     * ARCFOUR
     * PBEWithHmacSHA256AndAES_256
     * AESWrap
     * RSA
     * RC2
     * PBEWithHmacSHA256AndAES_128
     * PBEWithHmacSHA1AndAES_128
     * DES
     * PBEWithMD5AndTripleDES
     * PBEWithHmacSHA1AndAES_256
     * Blowfish
     * PBEWithHmacSHA384AndAES_256
     * RSA/ECB/PKCS1Padding
     * RSA
     */
    @Test
    void listAlgorithms() {
        CipherUtil.listAlgorithms().forEach(service ->
                System.out.printf("provider:%s, type:%s, algorithm:%s%n", service.getProvider(), service.getType(), service.getAlgorithm())
        );
    }
}