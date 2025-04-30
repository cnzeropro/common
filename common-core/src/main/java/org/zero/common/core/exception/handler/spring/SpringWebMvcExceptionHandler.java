package org.zero.common.core.exception.handler.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zero.common.core.exception.ThrowableMessageProvider;
import org.zero.common.data.model.view.Result;

import java.util.Collections;

/**
 * 异常处理器
 * <p>
 * 异常建议从小到大（便于代码阅读和后期维护）
 *
 * @author Zero
 * @since 2020/03/21
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication
public class SpringWebMvcExceptionHandler extends AbstractThrowableHandler {
    public SpringWebMvcExceptionHandler(ThrowableMessageProvider throwableMessageProvider) {
        super(throwableMessageProvider);
    }

    /* *************************************************** Web MVC 异常 *************************************************** */
    @ExceptionHandler(org.springframework.web.servlet.ModelAndViewDefiningException.class)
    public Result<Void> modelAndViewDefiningException(org.springframework.web.servlet.ModelAndViewDefiningException e) {
        log.error("Model and view definition exception", e);
        return this.handleThrowable(e);
    }

    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    public Result<Void> noHandlerFoundException(org.springframework.web.servlet.NoHandlerFoundException e) {
        String requestURL = e.getRequestURL();
        log.error(String.format("The request did not find the resource: %s", requestURL), e);
        return this.handleThrowable(HttpStatus.NOT_FOUND, e, Collections.singletonList(requestURL));
    }
}
