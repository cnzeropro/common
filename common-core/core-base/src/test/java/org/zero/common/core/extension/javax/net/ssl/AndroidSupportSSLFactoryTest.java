package org.zero.common.core.extension.javax.net.ssl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/15
 */
class AndroidSupportSSLFactoryTest {
	@Test
	void defaultProtocolsShouldPreferStrongProtocols() {
		assertArrayEquals(
				new String[]{SslProtocols.TLSv13, SslProtocols.TLSv12, SslProtocols.TLSv11, SslProtocols.TLSv1},
				AndroidSupportSSLFactory.PROTOCOLS
		);
	}

	@Test
	void subclassShouldCustomizeProtocols() {
		AndroidSupportSSLFactory factory = new TestAndroidSupportSSLFactory(SslProtocols.TLSv12);

		assertNotNull(factory.getDefaultCipherSuites());
	}

	private static class TestAndroidSupportSSLFactory extends AndroidSupportSSLFactory {
		private TestAndroidSupportSSLFactory(String... protocols) {
			super(protocols);
		}
	}
}
