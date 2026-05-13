package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class ThrowableBiFunctionTest {
	@Test
	void shouldWrapStandardBiFunction() throws Throwable {
		assertEquals("ab", ThrowableBiFunction.of(String::concat).apply("a", "b"));
	}

	@Test
	void shouldAdaptToBiFunctionAndReturnValue() {
		ThrowableBiFunction<String, String, String> throwableFunction = String::concat;

		assertEquals("ab", throwableFunction.to().apply("a", "b"));
	}

	@Test
	void shouldAdaptToBiFunctionAndPropagateSameThrowable() {
		IOException exception = new IOException("bi-function failed");
		ThrowableBiFunction<String, String, String> throwableFunction = (left, right) -> {
			throw exception;
		};
		BiFunction<String, String, String> function = throwableFunction.to();

		IOException actual = assertThrows(IOException.class, () -> function.apply("a", "b"));

		assertSame(exception, actual);
	}
}
