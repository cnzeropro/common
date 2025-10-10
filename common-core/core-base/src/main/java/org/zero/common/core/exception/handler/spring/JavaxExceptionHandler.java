package org.zero.common.core.exception.handler.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zero.common.core.exception.ThrowableMessageSupplier;
import org.zero.common.core.exception.handler.ThrowableHandler;
import org.zero.common.data.model.view.Result;

import javax.validation.ConstraintViolation;
import java.util.Optional;
import java.util.Set;
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
public class JavaxExceptionHandler extends ThrowableHandler {

    public JavaxExceptionHandler(ThrowableMessageSupplier throwableMessageProvider) {
        super(throwableMessageProvider);
    }

    /* *************************************************** Javax 异常 *************************************************** */
    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    public Result<Void> constraintViolationException(javax.validation.ConstraintViolationException e) {
        String errorMsg = Optional.ofNullable(e.getConstraintViolations())
                .map(Set::stream)
                .orElseGet(Stream::empty)
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(" | ", "[", "]"));
        log.error(String.format("Parameter validation not pass: %s", errorMsg), e);
        return this.handle(HttpStatus.BAD_REQUEST, e, new Object[]{errorMsg});
    }
}
