package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class ThrowableFunctionTest {
	@Test
	void shouldWrapStandardFunction() throws Throwable {
		assertEquals(4, ThrowableFunction.of(String::length).apply("zero"));
	}

	@Test
	void shouldAdaptToFunctionAndReturnValue() {
		ThrowableFunction<String, Integer> throwableFunction = String::length;

		assertEquals(4, throwableFunction.to().apply("zero"));
	}

	@Test
	void shouldAdaptToFunctionAndPropagateSameThrowable() {
		IOException exception = new IOException("function failed");
		ThrowableFunction<String, Integer> throwableFunction = value -> {
			throw exception;
		};
		Function<String, Integer> function = throwableFunction.to();

		IOException actual = assertThrows(IOException.class, () -> function.apply("zero"));

		assertSame(exception, actual);
	}
}
