package org.zero.common.data.model.query;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/3
 */
class SortQOTest {

	@Test
	void noArgsConstructorShouldDefaultToAscendingDirection() {
		SortQO sortQO = new SortQO();

		assertAll(
			() -> assertNull(sortQO.getField()),
			() -> assertEquals(SortQO.Direction.ASC, sortQO.getDirection())
		);
	}

	@Test
	void allArgsConstructorShouldKeepProvidedValues() {
		SortQO sortQO = new SortQO("createdAt", SortQO.Direction.DESC);

		assertAll(
			() -> assertEquals("createdAt", sortQO.getField()),
			() -> assertEquals(SortQO.Direction.DESC, sortQO.getDirection())
		);
	}
}
