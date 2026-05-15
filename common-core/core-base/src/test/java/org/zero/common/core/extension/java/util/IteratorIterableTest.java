package org.zero.common.core.extension.java.util;

import org.junit.jupiter.api.Test;
import org.zero.common.core.util.java.util.ListUtil;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/14
 */
class IteratorIterableTest {
	@Test
	void shouldReturnSameIterator() {
		Iterator<String> iterator = Arrays.asList("a", "b").iterator();
		IteratorIterable<String> iterable = IteratorIterable.of(iterator);

		assertSame(iterator, iterable.iterator());
	}

	@Test
	void shouldAdaptIteratorAsOneShotIterable() {
		IteratorIterable<String> iterable = IteratorIterable.of(Arrays.asList("a", "b").iterator());

		assertEquals(Arrays.asList("a", "b"), ListUtil.of(iterable));
		assertTrue(ListUtil.of(iterable).isEmpty());
	}

	@Test
	void shouldTreatNullIteratorAsEmpty() {
		IteratorIterable<String> iterable = IteratorIterable.of(null);

		assertTrue(ListUtil.of(iterable).isEmpty());
	}
}
