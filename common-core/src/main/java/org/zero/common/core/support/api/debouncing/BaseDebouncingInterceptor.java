package org.zero.common.core.support.api.debouncing;

import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.zero.common.core.extension.spring.webmvc.AbstractHandlerMethodInterceptor;
import org.zero.common.core.util.jackson.JacksonUtils;
import org.zero.common.core.util.javax.servlet.ResponseUtil;
import org.zero.common.data.model.view.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 防重提交拦截器
 *
 * @author zero
 */
public abstract class BaseDebouncingInterceptor implements AbstractHandlerMethodInterceptor {
    @Override
    public boolean supportsInternal(HandlerMethod handlerMethod) {
        return handlerMethod.hasMethodAnnotation(Debouncing.class);
    }

    @Override
    public boolean preHandleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        Debouncing debouncing = handlerMethod.getMethodAnnotation(Debouncing.class);
        if (Objects.isNull(debouncing)) {
            return true;
        }
        if (!debouncing.value()) {
            return true;
        }
        if (!this.needPrevent(request, debouncing)) {
            return true;
        }
        String jsonStr = JacksonUtils.toJsonStr(Result.fail(debouncing.message()));
        ResponseUtil.writeOkJson(response, jsonStr);
        return false;
    }

    /**
     * 验证是否需要阻止
     */
    protected abstract boolean needPrevent(HttpServletRequest request, Debouncing debouncing);

    /**
     * 获取防抖 key
     */
    protected String getKey(HttpServletRequest request, Debouncing debouncing) {
        String key = debouncing.key();
        if (StringUtils.hasText(key)) {
            return key;
        }
        return this.getDefaultKey(request,debouncing);
    }

    /**
     * 获取默认的 key
     */
    protected abstract String getDefaultKey(HttpServletRequest request, Debouncing debouncing);

    /**
     * 获取防抖 value
     */
    protected Object getValue(HttpServletRequest request, Debouncing debouncing) {
        return LocalDateTime.now().toString();
    }
}
