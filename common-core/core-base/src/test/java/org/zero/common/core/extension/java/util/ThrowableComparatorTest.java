package org.zero.common.core.extension.java.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ThrowableComparator} 测试。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/15
 */
class ThrowableComparatorTest {
	@Test
	void shouldWrapStandardComparator() throws Throwable {
		ThrowableComparator<String> comparator = ThrowableComparator.of(Comparator.<String>naturalOrder());

		assertTrue(comparator.compare("a", "b") < 0);
		assertEquals(0, comparator.compare("a", "a"));
		assertTrue(comparator.compare("b", "a") > 0);
	}

	@Test
	void shouldAllowCheckedThrowableFromCompare() {
		IOException exception = new IOException("compare failed");
		ThrowableComparator<String> comparator = (left, right) -> {
			throw exception;
		};

		IOException actual = assertThrows(IOException.class, () -> comparator.compare("a", "b"));

		assertSame(exception, actual);
	}

	@Test
	void shouldAdaptToStandardComparator() {
		Comparator<String> comparator = ((ThrowableComparator<String>) (left, right) -> {
			return Integer.compare(left.length(), right.length());
		}).to();

		assertTrue(comparator.compare("aa", "b") > 0);
		assertEquals(0, comparator.compare("aa", "bb"));
		assertTrue(comparator.compare("a", "bb") < 0);
	}

	@Test
	void shouldRethrowThrowableWhenAdaptedComparatorFails() {
		IOException exception = new IOException("compare failed");
		Comparator<String> comparator = ((ThrowableComparator<String>) (left, right) -> {
			throw exception;
		}).to();

		IOException actual = assertThrows(IOException.class, () -> comparator.compare("a", "b"));

		assertSame(exception, actual);
	}
}
