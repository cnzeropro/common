package org.zero.common.core.exception.handler.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zero.common.core.exception.ThrowableMessageProvider;
import org.zero.common.data.model.view.Result;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class SpringExceptionHandler extends AbstractThrowableHandler {
    public SpringExceptionHandler(ThrowableMessageProvider throwableMessageProvider) {
        super(throwableMessageProvider);
    }

    /* *************************************************** Spring 异常 *************************************************** */
    @ExceptionHandler(org.springframework.validation.BindException.class)
    public Result<Void> bindException(org.springframework.validation.BindException e) {
        String errorMsg = Optional.of(e.getAllErrors())
                .map(List::stream)
                .orElseGet(Stream::empty)
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(" | ", "[", "]"));
        log.error(String.format("Data binding exception: %s", errorMsg), e);
        return this.handleThrowable(HttpStatus.BAD_REQUEST, e, new Object[]{errorMsg});
    }
}
