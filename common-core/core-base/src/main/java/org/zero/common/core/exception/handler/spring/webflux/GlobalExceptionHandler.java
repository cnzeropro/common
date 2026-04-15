package org.zero.common.core.exception.handler.spring.webflux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.zero.common.core.exception.ThrowableMessageSupplier;
import org.zero.common.core.exception.handler.ThrowableHandler;
import org.zero.common.core.util.jackson.databind.JacksonUtils;
import org.zero.common.core.util.spring.http.server.reactive.ResponseUtil;
import org.zero.common.data.model.view.Result;
import reactor.core.publisher.Mono;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/16
 */
@Slf4j
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class GlobalExceptionHandler extends ThrowableHandler implements ErrorWebExceptionHandler, Ordered {
	private static final int DEFAULT_ORDER = -2;

	public GlobalExceptionHandler(ThrowableMessageSupplier throwableMessageProvider) {
		super(throwableMessageProvider);
	}

	/**
	 * 必须早于 Boot 默认 {@code DefaultErrorWebExceptionHandler(-1)} 和 WebFlux 默认
	 * {@code responseStatusExceptionHandler(0)}，确保异常优先进入统一 JSON 处理链。
	 */
	@Override
	public int getOrder() {
		return DEFAULT_ORDER;
	}

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		log.error("system error", ex);
		ServerHttpResponse response = exchange.getResponse();
		if (response.isCommitted()) {
			return Mono.error(ex);
		}
		HttpStatus httpStatus = resolveHttpStatus(ex);
		CharSequence defaultMessage = resolveDefaultMessage(ex);
		applyResponseHeaders(response, ex);
		Result<Void> result = this.handle(httpStatus, ex, defaultMessage);
		byte[] jsonBytes = JacksonUtils.toJsonBytes(result);
		return ResponseUtil.writeJson(response, jsonBytes, httpStatus);
	}

	private HttpStatus resolveHttpStatus(Throwable ex) {
		for (Throwable current = ex; current != null && current != current.getCause(); current = current.getCause()) {
			if (current instanceof ResponseStatusException) {
				return ((ResponseStatusException) current).getStatus();
			}
			ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(current.getClass(), ResponseStatus.class);
			if (responseStatus != null) {
				return responseStatus.code();
			}
		}
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}

	private CharSequence resolveDefaultMessage(Throwable ex) {
		for (Throwable current = ex; current != null && current != current.getCause(); current = current.getCause()) {
			if (current instanceof ResponseStatusException) {
				String reason = ((ResponseStatusException) current).getReason();
				if (reason != null && !reason.isEmpty()) {
					return reason;
				}
			}
			ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(current.getClass(), ResponseStatus.class);
			if (responseStatus != null) {
				String reason = responseStatus.reason();
				if (reason != null && !reason.isEmpty()) {
					return reason;
				}
			}
		}
		return ex.getMessage();
	}

	private void applyResponseHeaders(ServerHttpResponse response, Throwable ex) {
		for (Throwable current = ex; current != null && current != current.getCause(); current = current.getCause()) {
			if (current instanceof ResponseStatusException) {
				HttpHeaders headers = ((ResponseStatusException) current).getResponseHeaders();
				if (headers != null && !headers.isEmpty()) {
					response.getHeaders().putAll(headers);
				}
				return;
			}
		}
	}
}
