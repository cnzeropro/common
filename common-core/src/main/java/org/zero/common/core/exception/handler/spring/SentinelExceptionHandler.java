package org.zero.common.core.exception.handler.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.zero.common.data.model.view.Result;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/8/30
 */
@Slf4j
@ControllerAdvice
@ConditionalOnWebApplication
public class SentinelExceptionHandler {
    @ExceptionHandler(com.alibaba.csp.sentinel.slots.block.flow.FlowException.class)
    public Result<Void> flowException(com.alibaba.csp.sentinel.slots.block.flow.FlowException e) {
        log.error("Resource has been throttled", e);
        return Result.fail("接口已被限流");
    }

    @ExceptionHandler(com.alibaba.csp.sentinel.slots.block.degrade.DegradeException.class)
    public Result<Void> degradeException(com.alibaba.csp.sentinel.slots.block.degrade.DegradeException e) {
        log.error("Resource has been degraded", e);
        return Result.fail("服务已被降级");
    }

    @ExceptionHandler(com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException.class)
    public Result<Void> paramFlowException(com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException e) {
        log.error("Resource[param] has been throttled", e);
        return Result.fail("热点参数被限流");
    }

    @ExceptionHandler(com.alibaba.csp.sentinel.slots.system.SystemBlockException.class)
    public Result<Void> systemBlockException(com.alibaba.csp.sentinel.slots.system.SystemBlockException e) {
        log.error("Resource[system] has been block", e);
        return Result.fail("触发系统保护规则");
    }

    @ExceptionHandler(com.alibaba.csp.sentinel.slots.block.authority.AuthorityException.class)
    public Result<Void> authorityException(com.alibaba.csp.sentinel.slots.block.authority.AuthorityException e) {
        log.error("Resource not authorized", e);
        return Result.fail("未被授权，请稍后再试");
    }

    @ExceptionHandler(com.alibaba.csp.sentinel.slots.block.BlockException.class)
    public Result<Void> blockException(com.alibaba.csp.sentinel.slots.block.BlockException e) {
        log.error("Resource has been block", e);
        return Result.fail("资源阻塞");
    }
}
