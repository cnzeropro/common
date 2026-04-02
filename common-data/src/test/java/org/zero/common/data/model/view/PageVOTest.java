package org.zero.common.data.model.view;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.zero.common.data.model.query.PageQO.DEFAULT_NUMBER;
import static org.zero.common.data.model.query.PageQO.DEFAULT_SIZE;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/04/02
 */
class PageVOTest {

	@Test
	void ofFactoryMethodsShouldPopulateExpectedDefaults() {
		PageVO<Integer> defaultPage = PageVO.of();
		PageVO<Integer> customPage = PageVO.of(3L, 5L, 23L);
		PageVO<Integer> customContentsPage = PageVO.of(4L, 6L, 25L, Arrays.asList(7, 8));

		assertEquals(DEFAULT_NUMBER, defaultPage.getCurrent());
		assertEquals(DEFAULT_SIZE, defaultPage.getSize());
		assertEquals(0L, defaultPage.getCount());
		assertEquals(0L, defaultPage.getTotal());
		assertTrue(defaultPage.getContents().isEmpty());
		assertEquals(3L, customPage.getCurrent());
		assertEquals(5L, customPage.getSize());
		assertEquals(23L, customPage.getCount());
		assertEquals(5L, customPage.getTotal());
		assertEquals(4L, customContentsPage.getCurrent());
		assertEquals(6L, customContentsPage.getSize());
		assertEquals(25L, customContentsPage.getCount());
		assertEquals(5L, customContentsPage.getTotal());
		assertIterableEquals(Arrays.asList(7, 8), customContentsPage.getContents());
	}

	@Test
	void getTotalShouldBeDerivedFromCountAndSize() {
		PageVO<Integer> page = PageVO.<Integer>builder()
			.size(5L)
			.count(23L)
			.build();

		assertEquals(5L, page.getTotal());
	}

	@Test
	void getTotalShouldFallbackToZeroWhenSizeIsInvalid() {
		PageVO<Integer> page = PageVO.<Integer>builder()
			.size(0L)
			.count(23L)
			.build();

		assertEquals(0L, page.getTotal());
	}

	@Test
	void convertShouldMutateSameInstanceAndKeepPaginationMetadata() {
		PageVO<Integer> page = PageVO.<Integer>of(2L, 5L, 23L)
			.setContents(Arrays.asList(1, 2));

		PageVO<String> converted = page.convert(String::valueOf);

		assertSame(page, converted);
		assertEquals(2L, converted.getCurrent());
		assertEquals(5L, converted.getSize());
		assertEquals(23L, converted.getCount());
		assertEquals(5L, converted.getTotal());
		assertIterableEquals(Arrays.asList("1", "2"), converted.getContents());
	}

	@Test
	void convertNewShouldCreateNewInstanceAndKeepOriginalContents() {
		PageVO<Integer> page = PageVO.<Integer>of(2L, 5L, 23L)
			.setContents(Arrays.asList(1, 2));

		PageVO<String> converted = page.convertNew(String::valueOf);

		assertNotSame(page, converted);
		assertEquals(2L, converted.getCurrent());
		assertEquals(5L, converted.getSize());
		assertEquals(23L, converted.getCount());
		assertEquals(5L, converted.getTotal());
		assertIterableEquals(Arrays.asList("1", "2"), converted.getContents());
		assertIterableEquals(Arrays.asList(1, 2), page.getContents());
	}

	@Test
	void withContentsShouldCreateNewPageAndKeepMetadata() {
		PageVO<Integer> page = PageVO.<Integer>of(2L, 5L, 23L)
			.setContents(Collections.singletonList(1));

		PageVO<Integer> copied = page.withContents(Arrays.asList(2, 3));

		assertNotSame(page, copied);
		assertEquals(2L, copied.getCurrent());
		assertEquals(5L, copied.getSize());
		assertEquals(23L, copied.getCount());
		assertEquals(5L, copied.getTotal());
		assertIterableEquals(Arrays.asList(2, 3), copied.getContents());
		assertIterableEquals(Collections.singletonList(1), page.getContents());
	}
}
