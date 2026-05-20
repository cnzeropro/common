package org.zero.common.core.support.api.crypto.converter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
class StringModeTest {

	@Test
	void shouldAutoPadOddLengthHexForHexLower() {
		byte[] bytes = StringMode.HexLower.INSTANCE.toBytes("abc");

		assertArrayEquals(new byte[]{0x0a, (byte) 0xbc}, bytes);
	}

	@Test
	void shouldAutoPadOddLengthHexForHexUpper() {
		byte[] bytes = StringMode.HexUpper.INSTANCE.toBytes("ABC");

		assertArrayEquals(new byte[]{0x0a, (byte) 0xbc}, bytes);
	}

	@Test
	void shouldDecodeEvenLengthHexForStringMode() {
		byte[] bytes = StringMode.HexLower.INSTANCE.toBytes("0abc");

		assertArrayEquals(new byte[]{0x0a, (byte) 0xbc}, bytes);
	}
}
