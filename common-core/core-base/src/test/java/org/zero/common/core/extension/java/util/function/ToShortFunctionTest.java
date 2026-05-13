package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class ToShortFunctionTest {
	@Test
	void shouldReturnShortValue() {
		ToShortFunction<Integer> function = value -> (short) (value + 2);

		assertEquals((short) 9, function.applyAsShort(7));
	}
}
