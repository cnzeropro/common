package org.zero.common.core.support.xss;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zero.common.core.support.xss.config.XssProperties;
import org.zero.common.core.support.xss.processor.XssMode;
import org.zero.common.core.support.xss.processor.XssProcessor;
import org.zero.common.core.util.java.reflect.MemberUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * XSS 过滤器
 * <p>
 * 支持配置项：{@code sys.web.xss.mode}，参见：{@link XssMode}
 *
 * @author zero
 * @since 2022/2/23
 */
@NoArgsConstructor
@AllArgsConstructor
@EnableConfigurationProperties(XssProperties.class)
public class XssFilter extends OncePerRequestFilter {
    protected static final PathMatcher PATH_MATCHER = new AntPathMatcher();
    protected XssProperties xssProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (xssProperties.isEnabled() && !isExcluded(request)) {
            XssProcessor processor = getXssProcessor();
            filterChain.doFilter(new XssHttpServletRequestWrapper(request, processor, xssProperties.getProcessableUriTypes()), response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    protected XssProcessor getXssProcessor() {
        return MemberUtil.getInstance(xssProperties.getProcessorClass(), true);
    }

    protected boolean isExcluded(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        for (String excludeUri : xssProperties.getExcludeUris()) {
            if (PATH_MATCHER.match(excludeUri, requestURI)) {
                return true;
            }
        }
        return false;
    }
}
