package org.zero.common.core.extension.java.security;

import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.interfaces.ECKey;
import java.security.spec.ECGenParameterSpec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
class KeyPairBuilderTest {
	@Test
	void shouldGenerateEcKeyPairWithConfiguredKeySize() {
		KeyPair keyPair = KeyPairBuilder.builder("EC")
				.keySize(384)
				.build();

		assertEquals(384, ((ECKey) keyPair.getPrivate()).getParams().getCurve().getField().getFieldSize());
	}

	@Test
	void shouldRejectConfiguringKeySizeAndAlgorithmParameterSpecAtTheSameTime() {
		IllegalStateException exception = assertThrows(
				IllegalStateException.class,
				() -> KeyPairBuilder.builder("EC")
						.keySize(256)
						.algorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
						.build()
		);

		assertEquals("keySize and algorithmParameterSpec cannot be configured at the same time", exception.getMessage());
	}
}
