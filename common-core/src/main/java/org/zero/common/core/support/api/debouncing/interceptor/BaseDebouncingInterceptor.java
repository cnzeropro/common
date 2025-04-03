package org.zero.common.core.support.api.debouncing.interceptor;

import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.zero.common.core.extension.spring.webmvc.AbstractHandlerMethodInterceptor;
import org.zero.common.core.support.api.debouncing.annotation.Debouncing;
import org.zero.common.core.support.api.debouncing.provider.DefaultDebouncingMessageProvider;
import org.zero.common.core.util.jackson.JacksonUtils;
import org.zero.common.core.util.java.reflect.MemberUtil;
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
    /**
     * 缓存 key 的前缀
     */
    public static final String KEY_PREFIX = "sys:api:debouncing";

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
        if (this.isPermit(request, handlerMethod, debouncing)) {
            return true;
        }
        String message = this.getMessage(request, handlerMethod, debouncing);
        String jsonStr = JacksonUtils.toJsonStr(Result.fail(message));
        ResponseUtil.writeOkJson(response, jsonStr);
        return false;
    }

    /**
     * 是否允许
     */
    protected abstract boolean isPermit(HttpServletRequest request, HandlerMethod handlerMethod, Debouncing debouncing);

    /**
     * 获取防抖 key
     */
    protected String getKey(HttpServletRequest request, HandlerMethod handlerMethod, Debouncing debouncing) {
        String key = debouncing.key();
        if (StringUtils.hasText(key)) {
            return key;
        }
        // 生成默认 key
        String keyPrefix = this.getKeyPrefix(request, handlerMethod, debouncing);
        if (!StringUtils.hasText(keyPrefix)) {
            keyPrefix = KEY_PREFIX;
        }
        String requestMethod = request.getMethod();
        String requestURI = request.getRequestURI();
        String mark = this.getMark(request, handlerMethod, debouncing);
        if (StringUtils.hasText(mark)) {
            return String.format("%s:%s:%s:%s", keyPrefix, mark, requestMethod, requestURI);
        }
        return String.format("%s:%s:%s", keyPrefix, requestMethod, requestURI);
    }

    /**
     * 获取缓存 key 的前缀
     * <p>
     * 建议重写，可返回自定义前缀
     */
    protected String getKeyPrefix(HttpServletRequest request, HandlerMethod handlerMethod, Debouncing debouncing) {
        return KEY_PREFIX;
    }

    /**
     * 获取隔离标识
     * <p>
     * 建议重写，可返回 token、用户名、客户端 ip 等等作为隔离标识
     */
    protected String getMark(HttpServletRequest request, HandlerMethod handlerMethod, Debouncing debouncing) {
        return null;
    }

    /**
     * 获取防抖 value
     */
    protected Object getValue(HttpServletRequest request, HandlerMethod handlerMethod, Debouncing debouncing) {
        return String.format("%s|%s", LocalDateTime.now(), Thread.currentThread());
    }

    /**
     * 获取提示信息
     */
    protected String getMessage(HttpServletRequest request, HandlerMethod handlerMethod, Debouncing debouncing) {
        return MemberUtil.getInstanceOpt(debouncing.messageProvider())
                .map(messageProvider -> messageProvider.generate(handlerMethod, debouncing))
                .orElse(DefaultDebouncingMessageProvider.MESSAGE);
    }
}
