package org.zero.common.core.exception.handler.spring;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.zero.common.core.exception.ThrowableMessageSupplier;
import org.zero.common.core.exception.handler.ThrowableHandler.ResponseType;
import org.zero.common.data.model.view.Result;

import java.lang.reflect.Proxy;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * {@link JakartaExceptionHandler} 测试。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/15
 */
class JakartaExceptionHandlerTest {
	private static final ThrowableMessageSupplier THROWABLE_MESSAGE_SUPPLIER = new ThrowableMessageSupplier() {
	};

	private static ConstraintViolationException createConstraintViolationException() {
		return new ConstraintViolationException(
				"Parameter validation not pass: {0}",
				Collections.singleton(createConstraintViolation("invalid"))
		);
	}

	@SuppressWarnings("unchecked")
	private static ConstraintViolation<Object> createConstraintViolation(String message) {
		return (ConstraintViolation<Object>) Proxy.newProxyInstance(
				JakartaExceptionHandlerTest.class.getClassLoader(),
				new Class[]{ConstraintViolation.class},
				(proxy, method, args) -> {
					if ("getMessage".equals(method.getName())) {
						return message;
					}
					return defaultValue(method.getReturnType());
				}
		);
	}

	private static Object defaultValue(Class<?> returnType) {
		if (!returnType.isPrimitive()) {
			return null;
		}
		if (returnType == boolean.class) {
			return false;
		}
		if (returnType == byte.class) {
			return (byte) 0;
		}
		if (returnType == short.class) {
			return (short) 0;
		}
		if (returnType == int.class) {
			return 0;
		}
		if (returnType == long.class) {
			return 0L;
		}
		if (returnType == float.class) {
			return 0F;
		}
		if (returnType == double.class) {
			return 0D;
		}
		if (returnType == char.class) {
			return '\0';
		}
		return null;
	}

	@Test
	void shouldReturnProblemDetailByDefault() {
		JakartaExceptionHandler handler = new JakartaExceptionHandler(THROWABLE_MESSAGE_SUPPLIER);

		Object result = handler.constraintViolationException(createConstraintViolationException());

		ProblemDetail problemDetail = assertInstanceOf(ProblemDetail.class, result);
		assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
		assertEquals("Parameter validation not pass: [invalid]", problemDetail.getDetail());
		assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getProperties().get("code"));
	}

	@Test
	void shouldReturnResultWhenResponseTypeIsResult() {
		JakartaExceptionHandler handler = new JakartaExceptionHandler(THROWABLE_MESSAGE_SUPPLIER, ResponseType.RESULT);

		Object result = handler.constraintViolationException(createConstraintViolationException());

		Result<?> view = assertInstanceOf(Result.class, result);
		assertEquals(HttpStatus.BAD_REQUEST.value(), view.getCode());
		assertEquals("Parameter validation not pass: [invalid]", view.getMessage());
	}
}
