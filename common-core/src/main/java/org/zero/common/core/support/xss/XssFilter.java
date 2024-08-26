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
 * 之所以没有采用自定义序列化器（{@link org.springframework.http.converter.json.MappingJackson2HttpMessageConverter}的{@link com.fasterxml.jackson.databind.ObjectMapper}进行设置），是为了对非JSON类型的参数进行处理，并且保持统一。
 *
 * @author zero
 * @since 2022/2/23
 */
public class XssFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Mode mode = getEnvironment().getProperty("sys.web.xss.mode", Mode.class, Mode.FILTER);
        filterChain.doFilter(new XssHttpServletRequestWrapper(request, mode), response);
    }
}
