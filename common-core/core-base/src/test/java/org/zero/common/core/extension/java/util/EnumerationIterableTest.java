package org.zero.common.core.extension.java.util;

import org.junit.jupiter.api.Test;
import org.zero.common.core.util.java.util.ListUtil;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/14
 */
class EnumerationIterableTest {
	@Test
	void shouldAdaptEnumerationAsOneShotIterable() {
		EnumerationIterable<String> iterable = EnumerationIterable.of(Collections.enumeration(Arrays.asList("a", "b")));

		assertEquals(Arrays.asList("a", "b"), ListUtil.of(iterable));
		assertTrue(ListUtil.of(iterable).isEmpty());
	}

	@Test
	void shouldTreatNullEnumerationAsEmpty() {
		EnumerationIterable<String> iterable = EnumerationIterable.of(null);

		assertTrue(ListUtil.of(iterable).isEmpty());
	}
}
