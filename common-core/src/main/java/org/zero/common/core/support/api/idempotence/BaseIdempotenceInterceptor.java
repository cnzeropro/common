package org.zero.common.core.support.api.idempotence;

import org.springframework.web.method.HandlerMethod;
import org.zero.common.core.extension.spring.webmvc.AbstractHandlerMethodInterceptor;
import org.zero.common.core.util.jackson.JacksonUtils;
import org.zero.common.core.util.javax.web.ResponseUtil;
import org.zero.common.data.model.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 防重提交拦截器
 *
 * @author zero
 */
public abstract class BaseIdempotenceInterceptor implements AbstractHandlerMethodInterceptor {
    @Override
    public boolean supportsInternal(HandlerMethod handlerMethod) {
        return handlerMethod.hasMethodAnnotation(Idempotence.class);
    }

    @Override
    public boolean preHandleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        Idempotence idempotence = handlerMethod.getMethodAnnotation(Idempotence.class);
        if (Objects.isNull(idempotence)) {
            return true;
        }
        if (!idempotence.value()) {
            return true;
        }
        if (!this.needPrevent(request, idempotence)) {
            return true;
        }
        String jsonStr = JacksonUtils.toJsonStr(Result.error(idempotence.message()));
        ResponseUtil.writeOkJson(response, jsonStr);
        return false;
    }

    /**
     * 验证是否需要阻止
     */
    protected abstract boolean needPrevent(HttpServletRequest request, Idempotence idempotence);
}
