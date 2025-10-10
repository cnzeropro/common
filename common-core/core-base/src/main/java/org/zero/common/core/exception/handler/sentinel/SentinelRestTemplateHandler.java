package org.zero.common.core.exception.handler.sentinel;

import com.alibaba.cloud.sentinel.rest.SentinelClientHttpResponse;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.zero.common.core.exception.handler.ThrowableHandler;
import org.zero.common.core.support.context.spring.SpringUtils;
import org.zero.common.core.util.jackson.databind.JacksonUtils;
import org.zero.common.data.model.view.Result;

import java.io.IOException;
import java.util.Objects;

/**
 * RestTemplate Sentinel 统一异常处理
 *
 * @author Zero (cnzeropro@163.com)
 * @see com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate
 * @since 2024/8/26
 */
@Slf4j
@UtilityClass
public class SentinelRestTemplateHandler {
    /**
     * 限流处理
     */
    public ClientHttpResponse blockHandler(HttpRequest request,
                                           byte[] body,
                                           ClientHttpRequestExecution execution,
                                           BlockException exception) {
        log.warn("Sentinel resource blocked");
        return defaultHandling(request, body, execution, exception);
    }

    /**
     * 降级处理
     */
    public ClientHttpResponse fallback(HttpRequest request,
                                       byte[] body,
                                       ClientHttpRequestExecution execution,
                                       BlockException exception) {
        log.warn("Sentinel resource fallback");
        return defaultHandling(request, body, execution, exception);
    }

    public ClientHttpResponse defaultHandling(HttpRequest request,
                                              byte[] body,
                                              ClientHttpRequestExecution execution,
                                              BlockException exception) {
        log.warn("Sentinel block exception", exception);
        ThrowableHandler throwableHandler = SpringUtils.getBeanProvider(ThrowableHandler.class).getIfAvailable();
        Result<Void> result = Result.error(exception.getMessage());
        if (Objects.nonNull(throwableHandler)) {
            result = throwableHandler.handle(exception);
        }
        String jsonStr = JacksonUtils.toJsonStr(result);
        return new CustomSentinelClientHttpResponse(jsonStr);
    }

    public static class CustomSentinelClientHttpResponse extends SentinelClientHttpResponse {
        public CustomSentinelClientHttpResponse(String body) {
            super(body);
        }

        @Override
        public int getRawStatusCode() throws IOException {
            return HttpStatus.SERVICE_UNAVAILABLE.value();
        }

        @Override
        public String getStatusText() throws IOException {
            return "RestTemplate request block by sentinel";
        }
    }
}
