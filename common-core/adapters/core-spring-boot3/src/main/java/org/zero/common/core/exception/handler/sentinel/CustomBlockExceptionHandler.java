package org.zero.common.core.exception.handler.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.zero.common.core.exception.ThrowableMessageSupplier;
import org.zero.common.core.exception.handler.ThrowableHandler;
import org.zero.common.core.util.jackson.databind.JacksonUtils;
import org.zero.common.core.util.javax.servlet.ResponseUtil;
import org.zero.common.data.model.view.Result;

/**
 * Web MVC 端 Sentinel 统一异常处理
 * <p>
 * 默认实现：{@link com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.DefaultBlockExceptionHandler}
 *
 * @author Zero
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
