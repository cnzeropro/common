package org.zero.common.core.exception.handler.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.zero.common.data.model.view.Result;

/**
 * 异常处理器
 * <p>
 * 异常建议从小到大（便于代码阅读和后期维护）
 *
 * @author Zero
 * @since 2020/03/21
 */
@Slf4j
@ControllerAdvice
@ConditionalOnWebApplication
public class SpringWebReactiveExceptionHandler {
    /* *************************************************** Web MVC 异常 *************************************************** */
    @ExceptionHandler(org.springframework.web.reactive.function.UnsupportedMediaTypeException.class)
    public Result<Void> unsupportedMediaTypeException(org.springframework.web.reactive.function.UnsupportedMediaTypeException e) {
        MediaType contentType = e.getContentType();
        log.error(String.format("The media type[%s] is not supported, only supported: %s", contentType, e.getSupportedMediaTypes()), e);
        return Result.fail(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), String.format("媒体类型（MediaType）不支持：%s", contentType));
    }
}
