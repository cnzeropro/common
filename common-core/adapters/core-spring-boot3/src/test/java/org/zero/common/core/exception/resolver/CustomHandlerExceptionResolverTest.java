package org.zero.common.core.exception.resolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.web.servlet.ModelAndView;
import org.zero.common.core.exception.ThrowableMessageSupplier;
import org.zero.common.core.exception.handler.ThrowableHandler;
import org.zero.common.core.exception.handler.ThrowableHandler.ResponseType;
import org.zero.common.data.model.view.Result;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * {@link CustomHandlerExceptionResolver} 测试。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/15
 */
class CustomHandlerExceptionResolverTest {
	private static final ThrowableMessageSupplier THROWABLE_MESSAGE_SUPPLIER = new ThrowableMessageSupplier() {
	};

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
	void shouldStoreProblemDetailInRequestAttributeByDefault() {
		AttributeRequest request = new AttributeRequest();
		ResponseStatusRecorder response = new ResponseStatusRecorder();
		TestCustomHandlerExceptionResolver resolver = new TestCustomHandlerExceptionResolver(
				new ThrowableHandler(THROWABLE_MESSAGE_SUPPLIER)
		);

		ModelAndView modelAndView = resolver.invoke(request.createProxy(), response.createProxy(), new IllegalArgumentException("boom"));

		assertEquals("error", modelAndView.getViewName());
		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.status);
		assertInstanceOf(ProblemDetail.class, request.attributes.get("jakarta.servlet.error.result"));
	}

	@Test
	void shouldStoreResultInRequestAttributeWhenResponseTypeIsResult() {
		AttributeRequest request = new AttributeRequest();
		ResponseStatusRecorder response = new ResponseStatusRecorder();
		TestCustomHandlerExceptionResolver resolver = new TestCustomHandlerExceptionResolver(
				new ThrowableHandler(THROWABLE_MESSAGE_SUPPLIER, ResponseType.RESULT)
		);

		ModelAndView modelAndView = resolver.invoke(request.createProxy(), response.createProxy(), new IllegalArgumentException("boom"));

		assertEquals("error", modelAndView.getViewName());
		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.status);
		assertInstanceOf(Result.class, request.attributes.get("jakarta.servlet.error.result"));
	}

	private static final class TestCustomHandlerExceptionResolver extends CustomHandlerExceptionResolver {
		private TestCustomHandlerExceptionResolver(ThrowableHandler throwableHandler) {
			super(throwableHandler);
		}

		private ModelAndView invoke(HttpServletRequest request, HttpServletResponse response, Exception ex) {
			return this.handleException(request, response, null, ex);
		}
	}

	private static final class AttributeRequest {
		private final Map<String, Object> attributes = new HashMap<String, Object>();

		private HttpServletRequest createProxy() {
			return (HttpServletRequest) Proxy.newProxyInstance(
					CustomHandlerExceptionResolverTest.class.getClassLoader(),
					new Class[]{HttpServletRequest.class},
					(proxy, method, args) -> {
						String name = method.getName();
						if ("setAttribute".equals(name)) {
							attributes.put((String) args[0], args[1]);
							return null;
						}
						if ("getAttribute".equals(name)) {
							return attributes.get((String) args[0]);
						}
						if ("removeAttribute".equals(name)) {
							attributes.remove((String) args[0]);
							return null;
						}
						return defaultValue(method.getReturnType());
					}
			);
		}
	}

	private static final class ResponseStatusRecorder {
		private int status;

		private HttpServletResponse createProxy() {
			return (HttpServletResponse) Proxy.newProxyInstance(
					CustomHandlerExceptionResolverTest.class.getClassLoader(),
					new Class[]{HttpServletResponse.class},
					(proxy, method, args) -> {
						String name = method.getName();
						if ("sendError".equals(name) || "setStatus".equals(name)) {
							status = (Integer) args[0];
							return null;
						}
						if ("isCommitted".equals(name)) {
							return false;
						}
						return defaultValue(method.getReturnType());
					}
			);
		}
	}
}
