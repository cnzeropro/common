package org.zero.common.core.extension.javax.crypto;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/20
 */
class SecretKeyBuilderTest {
	@Test
	void test() {
		SecretKey secretKey = SecretKeyBuilder.builder("DES")
			.keySize(64)
			.build();
		System.out.println(secretKey);
	}
}
