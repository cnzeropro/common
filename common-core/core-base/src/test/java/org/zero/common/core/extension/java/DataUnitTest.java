package org.zero.common.core.extension.java;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
class DataUnitTest {
	@Test
	void fromSuffixShouldPreferExactSuffixBeforeAbbreviation() {
		assertEquals(DataUnit.BYTE, DataUnit.fromSuffix("B"));
		assertEquals(DataUnit.MEBIBYTE, DataUnit.fromSuffix("M"));
		assertEquals(DataUnit.MEBIBYTE, DataUnit.fromSuffix("m"));
		assertEquals(DataUnit.MEBIBYTE, DataUnit.fromSuffix("MiB"));
		assertEquals(DataUnit.MEGABYTE, DataUnit.fromSuffix("MB"));
		assertEquals(DataUnit.QUEBIBYTE, DataUnit.fromSuffix("Q"));
		assertEquals(DataUnit.QUETTABYTE, DataUnit.fromSuffix("QB"));
	}

	@Test
	void fromSuffixShouldRejectBlankSuffix() {
		assertThrows(IllegalArgumentException.class, () -> DataUnit.fromSuffix(""));
	}
}
