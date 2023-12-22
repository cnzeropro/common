package org.zero.common.core.exception.handler;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zero.common.core.util.javax.web.ResponseUtil;
import org.zero.common.core.util.spring.JacksonUtils;
import org.zero.common.data.model.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 默认实现：{@link com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.DefaultBlockExceptionHandler}
 *
 * @author Zero
 * @since 2022/7/16
 */
@Slf4j
@Component
public class CustomBlockExceptionHandler implements BlockExceptionHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) {
        log.warn("BlockException", e);
        Result<String> result = Result.error("未知异常", e.getMessage());
        if (e instanceof FlowException) {
            result = Result.error("接口已被限流", e.getMessage());
        }
        if (e instanceof DegradeException) {
            result = Result.error("服务已被降级", e.getMessage());
        }
        if (e instanceof ParamFlowException) {
            result = Result.error("热点参数被限流", e.getMessage());
        }
        if (e instanceof SystemBlockException) {
            result = Result.error("触发系统保护规则", e.getMessage());
        }
        if (e instanceof AuthorityException) {
            result = Result.error("未被授权，请稍后再试", e.getMessage());
        }
        String jsonStr = JacksonUtils.toJsonStr(result);
        ResponseUtil.writeErrorJson(response, jsonStr);
    }
}
