package org.zero.common.core.extension.javax.net.ssl;

import org.junit.jupiter.api.Test;

import javax.net.ssl.KeyManagerFactory;
import java.security.Provider;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/15
 */
class KeyManagerBuilderTest {
	@Test
	void buildShouldUseDefaultAlgorithmAndProvider() {
		assertNotNull(KeyManagerBuilder.create().build());
	}

	@Test
	void providerNameShouldResolveToProviderOnly() throws Exception {
		Provider provider = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).getProvider();
		KeyManagerBuilder builder = KeyManagerBuilder.create().providerName(provider.getName());

		assertSame(provider, builder.provider);
		assertNotNull(builder.build());
	}

	@Test
	void providerNameAliasShouldResolveToProviderOnly() throws Exception {
		Provider provider = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).getProvider();
		KeyManagerBuilder builder = KeyManagerBuilder.create().providerName(provider.getName());

		assertSame(provider, builder.provider);
		assertNotNull(builder.build());
	}

	@Test
	void providerShouldCreateFactoryWithProvider() throws Exception {
		Provider provider = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).getProvider();
		KeyManagerBuilder builder = KeyManagerBuilder.create().provider(provider);

		assertSame(provider, builder.provider);
		assertNotNull(builder.build());
	}

	@Test
	void missingProviderNameShouldClearProvider() {
		KeyManagerBuilder builder = KeyManagerBuilder.create().providerName("missing");

		assertNull(builder.provider);
		assertNotNull(builder.build());
	}

	@Test
	void passwordShouldCopyCharSequence() {
		StringBuilder password = new StringBuilder("secret");
		KeyManagerBuilder builder = KeyManagerBuilder.create().password(password);

		password.setCharAt(0, 'x');

		assertArrayEquals("secret".toCharArray(), builder.password);
	}

	@Test
	void passwordShouldCopyInputArray() {
		char[] password = "secret".toCharArray();
		KeyManagerBuilder builder = KeyManagerBuilder.create().password(password);

		password[0] = 'x';

		assertArrayEquals("secret".toCharArray(), builder.password);
	}

	@Test
	void nullPasswordShouldRemainNull() {
		KeyManagerBuilder builder = KeyManagerBuilder.create().password((CharSequence) null);

		assertNull(builder.password);
	}

	@Test
	void buildShouldWrapSecurityFailure() {
		IllegalStateException exception = assertThrows(
				IllegalStateException.class,
				() -> KeyManagerBuilder.create().algorithm("missing").build()
		);

		assertEquals("Failed to build KeyManagers", exception.getMessage());
	}
}
