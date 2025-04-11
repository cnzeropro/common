package org.zero.common.core.support.api.throttle.interceptor;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.zero.common.core.extension.spring.webmvc.AbstractHandlerMethodInterceptor;
import org.zero.common.core.support.api.throttle.annotation.Throttle;
import org.zero.common.core.support.api.throttle.provider.DefaultMessageProvider;
import org.zero.common.core.util.jackson.JacksonUtils;
import org.zero.common.core.util.java.reflect.MemberUtil;
import org.zero.common.core.util.javax.servlet.ResponseUtil;
import org.zero.common.data.model.view.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 限流拦截器
 *
 * @author zero
 */
public abstract class BaseThrottleInterceptor implements AbstractHandlerMethodInterceptor {
    /**
     * 缓存 key 的前缀
     */
    public static final String KEY_PREFIX = "sys:api:throttle";

    @Override
    public boolean supportsInternal(HandlerMethod handlerMethod) {
        return AnnotatedElementUtils.hasAnnotation(handlerMethod.getMethod(), Throttle.class);
    }

    @Override
    public boolean preHandleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        Throttle throttle = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), Throttle.class);
        if (Objects.isNull(throttle)) {
            return true;
        }
        if (!throttle.value()) {
            return true;
        }
        if (this.isPermit(request, handlerMethod, throttle)) {
            return true;
        }
        String message = this.getMessage(request, handlerMethod, throttle);
        Result<Void> result = Result.fail(message);
        String jsonStr = JacksonUtils.toJsonStr(result);
        ResponseUtil.writeOkJson(response, jsonStr);
        return false;
    }

    /**
     * 是否允许
     */
    protected abstract boolean isPermit(HttpServletRequest request, HandlerMethod handlerMethod, Throttle throttle);

    /**
     * 获取限流 key
     */
    protected String getKey(HttpServletRequest request, HandlerMethod handlerMethod, Throttle throttle) {
        String key = throttle.key();
        if (StringUtils.hasText(key)) {
            return key;
        }
        // 生成默认 key
        String keyPrefix = this.getKeyPrefix(request, handlerMethod, throttle);
        if (!StringUtils.hasText(keyPrefix)) {
            keyPrefix = KEY_PREFIX;
        }
        String requestMethod = request.getMethod();
        String requestURI = request.getRequestURI();
        String isolationMark = this.getIsolationMark(request, handlerMethod, throttle);
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
    protected String getKeyPrefix(HttpServletRequest request, HandlerMethod handlerMethod, Throttle throttle) {
        return KEY_PREFIX;
    }

    /**
     * 获取隔离标识
     * <p>
     * 建议重写，可返回 token、用户名、客户端 ip 等等作为隔离标识
     */
    protected String getIsolationMark(HttpServletRequest request, HandlerMethod handlerMethod, Throttle throttle) {
        return null;
    }

    /**
     * 获取提示信息
     */
    protected String getMessage(HttpServletRequest request, HandlerMethod handlerMethod, Throttle throttle) {
        return MemberUtil.getInstanceOpt(throttle.messageProvider())
                .map(messageProvider -> messageProvider.generate(handlerMethod, throttle))
                .orElse(DefaultMessageProvider.MESSAGE);
    }
}
