package org.zero.common.data.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/03/26
 */
class BaseExceptionTest {

	@Test
	void baseExceptionShouldUseDefaultStatusWhenNoArgConstructorIsUsed() {
		BaseException exception = new BaseException();

		assertEquals("error", exception.getMessage());
		assertEquals(Status.ERROR_CODE, exception.getErrorCode());
		assertEquals("error", exception.getErrorMessage());
		assertEquals("error", exception.getLocalizedMessage());
	}

	@Test
	void baseExceptionShouldUseProvidedStatusConstructor() {
		Status status = Status.Default.of("E100", "business error");
		BaseException exception = new BaseException(status);

		assertEquals("business error", exception.getMessage());
		assertEquals("E100", exception.getErrorCode());
		assertEquals("business error", exception.getErrorMessage());
		assertEquals("business error", exception.getLocalizedMessage());
	}

	@Test
	void baseExceptionShouldSupportMessageConstructors() {
		Status status = Status.Default.of("E200", "legacy");
		BaseException messageException = new BaseException("plain");
		BaseException messageStatusException = new BaseException("plain", status);

		assertEquals("plain", messageException.getMessage());
		assertEquals(Status.ERROR_CODE, messageException.getErrorCode());
		assertEquals("error", messageException.getLocalizedMessage());

		assertEquals("plain", messageStatusException.getMessage());
		assertEquals("E200", messageStatusException.getErrorCode());
		assertEquals("legacy", messageStatusException.getLocalizedMessage());
	}

	@Test
	void baseExceptionShouldSupportCauseConstructors() {
		Status status = Status.Default.of("E300", "legacy cause");
		IllegalStateException cause = new IllegalStateException("boom");
		BaseException causeException = new BaseException(cause);
		BaseException causeStatusException = new BaseException(cause, status);

		assertEquals(cause.toString(), causeException.getMessage());
		assertSame(cause, causeException.getCause());
		assertEquals(Status.ERROR_CODE, causeException.getErrorCode());
		assertEquals("error", causeException.getLocalizedMessage());

		assertEquals(cause.toString(), causeStatusException.getMessage());
		assertSame(cause, causeStatusException.getCause());
		assertEquals("E300", causeStatusException.getErrorCode());
		assertEquals("legacy cause", causeStatusException.getLocalizedMessage());
	}

	@Test
	void baseExceptionShouldSupportMessageAndCauseConstructors() {
		Status status = Status.Default.of("E400", "legacy message cause");
		IllegalStateException cause = new IllegalStateException("boom");
		BaseException messageCauseException = new BaseException("plain", cause);
		BaseException messageCauseStatusException = new BaseException("plain", cause, status);

		assertEquals("plain", messageCauseException.getMessage());
		assertSame(cause, messageCauseException.getCause());
		assertEquals(Status.ERROR_CODE, messageCauseException.getErrorCode());
		assertEquals("error", messageCauseException.getLocalizedMessage());

		assertEquals("plain", messageCauseStatusException.getMessage());
		assertSame(cause, messageCauseStatusException.getCause());
		assertEquals("E400", messageCauseStatusException.getErrorCode());
		assertEquals("legacy message cause", messageCauseStatusException.getLocalizedMessage());
	}
}
