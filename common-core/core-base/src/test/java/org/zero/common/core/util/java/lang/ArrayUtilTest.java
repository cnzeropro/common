package org.zero.common.core.util.java.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/15
 */
class ArrayUtilTest {
	@Test
	void containsShouldCompareByObjectsEquals() {
		String[] array = {"a", null, "b"};

		assertTrue(ArrayUtil.contains(array, null));
		assertTrue(ArrayUtil.contains(array, "b"));
		assertFalse(ArrayUtil.contains(array, "c"));
	}

	@Test
	void intersectionShouldKeepLeftOrderAndRemoveDuplicates() {
		String[] intersection = ArrayUtil.intersection(
				new String[]{"TLSv1.3", "SSLv3", "TLSv1.3", "TLSv1.2"},
				new String[]{"TLSv1.2", "TLSv1.3"}
		);

		assertArrayEquals(new String[]{"TLSv1.3", "TLSv1.2"}, intersection);
	}

	@Test
	void intersectionShouldSupportNullElements() {
		String[] intersection = ArrayUtil.intersection(
				new String[]{"a", null, "b", null},
				new String[]{null, "b"}
		);

		assertArrayEquals(new String[]{null, "b"}, intersection);
	}

	@Test
	void intersectionShouldReturnEmptyArrayWhenEitherArrayIsEmpty() {
		assertArrayEquals(new String[0], ArrayUtil.intersection(new String[]{"a"}, new String[0]));
		assertArrayEquals(new String[0], ArrayUtil.intersection(new String[0], new String[]{"a"}));
	}
}
