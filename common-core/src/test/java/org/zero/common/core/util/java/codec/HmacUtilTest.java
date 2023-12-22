package org.zero.common.core.util.java.codec;

import org.junit.jupiter.api.Test;

import java.security.Provider;
import java.security.Security;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2022/11/25
 */
class HmacUtilTest {

    @Test
    void test() {
        String hmacAlgorithm = "HmacPBESHA512/224";
        String keyAlgorithm = "PBKDF2WithHmacSHA256";
        String content = "你好abc@123";
        String password = "1PysjKLY";

        Instant start = Instant.now();
        String digest = HmacUtil.digest(hmacAlgorithm, keyAlgorithm, content, password);
        Instant end = Instant.now();

        System.out.println("digest:" + digest);
        System.out.println("len:" + digest.length());
        System.out.println("time:" + Duration.between(start, end));
    }

    /**
     * PBEWithHmacSHA512
     * HmacPBESHA512
     * HmacPBESHA512/224
     * HmacSHA1
     * HmacSHA224
     * HmacSHA256
     * HmacPBESHA1
     * SslMacMD5
     * PBEWithHmacSHA384
     * PBEWithHmacSHA1
     * HmacSHA384
     * HmacSHA512
     * HmacPBESHA512/256
     * HmacPBESHA384
     * HmacPBESHA256
     * SslMacSHA1
     * PBEWithHmacSHA224
     * HmacPBESHA224
     * PBEWithHmacSHA256
     * HmacMD5
     */
    @Test
    void listAlgorithms() {
        Arrays.stream(Security.getProviders())
                .flatMap(p -> p.getServices().stream())
                .map(Provider.Service::getType)
                .distinct()
                .forEach(System.out::println);
        // HmacUtil.listAlgorithms().forEach(service ->
        //         System.out.printf("provider:%s, type:%s, algorithm:%s%n", service.getProvider(), service.getType(), service.getAlgorithm())
        // );
    }
}