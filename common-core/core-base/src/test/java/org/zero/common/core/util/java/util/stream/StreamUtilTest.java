package org.zero.common.core.util.java.util.stream;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/14
 */
class StreamUtilTest {
	@Test
	void shouldCreateStreamFromEnumeration() {
		Enumeration<String> enumeration = Collections.enumeration(Arrays.asList("a", "b"));

		assertEquals(Arrays.asList("a", "b"), StreamUtil.of(enumeration).collect(Collectors.toList()));
	}

	@Test
	void shouldCreateEmptyStreamFromNullEnumeration() {
		assertTrue(StreamUtil.of((Enumeration<String>) null).collect(Collectors.toList()).isEmpty());
	}

	@Test
	void shouldCreateStreamFromIterator() {
		Iterator<String> iterator = Arrays.asList("a", "b").iterator();

		assertEquals(Arrays.asList("a", "b"), StreamUtil.of(iterator).collect(Collectors.toList()));
	}

	@Test
	void shouldCreateEmptyStreamFromNullIterator() {
		assertTrue(StreamUtil.of((Iterator<String>) null).collect(Collectors.toList()).isEmpty());
	}
}
