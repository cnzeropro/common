package org.zero.common.core.util.spring.web.context.request;

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
     * 获取当前 HttpServletRequest
     */
    public static HttpServletRequest getHttpServletRequest() {
        return getHttpServletRequestOpt().orElse(null);
    }

    /**
     * 获取当前 HttpServletRequest
     */
    public static Optional<HttpServletRequest> getHttpServletRequestOpt() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
    }

    /**
     * protocol://host[:port]/path?query#fragment
     * <p>
     * 示例：http://127.0.0.1:8080/demo/test?a=bbb <br>
     * 结果：http://127.0.0.1:8080/demo
     */
    public static String getDomain() {
        return getHttpServletRequestOpt()
                .map(org.zero.common.core.util.javax.servlet.RequestUtil::getDomain)
                .orElse(null);
    }

    /**
     * protocol://host[:port]/path?query#fragment
     * <p>
     * 示例：http://127.0.0.1:8080/demo/test?a=bbb <br>
     * 结果：http://127.0.0.1:8080
     */
    public static String getServerDomain() {
        return getHttpServletRequestOpt()
                .map(org.zero.common.core.util.javax.servlet.RequestUtil::getServerDomain)
                .orElse(null);
    }

    public static String getContentType() {
        return getHttpServletRequestOpt()
                .map(RequestUtil::getContentType)
                .orElse(null);
    }

    private static String getContentType(ServletRequest request) {
        return request.getContentType();
    }

    public static MediaType getMediaType() {
        return getHttpServletRequestOpt()
                .map(RequestUtil::getMediaType)
                .orElse(null);
    }

    public static MediaType getMediaType(ServletRequest request) {
        return Optional.ofNullable(request)
                .map(RequestUtil::getContentType)
                .map(MediaType::valueOf)
                .orElse(null);
    }
}
