package org.zero.common.core.exception.handler.sentinel;

import com.alibaba.cloud.sentinel.rest.SentinelClientHttpResponse;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.zero.common.core.util.jackson.JacksonUtils;
import org.zero.common.data.model.view.Result;

/**
 * RestTemplate Sentinel 统一异常处理
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/8/26
 */
@Slf4j
public final class SentinelRestTemplateHandler {
    /**
     * 限流处理
     */
    public static ClientHttpResponse blockHandler(HttpRequest request,
                                                  byte[] body,
                                                  ClientHttpRequestExecution execution,
                                                  BlockException exception) {
        log.warn("Sentinel resource blocked");
        return defaultHandling(request, body, execution, exception);
    }

    /**
     * 降级处理
     */
    public static ClientHttpResponse fallback(HttpRequest request,
                                              byte[] body,
                                              ClientHttpRequestExecution execution,
                                              BlockException exception) {
        log.warn("Sentinel resource fallback");
        return defaultHandling(request, body, execution, exception);
    }

    public static ClientHttpResponse defaultHandling(HttpRequest request,
                                                     byte[] body,
                                                     ClientHttpRequestExecution execution,
                                                     BlockException exception) {
        log.warn("Sentinel block exception", exception);
        Result<Void> result = SentinelExceptionUtil.exception2Result(exception);
        String jsonStr = JacksonUtils.toJsonStr(result);
        return new SentinelClientHttpResponse(jsonStr);
    }

    private SentinelRestTemplateHandler() {
        throw new UnsupportedOperationException();
    }
}
