package org.zero.common.core.extension.spring.cloud.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 修复 {@code The ‘Access-Control-Allow-Origin’ header contains multiple values “*, *”, but only one is allowed.} 问题
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/16
 */
public class CorsResponseHeaderFixFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.defer(() -> {
            HttpHeaders httpHeaders = exchange.getResponse().getHeaders();
            httpHeaders.entrySet()
                    .stream()
                    .filter(entry -> {
                        List<String> value = entry.getValue();
                        return Objects.nonNull(value) && value.size() > 1;
                    })
                    .filter(entry -> {
                        String key = entry.getKey();
                        return key.equals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN) || key.equals(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
                    })
                    .map(Map.Entry::getKey)
                    .forEach(key -> {
                        List<String> values = httpHeaders.get(key);
                        if (!CollectionUtils.isEmpty(values)) {
                            httpHeaders.put(key, Collections.singletonList(values.get(0)));
                        }
                    });
            return chain.filter(exchange);
        }));
    }

    @Override
    public int getOrder() {
        // 指定此过滤器位于 NettyWriteResponseFilter 之后，即待处理完响应体后接着处理响应头
        return NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER + 1;
    }
}
