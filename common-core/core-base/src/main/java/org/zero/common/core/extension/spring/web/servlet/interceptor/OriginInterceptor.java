package org.zero.common.core.extension.spring.web.servlet.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;

/**
 * 访问源拦截器
 *
 * @author zero
 * @since 2022/6/23
 */
@Slf4j
@RequiredArgsConstructor
public class OriginInterceptor implements HandlerInterceptor {
    protected final List<String> allowedAddresses;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (CollectionUtils.isEmpty(allowedAddresses)) {
            return true;
        }

        String origin = request.getHeader(HttpHeaders.ORIGIN);
        if (!StringUtils.hasText(origin)) {
            origin = request.getHeader(HttpHeaders.REFERER);
        }

        for (String allowedAddress : allowedAddresses) {
            if (this.match(allowedAddress, origin)) {
                return true;
            }
        }

        log.warn("prohibited request, origin: {}", origin);
        // 拦截器中抛出的异常能被统一异常处理捕获到
        throw new NotAcceptableStatusException("Illegal origin access");
    }

    protected boolean match(String hostPattern, String url) {
        if (StringUtils.hasText(url)) {
            URI uri = URI.create(url);
            String host = uri.getHost();
            return PatternMatchUtils.simpleMatch(hostPattern, host);
        }
        return false;
    }
}
