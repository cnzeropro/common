package org.zero.common.core.exception.handler.sentinel;

import com.alibaba.csp.sentinel.adapter.dubbo.fallback.DubboFallback;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.AppResponse;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.zero.common.core.exception.ThrowableMessageSupplier;
import org.zero.common.core.exception.handler.ThrowableHandler;
import org.zero.common.data.model.view.Result;

/**
 * Dubbo 2 侧的 Sentinel 阻塞异常统一处理器。
 * <p>
 * 当 Dubbo 调用被 Sentinel 拦截时，将异常转换为统一的返回值结构并写入 RPC 响应。
 *
 * @author Zero (cnzeropro@163.com)
 * @see com.alibaba.csp.sentinel.adapter.dubbo.fallback.DefaultDubboFallback
 * @since 2024/8/29
 */
@Slf4j
public class CustomDubboFallback extends ThrowableHandler implements DubboFallback {
	public CustomDubboFallback(ThrowableMessageSupplier throwableMessageProvider) {
		super(throwableMessageProvider);
	}

	@Override
	public org.apache.dubbo.rpc.Result handle(Invoker<?> invoker, Invocation invocation, BlockException ex) {
		log.warn("Sentinel block exception", ex);
		AppResponse appResponse = new AppResponse(invocation);
		appResponse.setException(ex);
		Result<Void> result = this.handle(ex);
		appResponse.setValue(result);
		return AsyncRpcResult.newDefaultAsyncResult(appResponse, invocation);
	}
}
