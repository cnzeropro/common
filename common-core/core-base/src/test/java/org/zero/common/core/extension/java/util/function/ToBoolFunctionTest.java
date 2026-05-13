package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class ToBoolFunctionTest {
	@Test
	void shouldReturnBooleanValue() {
		ToBoolFunction<String> function = value -> value.length() > 3;

		assertTrue(function.applyAsBool("zero"));
		assertFalse(function.applyAsBool("AI"));
	}
}
