package org.zero.common.data.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/19
 */
class UtilExceptionTest {
	@Test
	void constructorShouldUseCauseMessageAndKeepCause() {
		IllegalStateException cause = new IllegalStateException("boom");
		UtilException exception = new UtilException(cause);

		assertEquals("boom", exception.getMessage());
		assertSame(cause, exception.getCause());
		assertEquals("error", exception.getLocalizedMessage());
	}
}
