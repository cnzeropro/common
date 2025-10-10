package org.zero.common.core.extension.spring.web.server;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/18
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ReactiveExchangeContextFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
                // 将 ServerWebExchange 存入 Reactor Context
                .contextWrite(Context.of(ServerWebExchangeHolder.CONTEXT_KEY, exchange));
    }
}