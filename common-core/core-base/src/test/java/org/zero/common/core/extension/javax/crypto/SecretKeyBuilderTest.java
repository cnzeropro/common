package org.zero.common.core.extension.javax.crypto;

import org.junit.jupiter.api.Test;
import sun.security.internal.spec.TlsRsaPremasterSecretParameterSpec;

import javax.crypto.SecretKey;
import java.security.spec.AlgorithmParameterSpec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/20
 */
class SecretKeyBuilderTest {
	@Test
	void shouldGenerateSecretKeyWithSingleAlgorithmParameterSpec() {
		AlgorithmParameterSpec algorithmParameterSpec = new TlsRsaPremasterSecretParameterSpec(3, 3);
		SecretKey secretKey = SecretKeyBuilder.builder("SunTlsRsaPremasterSecret")
				.algorithmParameterSpec(algorithmParameterSpec)
			.build();

		assertEquals("TlsRsaPremasterSecret", secretKey.getAlgorithm());
		assertEquals(48, secretKey.getEncoded().length);
	}

	@Test
	void shouldRejectConfiguringKeySizeAndAlgorithmParameterSpecAtTheSameTime() {
		IllegalStateException exception = assertThrows(
				IllegalStateException.class,
				() -> SecretKeyBuilder.builder("SunTlsRsaPremasterSecret")
						.keySize(128)
						.algorithmParameterSpec(new TlsRsaPremasterSecretParameterSpec(3, 3))
						.build()
		);

		assertEquals("keySize and algorithmParameterSpec cannot be configured at the same time", exception.getMessage());
	}
}
