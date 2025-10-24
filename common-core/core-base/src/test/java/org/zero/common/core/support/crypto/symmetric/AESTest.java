package org.zero.common.core.support.crypto.symmetric;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/20
 */
class AESTest {
	@Test
	void test() {
		String message = "Hello, World!";
		AES aes = new AES();
		byte[] encrypted = aes.encrypt(message.getBytes());
		byte[] decrypted = aes.decrypt(encrypted);
		System.out.println("Message: " + message);
		System.out.println("Encrypted: " + Arrays.toString(encrypted));
		System.out.println("Decrypted: " + Arrays.toString(decrypted));
		System.out.println("Decrypted Message: " + new String(decrypted, StandardCharsets.UTF_8));
	}
}
