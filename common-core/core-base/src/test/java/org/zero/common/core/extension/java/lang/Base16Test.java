package org.zero.common.core.extension.java.lang;

import org.junit.jupiter.api.Test;
import org.zero.common.data.exception.UtilException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
class Base16Test {

	@Test
	void shouldDecodeEvenLengthHex() {
		byte[] bytes = Base16.LOWER.decode("0aBc");

		assertArrayEquals(new byte[]{0x0a, (byte) 0xbc}, bytes);
	}

	@Test
	void shouldAutoPadLeadingZeroForOddLengthHexByDefault() {
		byte[] defaultDecoded = Base16.LOWER.decode("abc");
		byte[] configuredDecoded = Base16.LOWER.decode("abc", true);

		assertArrayEquals(new byte[]{0x0a, (byte) 0xbc}, defaultDecoded);
		assertArrayEquals(defaultDecoded, configuredDecoded);
	}

	@Test
	void shouldRejectOddLengthHexWhenAutoPaddingDisabled() {
		UtilException exception = assertThrows(UtilException.class, () -> Base16.LOWER.decode("abc", false));

		assertEquals("Odd-length hexadecimal input is not allowed", exception.getMessage());
	}

	@Test
	void shouldReturnEmptyArrayForNullOrEmptyInput() {
		assertArrayEquals(new byte[0], Base16.LOWER.decode((CharSequence) null));
		assertArrayEquals(new byte[0], Base16.LOWER.decode("", false));
	}

	@Test
	void shouldDecodeMixedCaseHexCharacters() {
		byte[] bytes = Base16.UPPER.decode("aBcD", false);

		assertArrayEquals(new byte[]{(byte) 0xab, (byte) 0xcd}, bytes);
	}
}
