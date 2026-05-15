package org.zero.common.core.extension.javax.net.ssl;

import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
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
class SSLContextBuilderTest {
	@Test
	void buildShouldUsePlatformTrustManagersByDefault() {
		SSLContextBuilder builder = SSLContextBuilder.create();

		assertNull(builder.trustManagers);
		assertNotNull(builder.build().getSocketFactory());
	}

	@Test
	void blankProtocolShouldReturnDefaultContextWithoutReinitializing() throws Exception {
		SSLContext defaultContext = SSLContext.getDefault();

		assertSame(defaultContext, SSLContextBuilder.create().protocol(null).build());
		assertSame(defaultContext, SSLContextBuilder.create().protocol(" ").build());
	}

	@Test
	void trustAllShouldRemainExplicitOptIn() {
		SSLContextBuilder builder = SSLContextBuilder.create().trustAll();

		assertSame(DefaultTrustManager.INSTANCE, builder.trustManagers[0]);
		assertNotNull(builder.build().getSocketFactory());
	}

	@Test
	void trustManagersShouldCopyInputArray() {
		TrustManager[] trustManagers = {DefaultTrustManager.INSTANCE};
		SSLContextBuilder builder = SSLContextBuilder.create().trustManagers(trustManagers);

		trustManagers[0] = null;

		assertSame(DefaultTrustManager.INSTANCE, builder.trustManagers[0]);
	}

	@Test
	void providerNameShouldCreateContextWithNamedProvider() throws Exception {
		Provider provider = SSLContext.getDefault().getProvider();
		SSLContextBuilder builder = SSLContextBuilder.create().providerName(provider.getName());
		SSLContext context = builder.build();

		assertSame(provider, builder.provider);
		assertEquals(provider.getName(), context.getProvider().getName());
	}

	@Test
	void providerShouldCreateContextWithProvider() throws Exception {
		Provider provider = SSLContext.getDefault().getProvider();
		SSLContext context = SSLContextBuilder.create().provider(provider).build();

		assertSame(provider, context.getProvider());
	}

	@Test
	void buildShouldWrapSecurityFailure() {
		IllegalStateException exception = assertThrows(
				IllegalStateException.class,
				() -> SSLContextBuilder.create().protocol("missing").build()
		);

		assertEquals("Failed to build SSLContext", exception.getMessage());
	}
}
