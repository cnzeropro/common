package org.zero.common.core.exception.handler.sentinel;

import com.alibaba.csp.sentinel.adapter.dubbo.fallback.DubboFallback;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.AppResponse;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.zero.common.core.exception.ThrowableMessageProvider;
import org.zero.common.core.exception.handler.ThrowableHandler;
import org.zero.common.data.model.view.Result;

/**
 * Dubbo 端 Sentinel 统一异常处理
 * <p>
 * 默认实现：{@link com.alibaba.csp.sentinel.adapter.dubbo.fallback.DefaultDubboFallback}
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/8/29
 */
@Slf4j
public class CustomDubboFallback extends ThrowableHandler implements DubboFallback {
    public CustomDubboFallback(ThrowableMessageProvider throwableMessageProvider) {
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
