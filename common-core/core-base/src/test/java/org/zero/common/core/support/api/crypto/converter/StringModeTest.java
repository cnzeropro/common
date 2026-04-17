package org.zero.common.core.support.api.crypto.converter;

import org.junit.jupiter.api.Test;
import org.zero.common.data.exception.UtilException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
class StringModeTest {

	@Test
	void shouldRejectOddLengthHexForHexLower() {
		UtilException exception = assertThrows(UtilException.class, () -> StringMode.HexLower.INSTANCE.toBytes("abc"));

		assertEquals("Odd-length hexadecimal input is not allowed", exception.getMessage());
	}

	@Test
	void shouldRejectOddLengthHexForHexUpper() {
		UtilException exception = assertThrows(UtilException.class, () -> StringMode.HexUpper.INSTANCE.toBytes("ABC"));

		assertEquals("Odd-length hexadecimal input is not allowed", exception.getMessage());
	}

	@Test
	void shouldDecodeEvenLengthHexForStringMode() {
		byte[] bytes = StringMode.HexLower.INSTANCE.toBytes("0abc");

		assertArrayEquals(new byte[]{0x0a, (byte) 0xbc}, bytes);
	}
}
