package org.zero.common.test.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.zero.common.core.exception.controller.CustomErrorController;
import org.zero.common.core.exception.controller.SpringXmlResult;

import javax.servlet.RequestDispatcher;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_XML;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/15
 */
class CustomErrorControllerTest {
	private static final String ERROR_PATH = "/error";
	private static final String MISSING_PATH = "/__missing_error_endpoint__";

	@Test
	void xmlErrorShouldKeepHttpStatus() throws Exception {
		CustomErrorController controller = createController();
		MockHttpServletRequest request = buildErrorRequest();

		ResponseEntity<SpringXmlResult> response = controller.errorXml(request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getHeaders().getContentType()).isEqualTo(APPLICATION_XML);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getStatus()).isEqualTo(404);
		assertThat(response.getBody().getPath()).isEqualTo(MISSING_PATH);
	}

	@Test
	void jsonErrorShouldKeepHttpStatus() throws Exception {
		CustomErrorController controller = createController();
		MockHttpServletRequest request = buildErrorRequest();

		ResponseEntity<Map<String, Object>> response = controller.errorJson(request);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertThat(response.getBody()).containsEntry("status", 404);
		assertThat(response.getBody()).containsEntry("path", MISSING_PATH);
	}

	private CustomErrorController createController() {
		ErrorAttributes errorAttributes = mock(ErrorAttributes.class);
		when(errorAttributes.getErrorAttributes(any(), any())).thenReturn(buildErrorAttributes());

		return new CustomErrorController(errorAttributes, new ErrorProperties());
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
}
