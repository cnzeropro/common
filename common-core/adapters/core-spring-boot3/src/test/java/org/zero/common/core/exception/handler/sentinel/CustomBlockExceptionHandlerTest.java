package org.zero.common.core.exception.handler.sentinel;

import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zero.common.core.exception.ThrowableMessageSupplier;
import org.zero.common.core.exception.handler.ThrowableResponseType;
import org.zero.common.core.util.jackson.databind.JacksonUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link CustomBlockExceptionHandler} 测试。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/15
 */
class CustomBlockExceptionHandlerTest {
	private static final String EXPECTED_MESSAGE = FlowException.class.getCanonicalName();
	private static final ThrowableMessageSupplier THROWABLE_MESSAGE_SUPPLIER = new ThrowableMessageSupplier() {
	};
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
	private ObjectMapper previousObjectMapper;

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

	@BeforeEach
	void setUp() {
		previousObjectMapper = JacksonUtils.getObjectMapper();
	}

	@AfterEach
	void tearDown() {
		JacksonUtils.setObjectMapper(previousObjectMapper);
	}

	@Test
	void shouldWriteProblemDetailJsonByDefault() throws Exception {
		JacksonUtils.setObjectMapper(OBJECT_MAPPER);
		CustomBlockExceptionHandler handler = new CustomBlockExceptionHandler(THROWABLE_MESSAGE_SUPPLIER);
		ResponseRecorder response = new ResponseRecorder();

		handler.handle(null, response.createProxy(), new FlowException("blocked"));
		JsonNode jsonNode = OBJECT_MAPPER.readTree(response.body.toString());

		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.status);
		assertEquals("application/json", response.contentType);
		assertEquals(EXPECTED_MESSAGE, jsonNode.get("detail").asText());
		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, jsonNode.get("status").asInt());
		assertTrue(
			jsonNode.has("code") ||
				(jsonNode.has("properties") && jsonNode.get("properties").has("code"))
		);
	}

	@Test
	void shouldWriteResultJsonWhenResponseTypeIsResult() throws Exception {
		JacksonUtils.setObjectMapper(OBJECT_MAPPER);
		CustomBlockExceptionHandler handler = new CustomBlockExceptionHandler(
			THROWABLE_MESSAGE_SUPPLIER,
			ThrowableResponseType.RESULT
		);
		ResponseRecorder response = new ResponseRecorder();

		handler.handle(null, response.createProxy(), new FlowException("blocked"));
		JsonNode jsonNode = OBJECT_MAPPER.readTree(response.body.toString());

		assertEquals(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response.status);
		assertEquals("application/json", response.contentType);
		assertTrue(jsonNode.has("success"));
		assertFalse(jsonNode.get("success").asBoolean());
		assertEquals(EXPECTED_MESSAGE, jsonNode.get("status").get("message").asText());
	}

	private static final class ResponseRecorder {
		private final StringWriter body = new StringWriter();
		private int status;
		private String contentType;

		private HttpServletResponse createProxy() {
			return (HttpServletResponse) Proxy.newProxyInstance(
				CustomBlockExceptionHandlerTest.class.getClassLoader(),
				new Class[]{HttpServletResponse.class},
				(proxy, method, args) -> {
					String name = method.getName();
					if ("setStatus".equals(name)) {
						status = (Integer) args[0];
						return null;
					}
					if ("setContentType".equals(name)) {
						contentType = (String) args[0];
						return null;
					}
					if ("getWriter".equals(name)) {
						return new PrintWriter(body);
					}
					return defaultValue(method.getReturnType());
				}
			);
		}
	}
}
