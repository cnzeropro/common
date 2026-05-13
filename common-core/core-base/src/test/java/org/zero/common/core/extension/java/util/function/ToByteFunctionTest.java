package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class ToByteFunctionTest {
	@Test
	void shouldReturnByteValue() {
		ToByteFunction<Integer> function = value -> (byte) (value + 1);

		assertEquals((byte) 8, function.applyAsByte(7));
	}
}
