package org.zero.common.core.exception.handler.sentinel;

import com.alibaba.csp.sentinel.adapter.dubbo3.fallback.DubboFallback;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.AppResponse;
import org.apache.dubbo.rpc.AsyncRpcResult;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.zero.common.data.model.vo.Result;

/**
 * Dubbo3 端 Sentinel 统一异常处理
 * <p>
 * 默认实现：{@link com.alibaba.csp.sentinel.adapter.dubbo3.fallback.DefaultDubboFallback}
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/8/29
 */
@Slf4j
public class CustomDubbo3Fallback implements DubboFallback {

    @Override
    public org.apache.dubbo.rpc.Result handle(Invoker<?> invoker, Invocation invocation, BlockException ex) {
        log.warn("Sentinel block exception", ex);
        AppResponse appResponse = new AppResponse(invocation);
        appResponse.setException(ex);
        Result<Void> result = SentinelExceptionUtil.exception2Result(ex);
        appResponse.setValue(result);
        return AsyncRpcResult.newDefaultAsyncResult(appResponse, invocation);
    }
}
