package org.zero.common.core.exception.handler.spring.webflux;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import org.zero.common.core.exception.ThrowableMessageSupplier;
import org.zero.common.core.util.jackson.databind.JacksonUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/15
 */
class GlobalExceptionHandlerTest {
	private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
	private final GlobalExceptionHandler handler = new GlobalExceptionHandler(new ThrowableMessageSupplier() {
	});

	@BeforeEach
	void setUp() {
		JacksonUtils.setObjectMapper(objectMapper);
	}

	@AfterEach
	void tearDown() {
		JacksonUtils.setObjectMapper(null);
	}

	@Test
	void shouldReturnInternalServerErrorForRuntimeException() throws Exception {
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/runtime").build());
		RuntimeException exception = new RuntimeException("boom");

		handler.handle(exchange, exception).block();

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getResponse().getStatusCode());
		JsonNode body = readBody(exchange);
		assertEquals(500, body.path("status").path("code").asInt());
		assertEquals("boom", body.path("status").path("message").asText());
	}

	@Test
	void shouldReturnStatusFromResponseStatusException() throws Exception {
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/missing").build());
		ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND, "resource missing");

		handler.handle(exchange, exception).block();

		assertEquals(HttpStatus.NOT_FOUND, exchange.getResponse().getStatusCode());
		JsonNode body = readBody(exchange);
		assertEquals(404, body.path("status").path("code").asInt());
		assertEquals("resource missing", body.path("status").path("message").asText());
	}

	@Test
	void shouldReturnStatusFromResponseStatusAnnotation() throws Exception {
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/bad-request").build());

		handler.handle(exchange, new BadRequestException()).block();

		assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());
		JsonNode body = readBody(exchange);
		assertEquals(400, body.path("status").path("code").asInt());
		assertEquals("bad request", body.path("status").path("message").asText());
	}

	@Test
	void shouldApplyHeadersFromResponseStatusException() {
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/headers").build());
		ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND, "resource missing") {
			@Override
			public HttpHeaders getResponseHeaders() {
				HttpHeaders headers = new HttpHeaders();
				headers.add("X-Trace-Id", "trace-123");
				return headers;
			}
		};

		handler.handle(exchange, exception).block();

		assertEquals("trace-123", exchange.getResponse().getHeaders().getFirst("X-Trace-Id"));
	}

	@Test
	void shouldUseConfiguredOrder() {
		assertEquals(-2, handler.getOrder());
	}

	@Test
	void shouldPropagateExceptionWhenResponseCommitted() {
		MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/committed").build());
		RuntimeException exception = new RuntimeException("committed");
		exchange.getResponse().setComplete().block();

		RuntimeException thrown = assertThrows(RuntimeException.class, () -> handler.handle(exchange, exception).block());

		assertSame(exception, thrown);
	}

	private JsonNode readBody(MockServerWebExchange exchange) throws Exception {
		String body = exchange.getResponse().getBodyAsString().block();
		return objectMapper.readTree(body);
	}

	@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "bad request")
	private static class BadRequestException extends RuntimeException {
	}
}
