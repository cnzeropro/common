package org.zero.common.core.support.api.debounce.interceptor;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.zero.common.core.extension.spring.web.servlet.interceptor.AbstractHandlerMethodInterceptor;
import org.zero.common.core.support.api.debounce.annotation.Debounce;
import org.zero.common.core.support.api.debounce.provider.DefaultMessageProvider;
import org.zero.common.core.util.jackson.databind.JacksonUtils;
import org.zero.common.core.util.java.reflect.MemberUtil;
import org.zero.common.core.util.javax.servlet.ResponseUtil;
import org.zero.common.data.model.view.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 防抖拦截器
 *
 * @author zero
 */
public abstract class BaseDebounceInterceptor implements AbstractHandlerMethodInterceptor {
    /**
     * 缓存 key 的前缀
     */
    public static final String KEY_PREFIX = "sys:api:debounce";

    @Override
    public boolean supportsInternal(HandlerMethod handlerMethod) {
        return AnnotatedElementUtils.hasAnnotation(handlerMethod.getMethod(), Debounce.class);
    }

    @Override
    public boolean preHandleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        Debounce debounce = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), Debounce.class);
        if (Objects.isNull(debounce)) {
            return true;
        }
        if (!debounce.value()) {
            return true;
        }
        if (this.isPermit(request, handlerMethod, debounce)) {
            return true;
        }
        String message = this.getMessage(request, handlerMethod, debounce);
        Result<Void> result = Result.fail(message);
        String jsonStr = JacksonUtils.toJsonStr(result);
        ResponseUtil.writeOkJson(response, jsonStr);
        return false;
    }

    /**
     * 是否允许
     */
    protected abstract boolean isPermit(HttpServletRequest request, HandlerMethod handlerMethod, Debounce debounce);

    /**
     * 获取防抖 key
     */
    protected String getKey(HttpServletRequest request, HandlerMethod handlerMethod, Debounce debounce) {
        String key = debounce.key();
        if (StringUtils.hasText(key)) {
            return key;
        }
        // 生成默认 key
        String keyPrefix = this.getKeyPrefix(request, handlerMethod, debounce);
        if (!StringUtils.hasText(keyPrefix)) {
            keyPrefix = KEY_PREFIX;
        }
        String requestMethod = request.getMethod();
        String requestURI = request.getRequestURI();
        String isolationMark = this.getIsolationMark(request, handlerMethod, debounce);
        if (StringUtils.hasText(isolationMark)) {
            return String.format("%s:%s:%s:%s", keyPrefix, isolationMark, requestMethod, requestURI);
        }
        return String.format("%s:%s:%s", keyPrefix, requestMethod, requestURI);
    }

    /**
     * 获取缓存 key 的前缀
     * <p>
     * 可重写，返回自定义前缀
     */
    protected String getKeyPrefix(HttpServletRequest request, HandlerMethod handlerMethod, Debounce debounce) {
        return KEY_PREFIX;
    }

    /**
     * 获取隔离标识
     * <p>
     * 建议重写，可返回 token、用户名、客户端 ip 等等作为隔离标识
     */
    protected String getIsolationMark(HttpServletRequest request, HandlerMethod handlerMethod, Debounce debounce) {
        return null;
    }

    /**
     * 获取防抖 value
     */
    protected Object getValue(HttpServletRequest request, HandlerMethod handlerMethod, Debounce debounce) {
        return String.format("%s|%s", LocalDateTime.now(), Thread.currentThread());
    }

    /**
     * 获取提示信息
     */
    protected String getMessage(HttpServletRequest request, HandlerMethod handlerMethod, Debounce debounce) {
        return MemberUtil.getInstanceOpt(debounce.messageProvider())
                .map(messageProvider -> messageProvider.generate(handlerMethod, debounce))
                .orElse(DefaultMessageProvider.MESSAGE);
    }
}
