package org.zero.common.core.support.api.deduplicate.interceptor;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.zero.common.core.extension.spring.web.servlet.interceptor.AbstractHandlerMethodInterceptor;
import org.zero.common.core.support.api.deduplicate.annotation.Deduplicate;
import org.zero.common.core.support.api.deduplicate.provider.DefaultMessageProvider;
import org.zero.common.core.util.jackson.databind.JacksonUtils;
import org.zero.common.core.util.java.reflect.MemberUtil;
import org.zero.common.core.util.javax.servlet.ResponseUtil;
import org.zero.common.data.model.view.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 防重拦截器
 *
 * @author zero
 */
public abstract class BaseDeduplicateInterceptor implements AbstractHandlerMethodInterceptor {
    /**
     * 缓存 key 的前缀
     */
    public static final String KEY_PREFIX = "sys:api:deduplicate";

    @Override
    public boolean supportsInternal(HandlerMethod handlerMethod) {
        return AnnotatedElementUtils.hasAnnotation(handlerMethod.getMethod(), Deduplicate.class);
    }

    @Override
    public boolean preHandleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        Deduplicate deduplicate = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), Deduplicate.class);
        if (Objects.isNull(deduplicate)) {
            return true;
        }
        if (!deduplicate.value()) {
            return true;
        }
        if (this.isPermit(request, handlerMethod, deduplicate)) {
            return true;
        }
        String message = this.getMessage(request, handlerMethod, deduplicate);
        Result<Void> result = Result.fail(message);
        String jsonStr = JacksonUtils.toJsonStr(result);
        ResponseUtil.writeOkJson(response, jsonStr);
        return false;
    }

    /**
     * 是否放行
     */
    protected abstract boolean isPermit(HttpServletRequest request, HandlerMethod handlerMethod, Deduplicate deduplicate);

    /**
     * 获取防重 key
     */
    protected String getKey(HttpServletRequest request, HandlerMethod handlerMethod, Deduplicate deduplicate) {
        String key = deduplicate.key();
        if (StringUtils.hasText(key)) {
            return key;
        }
        // 生成默认 key
        String keyPrefix = this.getKeyPrefix(request, handlerMethod, deduplicate);
        if (!StringUtils.hasText(keyPrefix)) {
            keyPrefix = KEY_PREFIX;
        }
        String isolationMark = this.getIsolationMark(request, handlerMethod, deduplicate);
        String equivalentVoucher = this.getEquivalentVoucher(request, handlerMethod, deduplicate);
        if (StringUtils.hasText(isolationMark)) {
            return String.format("%s:%s:%s", keyPrefix, isolationMark, equivalentVoucher);
        }
        return String.format("%s:%s", keyPrefix, equivalentVoucher);
    }

    /**
     * 获取缓存 key 的前缀
     * <p>
     * 可重写，返回自定义前缀
     */
    protected String getKeyPrefix(HttpServletRequest request, HandlerMethod handlerMethod, Deduplicate deduplicate) {
        return KEY_PREFIX;
    }

    /**
     * 获取隔离标识
     * <p>
     * 建议重写，可返回 token、用户名、客户端 ip 等等作为隔离标识
     */
    protected String getIsolationMark(HttpServletRequest request, HandlerMethod handlerMethod, Deduplicate deduplicate) {
        return null;
    }

    /**
     * 获取等效凭证
     */
    protected String getEquivalentVoucher(HttpServletRequest request, HandlerMethod handlerMethod, Deduplicate deduplicate) {
        return Optional.ofNullable(deduplicate.equivalentVouchers())
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .map(equivalentVoucher -> MemberUtil.getInstance(equivalentVoucher, false))
                .map(equivalentVoucher -> equivalentVoucher.generate(handlerMethod, deduplicate))
                .collect(Collectors.joining("-"));
    }

    /**
     * 获取防重 value
     */
    protected Object getValue(HttpServletRequest request, HandlerMethod handlerMethod, Deduplicate deduplicate) {
        return String.format("%s|%s", LocalDateTime.now(), Thread.currentThread());
    }

    protected String getMessage(HttpServletRequest request, HandlerMethod handlerMethod, Deduplicate deduplicate) {
        return MemberUtil.getInstanceOpt(deduplicate.messageProvider())
                .map(messageProvider -> messageProvider.generate(handlerMethod, deduplicate))
                .orElse(DefaultMessageProvider.MESSAGE);
    }
}
