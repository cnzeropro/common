package org.zero.common.core.extension.java;

import org.junit.jupiter.api.Test;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/12/25
 */
class DataSizeTest {

	@Test
	void test() {
		DataSize dataSize = DataSize.parse("5.18546Q");
		System.out.println(dataSize.toReadableString());
	}
}
