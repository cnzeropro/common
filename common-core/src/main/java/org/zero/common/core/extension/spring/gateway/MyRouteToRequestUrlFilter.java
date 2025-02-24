package org.zero.common.core.extension.spring.gateway;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR;

/**
 * 解决浏览器（如：Edge）或服务器发送 GET 等请求时，对保留字符采用宽松模式（即不进行编码）造成已编码部分被双重编码问题。
 * <p>
 * 如：{@code a[0].b=你好}，正常应该是 {@code a%5B0%5D.b=%E4%BD%A0%E5%A5%BD}，
 * 但是 Chrome 没有编码保留字符（{@code a[0].b=%E4%BD%A0%E5%A5%BD}），出现了半编码状态，
 * 导致经过 {@linkplain ServerWebExchangeUtils#containsEncodedParts(URI) containsEncodedParts} 方法时，
 * 由于内部 {@linkplain UriComponentsBuilder#build(boolean) UriComponentsBuilder.build} 抛了异常，因此返回 false（即未编码），
 * 最终导致构建 {@code mergedUrl} 时，进行重复编码（{@code %25E4%25BD%25A0%25E5%25A5%25BD}），因此出现了双重编码问题。
 * <p>
 * 但是，这本质是浏览器或服务器未采用严格模式引起的问题（保留字符在 url 参数中本就应该被编码），因此最好的方式还是手动编码或者前端编码处理后传递。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/2/20
 */
@Slf4j
public class MyRouteToRequestUrlFilter extends RouteToRequestUrlFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        if (route == null) {
            return chain.filter(exchange);
        }
        log.trace("RouteToRequestUrlFilter start");
        URI uri = exchange.getRequest().getURI();
        URI routeUri = route.getUri();

        if (hasAnotherScheme(routeUri)) {
            // this is a special url, save scheme to special attribute
            // replace routeUri with schemeSpecificPart
            exchange.getAttributes().put(GATEWAY_SCHEME_PREFIX_ATTR, routeUri.getScheme());
            routeUri = URI.create(routeUri.getSchemeSpecificPart());
        }

        if ("lb".equalsIgnoreCase(routeUri.getScheme()) && routeUri.getHost() == null) {
            // Load balanced URIs should always have a host. If the host is null it is
            // most
            // likely because the host name was invalid (for example included an
            // underscore)
            throw new IllegalStateException("Invalid host: " + routeUri.toString());
        }

        // 处理 QueryParam 中半编码情况，如：a[0].b=%E4%BD%A0%E5%A5%BD
        // 其中中文的字符（你好）被正常编码（%E4%BD%A0%E5%A5%BD），但方括号（[]）未被编码（%5B%5D）
        // 原因在于其属于保留字符，各家实现不同，因此存在浏览器（如：Edge）或服务器采用宽松模式
        // https://github.com/spring-cloud/spring-cloud-gateway/issues/2065
        // https://datatracker.ietf.org/doc/html/rfc3986#section-2.2
        boolean encoded = hasEncodedQueryParam(uri);
        MultiValueMap<String, String> queryParam;
        if (encoded) {
            queryParam = getQueryParam(uri);
            if (hasInvalidChar(uri)) {
                queryParam = encodedQueryParam(queryParam);
            }
        } else {
            queryParam = encodedQueryParam(getQueryParam(uri));
        }

        URI mergedUrl = UriComponentsBuilder.fromUri(uri)
                // .uri(routeUri)
                .scheme(routeUri.getScheme())
                .host(routeUri.getHost())
                .port(routeUri.getPort())
                .replaceQueryParams(queryParam)
                .build(true)
                .toUri();
        exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, mergedUrl);
        return chain.filter(exchange);
    }

    private static final String SCHEME_REGEX = "[a-zA-Z]([a-zA-Z]|\\d|\\+|\\.|-)*:.*";
    protected static final Pattern schemePattern = Pattern.compile(SCHEME_REGEX);

    /* for testing */
    protected static boolean hasAnotherScheme(URI uri) {
        return schemePattern.matcher(uri.getSchemeSpecificPart()).matches() && uri.getHost() == null
                && uri.getRawPath() == null;
    }


    protected static boolean hasEncodedQueryParam(URI uri) {
        return uri.getRawQuery() != null && uri.getRawQuery().contains("%");
    }

    protected static boolean hasInvalidChar(URI uri) {
        try {
            UriComponentsBuilder.fromUri(uri).build(true);
            return false;
        } catch (IllegalArgumentException ignored) {
            if (log.isTraceEnabled()) {
                log.trace("Error in containsEncodedParts", ignored);
            }
            return true;
        }
    }

    protected static MultiValueMap<String, String> getQueryParam(URI uri) {
        String rawQuery = uri.getRawQuery();
        return UriComponentsBuilder.newInstance().query(rawQuery).build().getQueryParams();
    }

    protected static MultiValueMap<String, String> encodedQueryParam(MultiValueMap<String, String> queryParams) {
        MultiValueMap<String, String> encodedQueryParams = new LinkedMultiValueMap<>(queryParams.size());
        queryParams.forEach((key, values) -> {
            String encodedKey = key;
            if (!isAllowed(key)) {
                encodedKey = UriUtils.encodeQueryParam(key, StandardCharsets.UTF_8);
            }
            for (String value : values) {
                String encodedValue = value;
                if (!isAllowed(value)) {
                    encodedValue = UriUtils.encodeQueryParam(value, StandardCharsets.UTF_8);
                }
                encodedQueryParams.add(encodedKey, encodedValue);
            }
        });
        return encodedQueryParams;
    }

    protected static boolean isAllowed(String source) {
        if (source == null) {
            return true;
        }
        int length = source.length();
        for (int i = 0; i < length; i++) {
            char ch = source.charAt(i);
            if (ch == '%') {
                if ((i + 2) < length) {
                    char hex1 = source.charAt(i + 1);
                    char hex2 = source.charAt(i + 2);
                    int u = Character.digit(hex1, 16);
                    int l = Character.digit(hex2, 16);
                    if (u == -1 || l == -1) {
                        return false;
                    }
                    i += 2;
                } else {
                    return false;
                }
            } else if (!isAllowed(ch)) {
                return false;
            }
        }
        return true;
    }

    protected static final Enum<?> type;
    protected static final Method method;

    static {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Class<?> clazz = Class.forName("org.springframework.web.util.HierarchicalUriComponents$Type", true, classLoader);
            Enum<?>[] enumConstants = (Enum<?>[]) clazz.getEnumConstants();
            type = Arrays.stream(enumConstants)
                    .filter(enumConstant -> "QUERY_PARAM".equalsIgnoreCase(enumConstant.name()))
                    .findFirst()
                    .orElse(null);
            method = clazz.getDeclaredMethod("isAllowed", int.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    protected static boolean isAllowed(int character) {
        method.setAccessible(true);
        Object invoked = method.invoke(type, character);
        return (boolean) invoked;
    }
}
