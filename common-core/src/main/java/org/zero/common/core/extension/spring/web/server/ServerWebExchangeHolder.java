package org.zero.common.core.extension.spring.web.server;

import lombok.experimental.UtilityClass;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Zero (cnzeropro@163.com)
 * @see org.zero.common.core.extension.spring.web.server.ReactiveExchangeContextFilter
 * @since 2025/7/18
 */
@UtilityClass
public class ServerWebExchangeHolder {
    // 上下文键名（避免命名冲突）
    static final Class<ServerWebExchange> CONTEXT_KEY = ServerWebExchange.class;

    /**
     * 获取当前请求的 {@link ServerWebExchange}
     */
    public static Mono<ServerWebExchange> getExchange() {
        return Mono.deferContextual(contextView -> contextView.hasKey(CONTEXT_KEY)
                ? Mono.just(contextView.get(CONTEXT_KEY))
                : Mono.error(new NullPointerException("No ServerWebExchange found in Reactor Context."))
        );
    }

    /**
     * 获取当前请求的 {@link ServerHttpRequest}
     */
    public static Mono<ServerHttpRequest> getRequest() {
        return getExchange().map(ServerWebExchange::getRequest);
    }

    /**
     * 获取当前请求的 {@link ServerHttpResponse}
     */
    public static Mono<ServerHttpResponse> getResponse() {
        return getExchange().map(ServerWebExchange::getResponse);
    }

    public static <T> Mono<T> getAttribute(String key) {
        return getExchange().flatMap(exchange -> Mono.justOrEmpty(exchange.getAttribute(key)));
    }

    public static Mono<String> getHeader(String headerName) {
        return getRequest().flatMap(request -> Mono.justOrEmpty(request.getHeaders().getFirst(headerName)));
    }

    public static Mono<List<String>> getHeaders(String headerName) {
        return getRequest().flatMap(request -> Mono.just(request.getHeaders().getValuesAsList(headerName)));
    }
}
