package org.zero.common.core.extension.common.data.model.view;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.zero.common.data.model.view.Result;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/04/15
 */
class ResultResponseBodyAdviceTest {
	private final ResultResponseBodyAdvice advice = new ResultResponseBodyAdvice();

	@Test
	void supportsShouldSkipMethodLevelAnnotation() throws Exception {
		assertFalse(advice.supports(returnType(ResultWrappingController.class, "methodSkip"), jsonConverter()));
	}

	@Test
	void supportsShouldSkipClassLevelAnnotation() throws Exception {
		assertFalse(advice.supports(returnType(ClassLevelSkipController.class, "rawBody"), jsonConverter()));
	}

	@Test
	void supportsShouldSkipConfiguredPackagePrefix() throws Exception {
		ResultResponseBodyAdvice packageSkippingAdvice = new ResultResponseBodyAdvice("org.zero.common.core");

		assertFalse(packageSkippingAdvice.supports(returnType(ResultWrappingController.class, "objectBody"), jsonConverter()));
	}

	@Test
	void beforeBodyWriteShouldWrapObjectBodyForJacksonJson() throws Exception {
		Map<String, Object> body = Collections.<String, Object>singletonMap("value", "wrapped");

		Object wrapped = advice.beforeBodyWrite(
				body,
				returnType(ResultWrappingController.class, "objectBody"),
				APPLICATION_JSON,
				jsonConverter(),
				null,
				null
		);

		Result<?> result = assertInstanceOf(Result.class, wrapped);
		assertEquals("00000", result.getCode());
		assertTrue(result.isSuccess());
		assertSame(body, result.getData());
	}

	@Test
	void beforeBodyWriteShouldKeepExistingResultBody() throws Exception {
		Result<Map<String, Object>> body = Result.ok(Collections.<String, Object>singletonMap("value", "already-result"));

		Object result = advice.beforeBodyWrite(
				body,
				returnType(ResultWrappingController.class, "resultBody"),
				APPLICATION_JSON,
				jsonConverter(),
				null,
				null
		);

		assertSame(body, result);
	}

	@Test
	void beforeBodyWriteShouldKeepStringBody() throws Exception {
		Object result = advice.beforeBodyWrite(
				"plain-text",
				returnType(ResultWrappingController.class, "stringBody"),
				TEXT_PLAIN,
				StringHttpMessageConverter.class,
				null,
				null
		);

		assertEquals("plain-text", result);
	}

	@Test
	void beforeBodyWriteShouldKeepResponseEntityBody() throws Exception {
		Map<String, Object> body = Collections.<String, Object>singletonMap("value", "response-entity");

		Object result = advice.beforeBodyWrite(
				body,
				returnType(ResultWrappingController.class, "responseEntityBody"),
				APPLICATION_JSON,
				jsonConverter(),
				null,
				null
		);

		assertSame(body, result);
	}

	@Test
	void supportsShouldAcceptRegularControllerMethod() throws Exception {
		assertTrue(advice.supports(returnType(ResultWrappingController.class, "objectBody"), jsonConverter()));
	}

	private Class<MappingJackson2HttpMessageConverter> jsonConverter() {
		return MappingJackson2HttpMessageConverter.class;
	}

	private MethodParameter returnType(Class<?> controllerClass, String methodName) throws NoSuchMethodException {
		Method method = controllerClass.getMethod(methodName);
		return new MethodParameter(method, -1);
	}

	public static class ResultWrappingController {
		public Map<String, Object> objectBody() {
			return Collections.<String, Object>singletonMap("value", "wrapped");
		}

		@SkipResultWrapping
		public Map<String, Object> methodSkip() {
			return Collections.<String, Object>singletonMap("value", "method-skip");
		}

		public Result<Map<String, Object>> resultBody() {
			return Result.ok(Collections.<String, Object>singletonMap("value", "already-result"));
		}

		public String stringBody() {
			return "plain-text";
		}

		public ResponseEntity<Map<String, Object>> responseEntityBody() {
			return ResponseEntity.ok(Collections.<String, Object>singletonMap("value", "response-entity"));
		}
	}

	@SkipResultWrapping
	public static class ClassLevelSkipController {
		public Map<String, Object> rawBody() {
			return Collections.<String, Object>singletonMap("value", "class-skip");
		}
	}
}
