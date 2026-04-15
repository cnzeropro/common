package org.zero.common.test.controller;

import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zero.common.core.extension.common.data.model.view.ResultResponseBodyAdvice;
import org.zero.common.core.extension.common.data.model.view.SkipResultWrapping;
import org.zero.common.data.model.view.Result;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/15
 */
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@WebMvcTest(controllers = {
		ResultResponseBodyAdviceTest.ResultWrappingController.class,
		ResultResponseBodyAdviceTest.ClassLevelSkipController.class,
})
@Import({
		ResultResponseBodyAdvice.class,
})
class ResultResponseBodyAdviceTest {
	@Resource
	MockMvc mockMvc;

	/**
	 * 普通对象返回值应自动包装为 Result。
	 */
	@Test
	void wrapsObjectBody() throws Exception {
		mockMvc.perform(get("/result-response-body-advice/object")
						.accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code", IsEqual.equalTo(200), Integer.class))
				.andExpect(jsonPath("$.success").value(TRUE))
				.andExpect(jsonPath("$.data.value").value("wrapped"));
	}

	/**
	 * 方法级跳过注解应生效。
	 */
	@Test
	void skipsMethodLevelWrapping() throws Exception {
		mockMvc.perform(get("/result-response-body-advice/method-skip")
						.accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value").value("method-skip"))
				.andExpect(jsonPath("$.code").doesNotExist());
	}

	/**
	 * 类级跳过注解应生效。
	 */
	@Test
	void skipsClassLevelWrapping() throws Exception {
		mockMvc.perform(get("/result-response-body-advice/class-skip/raw")
						.accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value").value("class-skip"))
				.andExpect(jsonPath("$.code").doesNotExist());
	}

	/**
	 * 已经是 Result 的响应不应重复包装。
	 */
	@Test
	void keepsExistingResultBody() throws Exception {
		mockMvc.perform(get("/result-response-body-advice/result")
						.accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.code", IsEqual.equalTo(200), Integer.class))
				.andExpect(jsonPath("$.success").value(TRUE))
				.andExpect(jsonPath("$.data.value").value("already-result"))
				.andExpect(jsonPath("$.data.data").doesNotExist());
	}

	/**
	 * String 响应不应被包装，避免和 StringHttpMessageConverter 冲突。
	 */
	@Test
	void keepsStringBody() throws Exception {
		mockMvc.perform(get("/result-response-body-advice/string")
						.accept(TEXT_PLAIN))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(TEXT_PLAIN))
				.andExpect(content().string("plain-text"));
	}

	/**
	 * ResponseEntity 响应体应保持原样。
	 */
	@Test
	void keepsResponseEntityBody() throws Exception {
		mockMvc.perform(get("/result-response-body-advice/response-entity")
						.accept(APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.value").value("response-entity"))
				.andExpect(jsonPath("$.code").doesNotExist());
	}

	@RestController
	@RequestMapping("/result-response-body-advice")
	public static class ResultWrappingController {
		@GetMapping("/object")
		public Map<String, Object> objectBody() {
			return Collections.<String, Object>singletonMap("value", "wrapped");
		}

		@SkipResultWrapping
		@GetMapping("/method-skip")
		public Map<String, Object> methodSkip() {
			return Collections.<String, Object>singletonMap("value", "method-skip");
		}

		@GetMapping("/result")
		public Result<Map<String, Object>> resultBody() {
			return Result.ok(Collections.<String, Object>singletonMap("value", "already-result"));
		}

		@GetMapping(value = "/string", produces = MediaType.TEXT_PLAIN_VALUE)
		public String stringBody() {
			return "plain-text";
		}

		@GetMapping("/response-entity")
		public ResponseEntity<Map<String, Object>> responseEntityBody() {
			return ResponseEntity.ok(Collections.<String, Object>singletonMap("value", "response-entity"));
		}
	}

	@SkipResultWrapping
	@RestController
	@RequestMapping("/result-response-body-advice/class-skip")
	public static class ClassLevelSkipController {
		@GetMapping("/raw")
		public Map<String, Object> rawBody() {
			return Collections.<String, Object>singletonMap("value", "class-skip");
		}
	}
}
