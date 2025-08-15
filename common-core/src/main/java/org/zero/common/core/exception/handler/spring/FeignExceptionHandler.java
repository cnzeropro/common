package org.zero.common.core.exception.handler.spring;

import feign.Request;
import feign.RequestTemplate;
import feign.Target;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zero.common.core.exception.ThrowableMessageProvider;
import org.zero.common.core.exception.handler.ThrowableHandler;
import org.zero.common.data.model.view.Result;

import java.util.Collections;
import java.util.Optional;

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
public class FeignExceptionHandler extends ThrowableHandler {
    public FeignExceptionHandler(ThrowableMessageProvider throwableMessageProvider) {
        super(throwableMessageProvider);
    }

    /* *************************************************** Feign异常 *************************************************** */

    @ExceptionHandler(feign.codec.EncodeException.class)
    public Result<Void> encodeException(feign.codec.EncodeException e) {
        log.error("Feign encode exception", e);
        return this.handle(e);
    }

    @ExceptionHandler(feign.codec.DecodeException.class)
    public Result<Void> decodeException(feign.codec.DecodeException e) {
        log.error("Feign decode exception", e);
        return this.handle(e);
    }

    @ExceptionHandler(feign.FeignException.class)
    public Result<Void> feignException(feign.FeignException e) {
        log.error("Feign service call failed", e);
        String feignServiceName = Optional.ofNullable(e.request())
                .map(Request::requestTemplate)
                .map(RequestTemplate::feignTarget)
                .map(Target::name)
                .orElse("unknown");
        return this.handle(e.status(), e, Collections.singletonList(feignServiceName));
    }
}
