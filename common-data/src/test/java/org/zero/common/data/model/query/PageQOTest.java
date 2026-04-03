package org.zero.common.data.model.query;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/3
 */
class PageQOTest {

	@Test
	void builderShouldUseDefaultPaginationValues() {
		PageQO pageQO = PageQO.builder().build();

		assertAll(
			() -> assertEquals(PageQO.DEFAULT_NUMBER, pageQO.getNumber()),
			() -> assertEquals(PageQO.DEFAULT_SIZE, pageQO.getSize())
		);
	}

	@Test
	void toBuilderShouldAllowOverridingPaginationValues() {
		PageQO original = PageQO.builder()
			.number(2L)
			.size(50L)
			.build();
		PageQO copied = original.toBuilder()
			.size(20L)
			.build();

		assertAll(
			() -> assertNotSame(original, copied),
			() -> assertEquals(2L, original.getNumber()),
			() -> assertEquals(50L, original.getSize()),
			() -> assertEquals(2L, copied.getNumber()),
			() -> assertEquals(20L, copied.getSize())
		);
	}
}
