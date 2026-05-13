package org.zero.common.core.extension.java.util.concurrent;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class ThrowableCallableTest {
	@Test
	void shouldWrapStandardCallable() throws Throwable {
		Callable<String> callable = () -> "value";

		assertEquals("value", ThrowableCallable.of(callable).call());
	}

	@Test
	void shouldAdaptToCallableAndReturnValue() throws Exception {
		ThrowableCallable<Integer> throwableCallable = () -> 7;

		assertEquals(7, throwableCallable.to().call());
	}

	@Test
	void shouldAdaptToCallableAndPropagateSameThrowable() {
		IOException exception = new IOException("callable failed");
		ThrowableCallable<Integer> throwableCallable = () -> {
			throw exception;
		};
		Callable<Integer> callable = throwableCallable.to();

		IOException actual = assertThrows(IOException.class, callable::call);

		assertSame(exception, actual);
	}
}
