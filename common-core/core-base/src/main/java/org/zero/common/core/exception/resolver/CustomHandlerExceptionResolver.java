package org.zero.common.core.exception.resolver;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.zero.common.core.exception.handler.ThrowableHandler;
import org.zero.common.data.model.view.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * Spring MVC 异常解析器
 *
 * @author zero
 * @see org.springframework.web.servlet.HandlerExceptionResolver
 * @see org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver
 * @see org.springframework.web.servlet.handler.HandlerExceptionResolverComposite
 * @since 2024/4/12
 */
@RequiredArgsConstructor
public class CustomHandlerExceptionResolver extends DefaultHandlerExceptionResolver {
    protected final ThrowableHandler throwableHandler;

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView modelAndView = super.doResolveException(request, response, handler, ex);
        if (Objects.nonNull(modelAndView)) {
            return modelAndView;
        }
        try {
            return this.handleException(request, response, handler, ex);
        } catch (Exception handlerEx) {
            if (logger.isWarnEnabled()) {
                logger.warn("Failure while trying to resolve exception [" + ex.getClass() + "]", handlerEx);
            }
        }
        return null;
    }

    /**
     * 如果可以处理异常，则返回一个 {@link ModelAndView} 对象，否则返回 {@code null}。
     */
    @SneakyThrows
    protected ModelAndView handleException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        this.sendServerError(ex, request, response);
        Result<Void> result = throwableHandler.handle(ex);
        request.setAttribute("javax.servlet.error.result", result);
        return new ModelAndView("error");
    }
}
