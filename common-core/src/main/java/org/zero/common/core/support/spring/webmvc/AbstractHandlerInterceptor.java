package org.zero.common.core.support.spring.webmvc;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/4
 */
public interface AbstractHandlerInterceptor extends HandlerInterceptor {
    /**
     * need to override
     */
    default boolean supports(Object handler) {
        return true;
    }

    @Override
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (this.supports(handler)) {
            return this.preHandleInternal(request, response, handler);
        } else {// 不支持时放行
            return true;
        }
    }

    /**
     * need to override
     */
    default boolean preHandleInternal(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (this.supports(handler)) {
            this.postHandleInternal(request, response, handler, modelAndView);
        }
    }

    /**
     * need to override
     */
    default void postHandleInternal(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (this.supports(handler)) {
            this.afterCompletionInternal(request, response, handler, ex);
        }
    }

    /**
     * need to override
     */
    default void afterCompletionInternal(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
