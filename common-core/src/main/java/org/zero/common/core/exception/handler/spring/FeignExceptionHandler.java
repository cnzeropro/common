package org.zero.common.core.exception.handler.spring;

import feign.Request;
import feign.RequestTemplate;
import feign.Target;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zero.common.data.model.Result;

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
@RestControllerAdvice(basePackages = "**.controller.**")
@ConditionalOnWebApplication
public class FeignExceptionHandler {
    /* *************************************************** Feign异常 *************************************************** */

    @ExceptionHandler(feign.codec.EncodeException.class)
    public Result<Void> encodeException(feign.codec.EncodeException e) {
        log.error("Feign encode exception", e);
        return Result.fail("编码参数异常");
    }

    @ExceptionHandler(feign.codec.DecodeException.class)
    public Result<Void> decodeException(feign.codec.DecodeException e) {
        log.error("Feign decode exception", e);
        return Result.fail("解码响应异常");
    }

    @ExceptionHandler(feign.FeignException.class)
    public Result<Void> feignException(feign.FeignException e) {
        log.error("Feign service call failed", e);
        String feignServiceName = Optional.ofNullable(e.request())
                .map(Request::requestTemplate)
                .map(RequestTemplate::feignTarget)
                .map(Target::name)
                .orElse("unknown");
        return Result.fail(e.status(), String.format("微服务[%s]调用失败", feignServiceName));
    }
}
