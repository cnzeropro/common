package org.zero.common.core.extension.javax.net.ssl;

import org.junit.jupiter.api.Test;

import javax.net.ssl.TrustManagerFactory;
import java.security.Provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/15
 */
class TrustManagerBuilderTest {
	@Test
	void buildShouldUseDefaultAlgorithmAndProvider() {
		assertNotNull(TrustManagerBuilder.create().build());
	}

	@Test
	void providerNameShouldResolveToProviderOnly() throws Exception {
		Provider provider = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).getProvider();
		TrustManagerBuilder builder = TrustManagerBuilder.create().providerName(provider.getName());

		assertSame(provider, builder.provider);
		assertNotNull(builder.build());
	}

	@Test
	void providerShouldCreateFactoryWithProvider() throws Exception {
		Provider provider = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).getProvider();
		TrustManagerBuilder builder = TrustManagerBuilder.create().provider(provider);

		assertSame(provider, builder.provider);
		assertNotNull(builder.build());
	}

	@Test
	void missingProviderNameShouldClearProvider() {
		TrustManagerBuilder builder = TrustManagerBuilder.create().providerName("missing");

		assertNull(builder.provider);
		assertNotNull(builder.build());
	}

	@Test
	void buildShouldWrapSecurityFailure() {
		IllegalStateException exception = assertThrows(
				IllegalStateException.class,
				() -> TrustManagerBuilder.create().algorithm("missing").build()
		);

		assertEquals("Failed to build TrustManagers", exception.getMessage());
	}
}
