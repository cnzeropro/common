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
class QuintPredicateTest {
	@Test
	void shouldApplyLogicalOperations() {
		QuintPredicate<Integer, Integer, Integer, Integer, Integer> greaterThanZero =
				(a, b, c, d, e) -> a + b + c + d + e > 0;
		QuintPredicate<Integer, Integer, Integer, Integer, Integer> allPositive =
				(a, b, c, d, e) -> a > 0 && b > 0 && c > 0 && d > 0 && e > 0;

		assertTrue(greaterThanZero.and(allPositive).test(1, 2, 3, 4, 5));
		assertTrue(greaterThanZero.or(allPositive).test(-1, 2, 3, 4, 5));
		assertTrue(greaterThanZero.negate().test(-1, -2, -3, -4, -5));
	}

	@Test
	void shouldShortCircuitAndOperation() {
		AtomicInteger calls = new AtomicInteger();
		QuintPredicate<Integer, Integer, Integer, Integer, Integer> predicate = (a, b, c, d, e) -> false;

		assertFalse(predicate.and((a, b, c, d, e) -> {
			calls.incrementAndGet();
			return true;
		}).test(1, 2, 3, 4, 5));
		assertEquals(0, calls.get());
	}

	@Test
	void shouldShortCircuitOrOperation() {
		AtomicInteger calls = new AtomicInteger();
		QuintPredicate<Integer, Integer, Integer, Integer, Integer> predicate = (a, b, c, d, e) -> true;

		assertTrue(predicate.or((a, b, c, d, e) -> {
			calls.incrementAndGet();
			return false;
		}).test(1, 2, 3, 4, 5));
		assertEquals(0, calls.get());
	}

	@Test
	void shouldRejectNullComposedPredicate() {
		QuintPredicate<Integer, Integer, Integer, Integer, Integer> predicate = (a, b, c, d, e) -> true;

		assertThrows(NullPointerException.class, () -> predicate.and(null));
		assertThrows(NullPointerException.class, () -> predicate.or(null));
	}
}
