package org.zero.common.core.exception.handler;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.zero.common.core.exception.ThrowableMessageSupplier;
import org.zero.common.data.model.view.Result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * {@link ThrowableHandler} 测试。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/15
 */
class ThrowableHandlerTest {
	private static final ThrowableMessageSupplier THROWABLE_MESSAGE_SUPPLIER = new ThrowableMessageSupplier() {
	};

	@Test
	void shouldReturnProblemDetailByDefault() {
		ThrowableHandler handler = new ThrowableHandler(THROWABLE_MESSAGE_SUPPLIER);

		Object result = handler.handle(HttpStatus.BAD_REQUEST, new IllegalArgumentException("boom"));

		ProblemDetail problemDetail = assertInstanceOf(ProblemDetail.class, result);
		assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
		assertEquals("boom", problemDetail.getDetail());
		assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getProperties().get("code"));
	}

	@Test
	void shouldReturnResultWhenResponseTypeIsResult() {
		ThrowableHandler handler = new ThrowableHandler(THROWABLE_MESSAGE_SUPPLIER, ThrowableResponseType.RESULT);

		Object result = handler.handle(HttpStatus.BAD_REQUEST, new IllegalArgumentException("boom"));

		Result<?> view = assertInstanceOf(Result.class, result);
		assertEquals(HttpStatus.BAD_REQUEST.value(), view.getCode());
		assertEquals("boom", view.getMessage());
	}

	@Test
	void shouldFallbackToInternalServerErrorWhenCodeIsNotStandardHttpStatus() {
		ThrowableHandler handler = new ThrowableHandler(THROWABLE_MESSAGE_SUPPLIER);

		Object result = handler.handle(9001, new IllegalArgumentException("boom"));

		ProblemDetail problemDetail = assertInstanceOf(ProblemDetail.class, result);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problemDetail.getStatus());
		assertEquals(9001, problemDetail.getProperties().get("code"));
	}
}
