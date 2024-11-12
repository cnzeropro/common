package org.zero.common.core.support.api.deduplication;

import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.zero.common.core.extension.spring.webmvc.AbstractHandlerMethodInterceptor;
import org.zero.common.core.util.jackson.JacksonUtils;
import org.zero.common.core.util.javax.web.ResponseUtil;
import org.zero.common.data.model.vo.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 防重提交拦截器
 *
 * @author zero
 */
public abstract class BaseDeduplicationInterceptor implements AbstractHandlerMethodInterceptor {
    @Override
    public boolean supportsInternal(HandlerMethod handlerMethod) {
        return handlerMethod.hasMethodAnnotation(Deduplication.class);
    }

    @Override
    public boolean preHandleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        Deduplication deduplication = handlerMethod.getMethodAnnotation(Deduplication.class);
        if (Objects.isNull(deduplication)) {
            return true;
        }
        if (!deduplication.value()) {
            return true;
        }
        if (!this.needPrevent(request, deduplication)) {
            return true;
        }
        String jsonStr = JacksonUtils.toJsonStr(Result.error(deduplication.message()));
        ResponseUtil.writeOkJson(response, jsonStr);
        return false;
    }

    /**
     * 验证是否需要阻止
     */
    protected abstract boolean needPrevent(HttpServletRequest request, Deduplication deduplication);

    /**
     * 获取防抖 key
     */
    protected String getKey(HttpServletRequest request, Deduplication deduplication) {
        String key = deduplication.key();
        if (StringUtils.hasText(key)) {
            return key;
        }
        return this.getDefaultKey(request, deduplication);
    }

    /**
     * 获取默认的 key
     */
    protected abstract String getDefaultKey(HttpServletRequest request, Deduplication deduplication);

    /**
     * 获取防抖 value
     */
    protected Object getValue(HttpServletRequest request, Deduplication deduplication) {
        return LocalDateTime.now().toString();
    }
}
