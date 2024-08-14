package org.zero.common.core.exception.resolver;

import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用于处理在处理请求过程中由控制器（Handler/Controller）抛出的未捕获异常
 *
 * @author zero
 * @see org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver
 * @since 2024/4/12
 */
public class CustomHandlerExceptionResolver extends AbstractHandlerExceptionResolver {
    public static final String DEFAULT_EXCEPTION_ATTRIBUTE = "exception";

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            return handleHttpRequestMethodNotSupported((HttpRequestMethodNotSupportedException) ex, request, response, handler);
        } else if (ex instanceof HttpMediaTypeNotSupportedException) {
            return handleHttpMediaTypeNotSupported((HttpMediaTypeNotSupportedException) ex, request, response, handler);
        } else {
            return handleUnknown(ex, request, response, handler);
        }
    }

    @SneakyThrows
    protected ModelAndView handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request, HttpServletResponse response, Object handler) {
        String[] supportedMethods = e.getSupportedMethods();
        if (!ObjectUtils.isEmpty(supportedMethods)) {
            response.setHeader("Allow", StringUtils.arrayToCommaDelimitedString(supportedMethods));
        }
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, e.getMessage());
        return this.getModelAndView("error/405", e);
    }

    @SneakyThrows
    protected ModelAndView handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException e, HttpServletRequest request, HttpServletResponse response, Object handler) {
        List<MediaType> mediaTypes = e.getSupportedMediaTypes();
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            response.setHeader("Accept", MediaType.toString(mediaTypes));
            if (request.getMethod().equals("PATCH")) {
                response.setHeader("Accept-Patch", MediaType.toString(mediaTypes));
            }
        }
        response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        return this.getModelAndView("error/415", e);
    }

    @SneakyThrows
    protected ModelAndView handleUnknown(Exception e, HttpServletRequest request, HttpServletResponse response, Object handler) {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return this.getModelAndView("error/500", e);
    }

    protected ModelAndView getModelAndView(String viewName, Exception e) {
        ModelAndView mv = new ModelAndView(viewName);
        mv.addObject(DEFAULT_EXCEPTION_ATTRIBUTE, e);
        return mv;
    }
}
