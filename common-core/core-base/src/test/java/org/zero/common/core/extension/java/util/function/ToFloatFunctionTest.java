package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class ToFloatFunctionTest {
	@Test
	void shouldReturnFloatValue() {
		ToFloatFunction<Integer> function = value -> value / 2.0F;

		assertEquals(3.5F, function.applyAsFloat(7), 0.0001F);
	}
}
