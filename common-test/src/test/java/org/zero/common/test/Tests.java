package org.zero.common.test;

import cn.hutool.core.codec.Base64;
import org.junit.jupiter.api.Test;
import org.zero.common.core.extension.java.security.KeyPairBuilder;

import java.security.KeyPair;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/6
 */
class Tests {
	@Test
	void test() {
		KeyPair keyPair = KeyPairBuilder.builder("RSA")
			.keySize(2048)
			.build();
		String publicKey = Base64.encode(keyPair.getPublic().getEncoded());
		String privateKey = Base64.encode(keyPair.getPrivate().getEncoded());
		System.out.println("publicKey = " + publicKey);
		System.out.println("privateKey = " + privateKey);
	}

    @Test
    void testArrayMaxSize() {
        Object[] array = new Object[0];
        for (int i = Integer.MAX_VALUE; i > 0; i--) {
            try {
                array = new Object[i];
            } catch (OutOfMemoryError e) {
                System.out.println("size = " + i);
                e.printStackTrace();
            }
        }
        System.out.println("max array size: " + array.length);
    }
}
