package org.zero.common.core.exception.handler.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.zero.common.core.exception.ThrowableMessageSupplier;
import org.zero.common.core.exception.handler.ThrowableHandler;
import org.zero.common.core.util.jackson.databind.JacksonUtils;
import org.zero.common.core.util.javax.servlet.ResponseUtil;
import org.zero.common.data.model.view.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring Web MVC 侧的 Sentinel 阻塞异常统一处理器。
 * <p>
 * 当请求被 Sentinel 限流、熔断或系统规则拦截时，将异常转换为统一的 JSON 响应。
 *
 * @author Zero (cnzeropro@163.com)
 * @see com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.DefaultBlockExceptionHandler
 * @since 2022/7/16
 */
@Slf4j
public class CustomBlockExceptionHandler extends ThrowableHandler implements BlockExceptionHandler {
	public CustomBlockExceptionHandler(ThrowableMessageSupplier throwableMessageProvider) {
		super(throwableMessageProvider);
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) {
		log.error("Sentinel block exception", e);
		Result<Void> result = this.handle(e);
		String jsonStr = JacksonUtils.toJsonStr(result);
		ResponseUtil.writeErrorJson(response, jsonStr);
	}
}
