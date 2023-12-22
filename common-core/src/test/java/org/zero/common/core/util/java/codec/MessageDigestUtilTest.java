package org.zero.common.core.util.java.codec;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2022/11/28
 */
class MessageDigestUtilTest {
    @Test
    void test() {
        String algorithm = "MD2";
        String content = "你好abc@123";

        Instant start = Instant.now();
        String digest = MessageDigestUtil.digest(algorithm, content);
        Instant end = Instant.now();

        System.out.println("digest:" + digest);
        System.out.println("len:" + digest.length());
        System.out.println("time:" + Duration.between(start, end));
    }

    @Test
    void listAlgorithms() {
        MessageDigestUtil.listAlgorithms().forEach(service ->
                System.out.printf("provider:%s, type:%s, algorithm:%s%n", service.getProvider(), service.getType(), service.getAlgorithm())
        );
    }
}