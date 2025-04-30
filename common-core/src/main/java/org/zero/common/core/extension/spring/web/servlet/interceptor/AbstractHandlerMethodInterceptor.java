package org.zero.common.core.extension.spring.web.servlet.interceptor;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 仅支持 {@link HandlerMethod} 的拦截器
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/4
 */
public interface AbstractHandlerMethodInterceptor extends AbstractHandlerInterceptor {
    @Override
    default boolean supports(Object handler) {
        return handler instanceof HandlerMethod && this.supportsInternal((HandlerMethod) handler);
    }

    /**
     * need to override
     */
    default boolean supportsInternal(HandlerMethod handlerMethod) {
        return true;
    }

    @Override
    default boolean preHandleInternal(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return this.preHandleInternal(request, response, (HandlerMethod) handler);
    }

    /**
     * need to override
     */
    default boolean preHandleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        return true;
    }

    @Override
    default void postHandleInternal(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        this.postHandleInternal(request, response, (HandlerMethod) handler, modelAndView);
    }

    /**
     * need to override
     */
    default void postHandleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, ModelAndView modelAndView) throws Exception {
    }

    @Override
    default void afterCompletionInternal(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        this.afterCompletionInternal(request, response, (HandlerMethod) handler, ex);
    }

    /**
     * need to override
     */
    default void afterCompletionInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception ex) throws Exception {
    }
}
