package org.zero.common.data.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
class CommonExceptionTest {
	@Test
	void constructorShouldKeepPlainMessageCompatible() {
		CommonException exception = new CommonException("plain");

		assertEquals("plain", exception.getMessage());
		assertEquals("error", exception.getLocalizedMessage());
	}
}
