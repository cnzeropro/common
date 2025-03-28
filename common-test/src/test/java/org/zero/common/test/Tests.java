package org.zero.common.test;

import cn.hutool.core.codec.Base64;
import org.junit.jupiter.api.Test;
import org.zero.common.core.support.codec.CodecUtil;

import java.security.KeyPair;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/6
 */
 class Tests {
     @Test
     void test() {
         KeyPair keyPair = CodecUtil.createKeyPair("RSA", 2048);
         String publicKey = Base64.encode(keyPair.getPublic().getEncoded());
         String privateKey = Base64.encode(keyPair.getPrivate().getEncoded());
         System.out.println("publicKey = " + publicKey);
         System.out.println("privateKey = " + privateKey);
     }
}
