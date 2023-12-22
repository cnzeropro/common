package org.zero.common.core.util.java.codec;

import cn.hutool.core.util.HexUtil;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.time.Duration;
import java.time.Instant;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2022/11/28
 */
class SignatureUtilTest {
    @Test
    void test() {
        String keyAlgorithm = "SHA512withDSA";
        String signatureAlgorithm = "SHA1withDSA";
        String content = "你好abc@123";

        KeyPair keyPair = KeyUtil.generateKeyPair(keyAlgorithm);

        Instant start = Instant.now();
        String sign = SignatureUtil.sign(signatureAlgorithm, content, keyPair.getPrivate());
        Instant end = Instant.now();

        System.out.println("privateKey:" + HexUtil.encodeHexStr(keyPair.getPrivate().getEncoded()));
        System.out.println("publicKey:" + HexUtil.encodeHexStr(keyPair.getPublic().getEncoded()));
        System.out.println("sign:" + sign);
        System.out.println("len:" + sign.length());
        System.out.println("time:" + Duration.between(start, end));

        System.out.println("==============================================================");

        start = Instant.now();
        boolean verify = SignatureUtil.verify(signatureAlgorithm, sign, content + "a", keyPair.getPublic());
        end = Instant.now();

        System.out.println("verify:" + verify);
        System.out.println("time:" + Duration.between(start, end));
    }

    @Test
    void listAlgorithms() {
        SignatureUtil.listAlgorithms().forEach(service ->
                System.out.printf("provider:%s, type:%s, algorithm:%s%n", service.getProvider(), service.getType(), service.getAlgorithm())
        );
    }
}