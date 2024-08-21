package org.zero.common.core.util.spring.web;

import lombok.experimental.UtilityClass;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/11/23
 */
@UtilityClass
public class RequestUtil {
    /**
     * 获取当前HttpServletRequest
     */
    public static HttpServletRequest getHttpServletRequest() {
        return getHttpServletRequestOpt().orElse(null);
    }

    /**
     * 获取当前HttpServletRequest
     */
    public static Optional<HttpServletRequest> getHttpServletRequestOpt() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
    }

    /**
     * protocol :// hostname[:port] / path / [;parameters] [?query] [#fragment]
     * <p>
     * 如：http://127.0.0.1:8080/demo/test?a=bbb
     * 取：http://127.0.0.1:8080/
     */
    public static String getDomain() {
        return getHttpServletRequestOpt()
                .map(RequestUtil::getDomain)
                .orElse(null);
    }

    /**
     * protocol :// hostname[:port] / path / [;parameters] [?query] [#fragment]
     * <p>
     * 如：http://127.0.0.1:8080/demo/test?a=bbb
     * 取：http://127.0.0.1:8080/
     */
    public static String getDomain(HttpServletRequest request) {
        return String.format("%s://%s:%d%s", request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
    }

    public static String getContentType() {
        return getHttpServletRequestOpt()
                .map(RequestUtil::getContentType)
                .orElse(null);
    }

    public static MediaType getMediaType() {
        return getHttpServletRequestOpt()
                .map(RequestUtil::getMediaType)
                .orElse(null);
    }

    public static String getContentType(ServletRequest request) {
        return request.getContentType();
    }

    public static MediaType getMediaType(ServletRequest request) {
        String contentType = getContentType(request);
        return MediaType.valueOf(contentType);
    }
}
