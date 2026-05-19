package org.zero.common.core.exception.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.RequestDispatcher;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_XML;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/04/15
 */
class CustomErrorControllerTest {
	private static final String ERROR_PATH = "/error";
	private static final String MISSING_PATH = "/__missing_error_endpoint__";

	@Test
	void xmlErrorShouldKeepHttpStatus() {
		CustomErrorController controller = createController();
		MockHttpServletRequest request = buildErrorRequest();

		ResponseEntity<SpringXmlResult> response = controller.errorXml(request);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals(APPLICATION_XML, response.getHeaders().getContentType());
		assertNotNull(response.getBody());
		assertEquals(Integer.valueOf(404), response.getBody().getStatus());
		assertEquals(MISSING_PATH, response.getBody().getPath());
	}

	@Test
	void jsonErrorShouldKeepHttpStatus() {
		CustomErrorController controller = createController();
		MockHttpServletRequest request = buildErrorRequest();

		ResponseEntity<Map<String, Object>> response = controller.errorJson(request);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(404, response.getBody().get("status"));
		assertEquals(MISSING_PATH, response.getBody().get("path"));
	}

	private CustomErrorController createController() {
		return new CustomErrorController(new FixedErrorAttributes(buildErrorAttributes()), new ErrorProperties());
	}

	private MockHttpServletRequest buildErrorRequest() {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", ERROR_PATH);
		request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 404);
		request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, MISSING_PATH);
		return request;
	}

	private Map<String, Object> buildErrorAttributes() {
		Map<String, Object> body = new LinkedHashMap<String, Object>();
		body.put("timestamp", new Date());
		body.put("status", 404);
		body.put("error", "Not Found");
		body.put("message", "No handler found");
		body.put("path", MISSING_PATH);
		return body;
	}

	private static final class FixedErrorAttributes implements ErrorAttributes {
		private final Map<String, Object> body;

		private FixedErrorAttributes(Map<String, Object> body) {
			this.body = body;
		}

		@Override
		public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
			return body;
		}

		@Override
		public Throwable getError(WebRequest webRequest) {
			return null;
		}
	}
}
