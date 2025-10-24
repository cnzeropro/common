package org.zero.common.core.support.crypto.asymmetric;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/20
 */
class RSATest {
	@Test
	void test() {
		String message = "Hello, World!";
		RSA rsa = new RSA();
		byte[] encrypted = rsa.encrypt(message.getBytes(StandardCharsets.UTF_8));
		byte[] decrypted = rsa.decrypt(encrypted);
		System.out.println("Message: " + message);
		System.out.println("Encrypted: " + Arrays.toString(encrypted));
		System.out.println("Decrypted: " + Arrays.toString(decrypted));
		System.out.println("Decrypted Message: " + new String(decrypted, StandardCharsets.UTF_8));
	}
}
