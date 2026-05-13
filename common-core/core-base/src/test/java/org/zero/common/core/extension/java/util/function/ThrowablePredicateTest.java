package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class ThrowablePredicateTest {
	@Test
	void shouldWrapStandardPredicate() throws Throwable {
		assertTrue(ThrowablePredicate.of(String::isEmpty).test(""));
	}

	@Test
	void shouldAdaptToPredicateAndReturnValue() {
		ThrowablePredicate<String> throwablePredicate = String::isEmpty;

		assertFalse(throwablePredicate.to().test("zero"));
	}

	@Test
	void shouldAdaptToPredicateAndPropagateSameThrowable() {
		IOException exception = new IOException("predicate failed");
		ThrowablePredicate<String> throwablePredicate = value -> {
			throw exception;
		};
		Predicate<String> predicate = throwablePredicate.to();

		IOException actual = assertThrows(IOException.class, () -> predicate.test("zero"));

		assertSame(exception, actual);
	}
}
