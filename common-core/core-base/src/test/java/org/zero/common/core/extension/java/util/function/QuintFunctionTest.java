package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class QuintFunctionTest {
	@Test
	void shouldComposeAfterFunction() {
		QuintFunction<Integer, Integer, Integer, Integer, Integer, Integer> function =
				(a, b, c, d, e) -> a + b + c + d + e;

		assertEquals("15", function.andThen(String::valueOf).apply(1, 2, 3, 4, 5));
	}

	@Test
	void shouldRejectNullAfterFunction() {
		QuintFunction<Integer, Integer, Integer, Integer, Integer, Integer> function =
				(a, b, c, d, e) -> a + b + c + d + e;

		assertThrows(NullPointerException.class, () -> function.andThen(null));
	}
}
