package org.zero.common.core.extension.java;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/12/25
 */
class DataSizeTest {
	@Test
	void parseShouldConvertFractionalUnitsWhenBytesAreExact() {
		assertEquals(BigInteger.valueOf(1536L), DataSize.parse("1.5KiB").toBytes());
		assertEquals(BigInteger.valueOf(1500L), DataSize.parse("1.5KB").toBytes());
	}

	@Test
	void parseShouldRejectFractionalBytes() {
		assertThrows(IllegalArgumentException.class, () -> DataSize.parse("1.9B"));
		assertThrows(IllegalArgumentException.class, () -> DataSize.parse("1.1KiB"));
		assertThrows(IllegalArgumentException.class, () -> DataSize.parse("5.18546Q"));
	}

	@Test
	void readableStringShouldUseAbsoluteBytesWhenChoosingUnit() {
		assertEquals("-1.5KiB", DataSize.ofBytes(-1536L).toReadableString());
		assertEquals("-1.5KB", DataSize.ofBytes(-1500L).toReadableString(false));
		assertEquals("-500B", DataSize.ofBytes(-500L).toReadableString());
	}

	@Test
	void parseShouldUseBinaryUnitForAmbiguousLargeSuffix() {
		DataSize dataSize = DataSize.parse("5.5Q");

		assertEquals("5.5QiB", dataSize.toReadableString());
	}
}
