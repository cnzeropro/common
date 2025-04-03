package org.zero.common.core.support.api.debouncing.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.springframework.util.StringUtils;
import org.zero.common.core.support.api.debouncing.annotation.Debouncing;
import org.zero.common.core.support.api.debouncing.exception.DebouncingException;
import org.zero.common.core.support.api.debouncing.provider.DefaultDebouncingMessageProvider;
import org.zero.common.core.util.java.reflect.MemberUtil;
import org.zero.common.core.util.spring.web.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
public abstract class BaseDebouncingAspect {
    /**
     * 缓存 key 的前缀
     */
    public static final String KEY_PREFIX = "sys:debouncing";

    @Before("@within(debouncing) || " +
            "@annotation(debouncing)")
    public void before(JoinPoint joinPoint, Debouncing debouncing) {
        if (debouncing.value()) {
            if (!this.isPermit(joinPoint, debouncing)) {
                String message = this.getMessage(joinPoint, debouncing);
                throw new DebouncingException(message);
            }
        }
    }

    /**
     * 是否放行
     */
    protected abstract boolean isPermit(JoinPoint joinPoint, Debouncing debouncing);

    /**
     * 获取防抖 key
     */
    protected String getKey(JoinPoint joinPoint, Debouncing debouncing) {
        String key = debouncing.key();
        if (StringUtils.hasText(key)) {
            return key;
        }
        // 生成默认 key
        String keyPrefix = this.getKeyPrefix(joinPoint, debouncing);
        if (!StringUtils.hasText(keyPrefix)) {
            keyPrefix = KEY_PREFIX;
        }
        String mark = this.getMark(joinPoint, debouncing);
        HttpServletRequest request = RequestUtil.getHttpServletRequest();
        if (Objects.isNull(request)) {
            String name = joinPoint.getSignature().getName();
            if (StringUtils.hasText(mark)) {
                return String.format("%s:%s:%s", keyPrefix, mark, name);
            }
            return String.format("%s:%s", keyPrefix, name);
        }
        String requestMethod = request.getMethod();
        String requestURI = request.getRequestURI();
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
    protected String getKeyPrefix(JoinPoint joinPoint, Debouncing debouncing) {
        return KEY_PREFIX;
    }


    /**
     * 获取隔离标识
     * <p>
     * 建议重写，可返回 token、用户名、客户端 ip 等等作为隔离标识
     */
    protected String getMark(JoinPoint joinPoint, Debouncing debouncing) {
        return null;
    }

    /**
     * 获取防抖 value
     */
    protected Object getValue(JoinPoint joinPoint, Debouncing debouncing) {
        return String.format("%s|%s", LocalDateTime.now(), Thread.currentThread());
    }

    /**
     * 获取提示消息
     */
    protected String getMessage(JoinPoint joinPoint, Debouncing debouncing) {
        return MemberUtil.getInstanceOpt(debouncing.messageProvider())
                .map(messageProvider -> messageProvider.generate(joinPoint, debouncing))
                .orElse(DefaultDebouncingMessageProvider.MESSAGE);
    }

    @After("@within(debouncing) || " +
            "@annotation(debouncing)")
    public void after(JoinPoint joinPoint, Debouncing debouncing) {
        this.afterInternal(joinPoint, debouncing);
    }

    /**
     * 后置处理
     */
    protected void afterInternal(JoinPoint joinPoint, Debouncing debouncing) {
        // do nothing
    }
}
