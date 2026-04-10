package org.zero.common.data.model.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/10
 */
class PairTest {

	@Test
	void constructorsShouldExposeKeyAndValue() {
		Pair<String, Integer> emptyPair = new Pair<>();
		Pair<String, Integer> pair = new Pair<>("age", 18);

		assertAll(
			() -> assertNull(emptyPair.getKey()),
			() -> assertNull(emptyPair.getValue()),
			() -> assertEquals("age", pair.getKey()),
			() -> assertEquals(Integer.valueOf(18), pair.getValue())
		);
	}

	@Test
	void setValueShouldReturnPreviousValueAndReplaceCurrentValue() {
		Pair<String, Integer> pair = new Pair<>("age", 18);

		Integer oldValue = pair.setValue(20);

		assertAll(
			() -> assertEquals(Integer.valueOf(18), oldValue),
			() -> assertEquals(Integer.valueOf(20), pair.getValue())
		);
	}
}
