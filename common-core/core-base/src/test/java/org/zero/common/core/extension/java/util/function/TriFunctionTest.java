package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class TriFunctionTest {
	@Test
	void shouldComposeAfterFunction() {
		TriFunction<Integer, Integer, Integer, Integer> function = (a, b, c) -> a + b + c;

		assertEquals("6", function.andThen(String::valueOf).apply(1, 2, 3));
	}

	@Test
	void shouldRejectNullAfterFunction() {
		TriFunction<Integer, Integer, Integer, Integer> function = (a, b, c) -> a + b + c;

		assertThrows(NullPointerException.class, () -> function.andThen(null));
	}
}
