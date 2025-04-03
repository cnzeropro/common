package org.zero.common.core.extension.spring.webmvc;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 支持 {@linkplain #supports(Object) supports} 方法判断是否可以拦截的拦截器
 *
 * @author Zero (cnzeropro@163.com)
 * @see org.springframework.web.servlet.HandlerExecutionChain
 * @see org.springframework.web.servlet.DispatcherServlet#doDispatch(HttpServletRequest, HttpServletResponse)
 * @since 2024/9/4
 */
public interface AbstractHandlerInterceptor extends HandlerInterceptor {
    /**
     * need to override
     * <p>
     * 处理器常见类型
     * <ul>
     *     <li>{@linkplain org.springframework.web.method.HandlerMethod HandlerMethod}：注解驱动的 Controller 方法（如 @RequestMapping）</li>
     *     <li>{@linkplain org.springframework.web.servlet.mvc.Controller Controller} 实例：传统 Controller（实现 Controller 接口）</li>
     *     <li>{@linkplain org.springframework.web.HttpRequestHandler HttpRequestHandler} 实例：直接实现 HttpRequestHandler 接口的处理器</li>
     *     <li>{@linkplain org.springframework.web.servlet.HandlerAdapter HandlerAdapter} 实例：通过自定义 HandlerAdapter 支持的处理器</li>
     * </ul>
     */
    default boolean supports(Object handler) {
        return true;
    }

    @Override
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (this.supports(handler)) {
            return this.preHandleInternal(request, response, handler);
        } else {
            // 不支持时放行
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
