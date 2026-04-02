package org.zero.common.data.model.view;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/04/02
 */
class SmartPageVOTest {

	@Test
	void ofFactoryShouldNormalizeInvalidArguments() {
		SmartPageVO<Integer> page = SmartPageVO.of(0L, 0L, 23L);
		SmartPageVO<Integer> customContentsPage = SmartPageVO.of(4L, 6L, 25L, Arrays.asList(7, 8));

		assertEquals(1L, page.getCurrent());
		assertEquals(10L, page.getSize());
		assertEquals(23L, page.getCount());
		assertEquals(3L, page.getTotal());
		assertEquals(4L, customContentsPage.getCurrent());
		assertEquals(6L, customContentsPage.getSize());
		assertEquals(25L, customContentsPage.getCount());
		assertEquals(5L, customContentsPage.getTotal());
		assertEquals(Arrays.asList(7, 8), customContentsPage.getContents());
	}

	@Test
	void withCurrentShouldClampToValidRange() {
		SmartPageVO<Integer> page = SmartPageVO.<Integer>of().setSize(20L).setCurrent(12L).setCount(107L);

		SmartPageVO<Integer> minCurrentPage = page.withCurrent(0L);
		SmartPageVO<Integer> maxCurrentPage = page.withCurrent(100L);

		assertNotSame(page, minCurrentPage);
		assertEquals(1L, minCurrentPage.getCurrent());
		assertEquals(6L, maxCurrentPage.getCurrent());
		assertEquals(6L, page.getCurrent());
	}

	@Test
	void withSizeShouldFallbackToDefaultPageSizeAndRecalculateTotal() {
		SmartPageVO<Integer> page = SmartPageVO.<Integer>of(2L, 5L, 23L);

		SmartPageVO<Integer> zeroSizePage = page.withSize(0L);
		SmartPageVO<Integer> negativeSizePage = page.withSize(-1L);

		assertEquals(10L, zeroSizePage.getSize());
		assertEquals(3L, zeroSizePage.getTotal());
		assertEquals(2L, zeroSizePage.getCurrent());
		assertEquals(10L, negativeSizePage.getSize());
		assertEquals(3L, negativeSizePage.getTotal());
	}

	@Test
	void withCountShouldStayConsistentAndClampCurrent() {
		SmartPageVO<Integer> page = SmartPageVO.<Integer>of().setSize(4L).setCount(16L).setCurrent(4L);

		SmartPageVO<Integer> countPage = page.withCount(10L);

		assertEquals(10L, countPage.getCount());
		assertEquals(3L, countPage.getTotal());
		assertEquals(3L, countPage.getCurrent());
		assertEquals(4L, page.getCurrent());
	}

	@Test
	void withByMethodsShouldReuseSameSmartCorrectionRules() {
		SmartPageVO<Integer> page = SmartPageVO.<Integer>of().setSize(5L).setCurrent(5L).setCount(21L);

		assertEquals(page.withCurrent(0L), page.withCurrentBy(current -> 0L));
		assertEquals(page.withSize(0L), page.withSizeBy(size -> 0L));
		assertEquals(page.withCount(11L), page.withCountBy(count -> 11L));
	}

	@Test
	void withContentsMethodsShouldReplaceContentsAndNormalizeMetadata() {
		SmartPageVO<String> page = SmartPageVO.of();
		page.size = 0L;
		page.count = 25L;
		page.current = 99L;
		page.contents = Collections.singletonList("legacy");

		SmartPageVO<String> contentsPage = page.withContents(Arrays.asList("A", "B"));
		SmartPageVO<String> contentsByPage = page.withContentsBy(contents -> Collections.singletonList("C"));

		assertEquals(10L, contentsPage.getSize());
		assertEquals(3L, contentsPage.getTotal());
		assertEquals(3L, contentsPage.getCurrent());
		assertEquals(Arrays.asList("A", "B"), contentsPage.getContents());
		assertEquals(10L, contentsByPage.getSize());
		assertEquals(3L, contentsByPage.getTotal());
		assertEquals(3L, contentsByPage.getCurrent());
		assertEquals(Collections.singletonList("C"), contentsByPage.getContents());
	}

	@Test
	void convertMethodsShouldKeepSmartTypeAndContents() {
		SmartPageVO<Integer> page = SmartPageVO.<Integer>of().setSize(5L).setCount(21L).setContents(Arrays.asList(1, 2));

		SmartPageVO<String> convertedPage = page.convertNew(String::valueOf);

		assertNotSame(page, convertedPage);
		assertEquals(5L, convertedPage.getSize());
		assertEquals(21L, convertedPage.getCount());
		assertEquals(Arrays.asList("1", "2"), convertedPage.getContents());
	}

	@Test
	void convertShouldReuseSameInstanceAndKeepSmartMetadata() {
		SmartPageVO<Integer> page = SmartPageVO.<Integer>of().setSize(5L).setCurrent(9L).setCount(21L).setContents(Arrays.asList(1, 2));

		SmartPageVO<String> convertedPage = page.convert(String::valueOf);

		assertSame(page, convertedPage);
		assertEquals(5L, convertedPage.getSize());
		assertEquals(21L, convertedPage.getCount());
		assertEquals(5L, convertedPage.getCurrent());
		assertEquals(5L, convertedPage.getTotal());
		assertEquals(Arrays.asList("1", "2"), convertedPage.getContents());
	}

	@Test
	void setterChainShouldKeepExistingBehavior() {
		SmartPageVO<Integer> page = SmartPageVO.<Integer>of().setSize(20L).setCurrent(12L).setCount(107L);

		assertEquals(20L, page.getSize());
		assertEquals(6L, page.getCurrent());
		assertEquals(6L, page.getTotal());
		assertEquals(107L, page.getCount());
	}
}
