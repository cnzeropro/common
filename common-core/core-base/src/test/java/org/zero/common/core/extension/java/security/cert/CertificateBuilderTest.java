package org.zero.common.core.extension.java.security.cert;

import org.junit.jupiter.api.Test;
import org.zero.common.core.extension.java.security.SecurityBuilderTestSupport;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.Certificate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
class CertificateBuilderTest {
	@Test
	void shouldNotHoldFileHandleWhenConfiguringPath() throws Exception {
		Path path = SecurityBuilderTestSupport.writeTestFile(
				"certificate-path-release-",
				".pem",
				SecurityBuilderTestSupport.certificateBytes()
		);

		CertificateBuilder.create().path(path);

		assertTrue(Files.deleteIfExists(path));
	}

	@Test
	void shouldLoadCertificateFromPath() throws Exception {
		Path path = SecurityBuilderTestSupport.writeTestFile(
				"certificate-load-",
				".pem",
				SecurityBuilderTestSupport.certificateBytes()
		);

		Certificate[] certificates = CertificateBuilder.create()
				.path(path)
				.build();

		assertEquals(1, certificates.length);
		assertEquals("X.509", certificates[0].getType());
	}
}
