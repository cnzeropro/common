package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class ToCharFunctionTest {
	@Test
	void shouldReturnCharValue() {
		ToCharFunction<String> function = value -> value.charAt(0);

		assertEquals('z', function.applyAsChar("zero"));
	}
}
