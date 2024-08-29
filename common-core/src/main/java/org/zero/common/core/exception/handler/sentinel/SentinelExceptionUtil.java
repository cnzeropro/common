package org.zero.common.core.exception.handler.sentinel;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import org.zero.common.data.model.vo.Result;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/8/29
 */
public final class SentinelExceptionUtil {
    public static Result<Void> exception2Result(Exception e) {
        if (e instanceof BlockException) {
            if (e instanceof FlowException) {
                return Result.fail("接口已被限流");
            }
            if (e instanceof DegradeException) {
                return Result.fail("服务已被降级");
            }
            if (e instanceof ParamFlowException) {
                return Result.fail("热点参数被限流");
            }
            if (e instanceof SystemBlockException) {
                return Result.fail("触发系统保护规则");
            }
            if (e instanceof AuthorityException) {
                return Result.fail("未被授权，请稍后再试");
            }
            return Result.fail("资源阻塞");
        }
        return Result.fail("未知异常");
    }

    private SentinelExceptionUtil() {
        throw new UnsupportedOperationException();
    }
}
