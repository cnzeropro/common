package org.zero.common.core.support.xss;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * XSS 过滤器
 * <p>
 * 支持配置项：{@code sys.web.xss.mode}，参见：{@link Mode}
 *
 * @author zero
 * @since 2022/2/23
 */
public class XssFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Mode mode = this.getEnvironment().getProperty("sys.web.xss.mode", Mode.class, Mode.FILTER);
        filterChain.doFilter(new XssHttpServletRequestWrapper(request, mode), response);
    }
}
