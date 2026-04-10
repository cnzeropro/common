package org.zero.common.data.enumeration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/10
 */
class CacheControlInstructionTest {

	@Test
	void shouldExposeStandardCacheControlValues() {
		assertAll(
			() -> assertEquals("no-store", CacheControlInstruction.NO_STORE.getCacheControl()),
			() -> assertEquals("no-cache", CacheControlInstruction.NO_CACHE.getCacheControl()),
			() -> assertEquals("no-transform", CacheControlInstruction.NO_TRANSFORM.getCacheControl()),
			() -> assertEquals("must-revalidate", CacheControlInstruction.MUST_REVALIDATE.getCacheControl())
		);
	}
}
