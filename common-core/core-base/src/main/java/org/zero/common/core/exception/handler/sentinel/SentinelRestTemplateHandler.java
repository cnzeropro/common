package org.zero.common.core.exception.handler.sentinel;

import com.alibaba.cloud.sentinel.rest.SentinelClientHttpResponse;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.UrlCleaner;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.zero.common.core.exception.handler.ThrowableHandler;
import org.zero.common.core.support.context.spring.SpringUtils;
import org.zero.common.core.util.jackson.databind.JacksonUtils;
import org.zero.common.data.model.view.Result;

import java.io.IOException;
import java.util.Objects;

/**
 * RestTemplate 对接 Sentinel 的统一处理器。
 * <p>
 * 提供限流、降级以及 URL 资源名清洗回调，统一输出项目约定的错误响应格式。
 *
 * @author Zero (cnzeropro@163.com)
 * @see com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate
 * @since 2024/8/26
 */
@Slf4j
@UtilityClass
public class SentinelRestTemplateHandler {
	/**
	 * Sentinel 阻塞回调，适用于限流或熔断等被直接拦截的场景。
	 */
	public static ClientHttpResponse blockHandler(HttpRequest request,
												  byte[] body,
												  ClientHttpRequestExecution execution,
												  BlockException exception) {
		log.warn("Sentinel resource blocked");
		return defaultHandling(request, body, execution, exception);
	}

	/**
	 * Sentinel 降级回调，适用于降级规则生效后的兜底返回。
	 */
	public static ClientHttpResponse fallback(HttpRequest request,
											  byte[] body,
											  ClientHttpRequestExecution execution,
											  BlockException exception) {
		log.warn("Sentinel resource fallback");
		return defaultHandling(request, body, execution, exception);
	}

	/**
	 * 对 URL 进行资源名清洗，优先复用 Spring 容器中的 {@link UrlCleaner} 实现。
	 */
	public static String urlCleaner(String url) {
		UrlCleaner urlCleaner = SpringUtils.getBeanProvider(UrlCleaner.class).getIfAvailable();
		if (Objects.nonNull(urlCleaner)) {
			return urlCleaner.clean(url);
		}
		return url;
	}

	/**
	 * 构造统一的 Sentinel 错误响应。
	 */
	public static ClientHttpResponse defaultHandling(HttpRequest request,
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

	/**
	 * 自定义 RestTemplate Sentinel 响应，补充更明确的状态描述。
	 */
	public static class CustomSentinelClientHttpResponse extends SentinelClientHttpResponse {
		public CustomSentinelClientHttpResponse(String body) {
			super(body);
		}

		@Override
		public String getStatusText() throws IOException {
			return "RestTemplate request block by sentinel";
		}
	}
}
