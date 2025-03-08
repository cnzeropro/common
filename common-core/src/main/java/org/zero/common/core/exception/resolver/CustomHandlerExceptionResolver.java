package org.zero.common.core.exception.resolver;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring MVC 异常解析器
 *
 * @author zero
 * @see org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver
 * @see org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver
 * @see org.springframework.web.servlet.handler.HandlerExceptionResolverComposite
 * @since 2024/4/12
 */
public class CustomHandlerExceptionResolver extends AbstractHandlerExceptionResolver {
    /**
     * 如果可以解析异常，则返回一个 ModelAndView 对象，否则返回 null。
     */
    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        return null;
    }
}
