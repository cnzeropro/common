package org.zero.common.core.exception.handler.spring.webflux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.server.reactive.ServerHttpResponse;
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
public class GlobalExceptionHandler extends ThrowableHandler implements ErrorWebExceptionHandler {
    public GlobalExceptionHandler(ThrowableMessageSupplier throwableMessageProvider) {
        super(throwableMessageProvider);
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("system error", ex);
        // 已经 commit，则直接返回异常
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        // 转换成 Result
        Result<Void> result = this.handle(ex);
        byte[] jsonBytes = JacksonUtils.toJsonBytes(result);
        return ResponseUtil.writeOkJson(response, jsonBytes);
    }
}
