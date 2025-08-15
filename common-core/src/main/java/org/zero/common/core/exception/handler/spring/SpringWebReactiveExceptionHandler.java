package org.zero.common.core.exception.handler.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zero.common.core.exception.ThrowableMessageProvider;
import org.zero.common.core.exception.handler.ThrowableHandler;
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
public class SpringWebReactiveExceptionHandler extends ThrowableHandler {
    public SpringWebReactiveExceptionHandler(ThrowableMessageProvider throwableMessageProvider) {
        super(throwableMessageProvider);
    }

    /* *************************************************** Web MVC 异常 *************************************************** */
    @ExceptionHandler(org.springframework.web.reactive.function.UnsupportedMediaTypeException.class)
    public Result<Void> unsupportedMediaTypeException(org.springframework.web.reactive.function.UnsupportedMediaTypeException e) {
        MediaType contentType = e.getContentType();
        log.error(String.format("The media type[%s] is not supported, only supported: %s", contentType, e.getSupportedMediaTypes()), e);
        return this.handle(HttpStatus.UNSUPPORTED_MEDIA_TYPE, e, Collections.singletonList(contentType));
    }
}
