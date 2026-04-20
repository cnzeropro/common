package org.zero.common.core.extension.java.security;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
class KeyStoreBuilderTest {
	@Test
	void shouldNotHoldFileHandleWhenConfiguringPath() throws Exception {
		char[] password = "changeit".toCharArray();
		Path path = SecurityBuilderTestSupport.writeTestFile(
				"keystore-path-release-",
				".p12",
				SecurityBuilderTestSupport.createEmptyKeyStoreBytes("PKCS12", password)
		);

		KeyStoreBuilder.create()
				.type("PKCS12")
				.path(path)
				.password(password);

		assertTrue(Files.deleteIfExists(path));
	}

	@Test
	void shouldLoadKeyStoreFromPath() throws Exception {
		char[] password = "changeit".toCharArray();
		Path path = SecurityBuilderTestSupport.writeTestFile(
				"keystore-load-",
				".p12",
				SecurityBuilderTestSupport.createEmptyKeyStoreBytes("PKCS12", password)
		);

		KeyStore keyStore = KeyStoreBuilder.create()
				.type("PKCS12")
				.path(path)
				.password(password)
				.build();

		assertEquals("PKCS12", keyStore.getType());
		assertEquals(0, keyStore.size());
	}

	@Test
	void shouldUseLastConfiguredInputSource() throws Exception {
		char[] password = "changeit".toCharArray();
		byte[] keyStoreBytes = SecurityBuilderTestSupport.createEmptyKeyStoreBytes("PKCS12", password);
		Path path = SecurityBuilderTestSupport.writeTestFile("keystore-last-source-", ".p12", keyStoreBytes);

		KeyStoreBuilder builder = KeyStoreBuilder.create()
				.type("PKCS12")
				.path(path)
				.password(password)
				.bytes(keyStoreBytes);

		assertTrue(Files.deleteIfExists(path));

		KeyStore keyStore = builder.build();
		assertEquals(0, keyStore.size());
	}
}
