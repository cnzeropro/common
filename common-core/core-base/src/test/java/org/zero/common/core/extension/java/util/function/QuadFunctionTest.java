package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class QuadFunctionTest {
	@Test
	void shouldComposeAfterFunction() {
		QuadFunction<Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d) -> a + b + c + d;

		assertEquals("10", function.andThen(String::valueOf).apply(1, 2, 3, 4));
	}

	@Test
	void shouldRejectNullAfterFunction() {
		QuadFunction<Integer, Integer, Integer, Integer, Integer> function = (a, b, c, d) -> a + b + c + d;

		assertThrows(NullPointerException.class, () -> function.andThen(null));
	}
}
