package org.zero.common.core.extension.java.util.function;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/13
 */
class TriPredicateTest {
	@Test
	void shouldApplyLogicalOperations() {
		TriPredicate<Integer, Integer, Integer> greaterThanZero = (a, b, c) -> a + b + c > 0;
		TriPredicate<Integer, Integer, Integer> allPositive = (a, b, c) -> a > 0 && b > 0 && c > 0;

		assertTrue(greaterThanZero.and(allPositive).test(1, 2, 3));
		assertTrue(greaterThanZero.or(allPositive).test(-1, 2, 3));
		assertTrue(greaterThanZero.negate().test(-1, -2, -3));
	}

	@Test
	void shouldShortCircuitAndOperation() {
		AtomicInteger calls = new AtomicInteger();
		TriPredicate<Integer, Integer, Integer> predicate = (a, b, c) -> false;

		assertFalse(predicate.and((a, b, c) -> {
			calls.incrementAndGet();
			return true;
		}).test(1, 2, 3));
		assertEquals(0, calls.get());
	}

	@Test
	void shouldShortCircuitOrOperation() {
		AtomicInteger calls = new AtomicInteger();
		TriPredicate<Integer, Integer, Integer> predicate = (a, b, c) -> true;

		assertTrue(predicate.or((a, b, c) -> {
			calls.incrementAndGet();
			return false;
		}).test(1, 2, 3));
		assertEquals(0, calls.get());
	}

	@Test
	void shouldRejectNullComposedPredicate() {
		TriPredicate<Integer, Integer, Integer> predicate = (a, b, c) -> true;

		assertThrows(NullPointerException.class, () -> predicate.and(null));
		assertThrows(NullPointerException.class, () -> predicate.or(null));
	}
}
