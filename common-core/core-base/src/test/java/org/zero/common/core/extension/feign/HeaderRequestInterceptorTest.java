package org.zero.common.core.extension.feign;

import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link HeaderRequestInterceptor} 测试。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/15
 */
class HeaderRequestInterceptorTest {
	private static Map.Entry<String, String> header(String name, String value) {
		return new java.util.AbstractMap.SimpleEntry<String, String>(name, value);
	}

	@SafeVarargs
	private static HttpServletRequest createRequest(Map.Entry<String, String>... headers) {
		Map<String, String> values = new LinkedHashMap<String, String>();
		for (Map.Entry<String, String> header : headers) {
			values.put(header.getKey(), header.getValue());
		}
		return (HttpServletRequest) Proxy.newProxyInstance(
				HeaderRequestInterceptorTest.class.getClassLoader(),
				new Class[]{HttpServletRequest.class},
				(proxy, method, args) -> {
					String name = method.getName();
					if ("getHeader".equals(name)) {
						return getHeader(values, (String) args[0]);
					}
					if ("getHeaderNames".equals(name)) {
						return Collections.enumeration(new ArrayList<String>(values.keySet()));
					}
					return defaultValue(method.getReturnType());
				}
		);
	}

	private static String getHeader(Map<String, String> values, String headerName) {
		for (Map.Entry<String, String> entry : values.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(headerName)) {
				return entry.getValue();
			}
		}
		return null;
	}

	private static void setCurrentRequest(HttpServletRequest request) {
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	}

	private static void assertHeader(RequestTemplate requestTemplate, String headerName, String expectedValue) {
		Collection<String> values = requestTemplate.headers().get(headerName);
		assertEquals(Collections.singletonList(expectedValue), new ArrayList<String>(values));
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

	@AfterEach
	void tearDown() {
		RequestContextHolder.resetRequestAttributes();
	}

	@Test
	void shouldKeepIncludeModeForLegacyConstructor() {
		setCurrentRequest(createRequest(header("X-Trace-Id", "trace"), header("X-Ignored", "ignored")));
		RequestTemplate requestTemplate = new RequestTemplate();
		HeaderRequestInterceptor interceptor = new HeaderRequestInterceptor("X-Trace-Id");

		interceptor.apply(requestTemplate);

		assertHeader(requestTemplate, "X-Trace-Id", "trace");
		assertNull(requestTemplate.headers().get("X-Ignored"));
	}

	@Test
	void shouldExcludeConfiguredHeaders() {
		setCurrentRequest(createRequest(header("X-Trace-Id", "trace"), header("X-Token", "token")));
		RequestTemplate requestTemplate = new RequestTemplate();
		HeaderRequestInterceptor interceptor = new HeaderRequestInterceptor(HeaderRequestInterceptor.Mode.EXCLUDE, "X-Token");

		interceptor.apply(requestTemplate);

		assertHeader(requestTemplate, "X-Trace-Id", "trace");
		assertNull(requestTemplate.headers().get("X-Token"));
	}

	@Test
	void shouldMatchHeaderNamesIgnoringCase() {
		setCurrentRequest(createRequest(header("X-Trace-Id", "trace"), header("X-Token", "token")));
		RequestTemplate requestTemplate = new RequestTemplate();
		HeaderRequestInterceptor interceptor = new HeaderRequestInterceptor(HeaderRequestInterceptor.Mode.EXCLUDE, "x-trace-id");

		interceptor.apply(requestTemplate);

		assertNull(requestTemplate.headers().get("X-Trace-Id"));
		assertHeader(requestTemplate, "X-Token", "token");
	}

	@Test
	void shouldHandleEmptyHeaderListForBothModes() {
		setCurrentRequest(createRequest(header("X-Trace-Id", "trace")));
		RequestTemplate includeTemplate = new RequestTemplate();
		RequestTemplate excludeTemplate = new RequestTemplate();

		new HeaderRequestInterceptor().apply(includeTemplate);
		new HeaderRequestInterceptor(HeaderRequestInterceptor.Mode.EXCLUDE).apply(excludeTemplate);

		assertTrue(includeTemplate.headers().isEmpty());
		assertHeader(excludeTemplate, "X-Trace-Id", "trace");
	}

	@Test
	void shouldSkipWhenRequestContextIsMissing() {
		RequestTemplate requestTemplate = new RequestTemplate();
		HeaderRequestInterceptor interceptor = new HeaderRequestInterceptor("X-Trace-Id");

		interceptor.apply(requestTemplate);

		assertTrue(requestTemplate.headers().isEmpty());
	}

	@Test
	void shouldDeduplicateConfiguredHeaders() {
		setCurrentRequest(createRequest(header("X-Trace-Id", "trace")));
		RequestTemplate requestTemplate = new RequestTemplate();
		HeaderRequestInterceptor interceptor = new HeaderRequestInterceptor("X-Trace-Id", "x-trace-id", " X-Trace-Id ");

		interceptor.apply(requestTemplate);

		assertHeader(requestTemplate, "X-Trace-Id", "trace");
		assertEquals(1, requestTemplate.headers().get("X-Trace-Id").size());
	}
}
