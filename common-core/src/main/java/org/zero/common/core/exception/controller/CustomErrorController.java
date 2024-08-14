package org.zero.common.core.exception.controller;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 用于统一处理所有HTTP错误，包括但不限于由控制器抛出的异常所引起的错误
 *
 * @author zero
 * @see org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController
 * @see org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration#basicErrorController(ErrorAttributes, ObjectProvider)
 * @since 2024/4/12
 */
@RequestMapping("${server.error.path:${error.path:/error}}")
public class CustomErrorController extends AbstractErrorController {
    protected final ErrorProperties errorProperties;

    public CustomErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties) {
        super(errorAttributes);
        this.errorProperties = errorProperties;
    }

    public CustomErrorController(ErrorAttributes errorAttributes, List<ErrorViewResolver> errorViewResolvers, ErrorProperties errorProperties) {
        super(errorAttributes, errorViewResolvers);
        this.errorProperties = errorProperties;
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = this.getStatus(request);
        ErrorAttributeOptions errorAttributeOptions = getErrorAttributeOptions(request);
        Map<String, Object> model = this.getErrorAttributes(request, errorAttributeOptions);
        response.setStatus(status.value());
        ModelAndView modelAndView = this.resolveErrorView(request, response, status, model);
        return Objects.nonNull(modelAndView) ? modelAndView : new ModelAndView("error", model);
    }

    @RequestMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Map<String, Object>> errorText(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity<>(status);
        }
        ErrorAttributeOptions errorAttributeOptions = getErrorAttributeOptions(request);
        Map<String, Object> body = this.getErrorAttributes(request, errorAttributeOptions);
        return new ResponseEntity<>(body, status);
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> errorJson(HttpServletRequest request) {
        return errorText(request);
    }

    protected ErrorAttributeOptions getErrorAttributeOptions(HttpServletRequest request) {
        ErrorAttributeOptions options = ErrorAttributeOptions.defaults();
        if (this.errorProperties.isIncludeException()) {
            options.including(ErrorAttributeOptions.Include.EXCEPTION);
        }
        if (isIncludeStackTrace(request)) {
            options.including(ErrorAttributeOptions.Include.STACK_TRACE);
        }
        if (isIncludeMessage(request)) {
            options.including(ErrorAttributeOptions.Include.MESSAGE);
        }
        if (isIncludeBindingErrors(request)) {
            options.including(ErrorAttributeOptions.Include.BINDING_ERRORS);
        }
        return options;
    }

    protected boolean isIncludeStackTrace(HttpServletRequest request) {
        switch (this.errorProperties.getIncludeStacktrace()) {
            case ALWAYS:
                return true;
            case ON_PARAM:
                return getTraceParameter(request);
            default:
                return false;
        }
    }

    protected boolean isIncludeMessage(HttpServletRequest request) {
        switch (this.errorProperties.getIncludeMessage()) {
            case ALWAYS:
                return true;
            case ON_PARAM:
                return getMessageParameter(request);
            default:
                return false;
        }
    }

    protected boolean isIncludeBindingErrors(HttpServletRequest request) {
        switch (this.errorProperties.getIncludeBindingErrors()) {
            case ALWAYS:
                return true;
            case ON_PARAM:
                return getErrorsParameter(request);
            default:
                return false;
        }
    }
}
