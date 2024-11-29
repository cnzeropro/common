package org.zero.common.core.exception.handler.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.zero.common.core.util.jackson.JacksonUtils;
import org.zero.common.core.util.javax.web.ResponseUtil;
import org.zero.common.data.model.view.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Web MVC 端 Sentinel 统一异常处理
 * <p>
 * 默认实现：{@link com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.DefaultBlockExceptionHandler}
 *
 * @author Zero
 * @since 2022/7/16
 */
@Slf4j
public class CustomBlockExceptionHandler implements BlockExceptionHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) {
        log.warn("Sentinel block exception", e);
        Result<Void> result = SentinelExceptionUtil.exception2Result(e);
        String jsonStr = JacksonUtils.toJsonStr(result);
        ResponseUtil.writeErrorJson(response, jsonStr);
    }
}
