package org.zero.common.core.support.api.debounce.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.springframework.util.StringUtils;
import org.zero.common.core.support.api.debounce.annotation.Debounce;
import org.zero.common.core.support.api.debounce.exception.DebounceException;
import org.zero.common.core.support.api.debounce.provider.DefaultMessageProvider;
import org.zero.common.core.util.java.reflect.MemberUtil;

import java.time.LocalDateTime;

/**
 * 防抖切面
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
public abstract class BaseDebounceAspect {
    /**
     * 缓存 key 的前缀
     */
    public static final String KEY_PREFIX = "sys:debounce";

    @Before("@within(debounce) || " +
            "@annotation(debounce)")
    public void before(JoinPoint joinPoint, Debounce debounce) {
        if (debounce.value()) {
            if (!this.isPermit(joinPoint, debounce)) {
                String message = this.getMessage(joinPoint, debounce);
                throw new DebounceException(message);
            }
        }
    }

    /**
     * 是否放行
     */
    protected abstract boolean isPermit(JoinPoint joinPoint, Debounce debounce);

    /**
     * 获取防抖 key
     */
    protected String getKey(JoinPoint joinPoint, Debounce debounce) {
        String key = debounce.key();
        if (StringUtils.hasText(key)) {
            return key;
        }
        // 生成默认 key
        String keyPrefix = this.getKeyPrefix(joinPoint, debounce);
        if (!StringUtils.hasText(keyPrefix)) {
            keyPrefix = KEY_PREFIX;
        }
        String isolationMark = this.getIsolationMark(joinPoint, debounce);
        Signature signature = joinPoint.getSignature();
        String name = String.format("%s.%s", signature.getDeclaringTypeName(), signature.getName());
        if (StringUtils.hasText(isolationMark)) {
            return String.format("%s:%s:%s", keyPrefix, isolationMark, name);
        }
        return String.format("%s:%s", keyPrefix, name);
    }

    /**
     * 获取缓存 key 的前缀
     * <p>
     * 可重写，返回自定义前缀
     */
    protected String getKeyPrefix(JoinPoint joinPoint, Debounce debounce) {
        return KEY_PREFIX;
    }


    /**
     * 获取隔离标识
     * <p>
     * 建议重写，可返回 token、用户名、客户端 ip 等等作为隔离标识
     */
    protected String getIsolationMark(JoinPoint joinPoint, Debounce debounce) {
        return null;
    }

    /**
     * 获取防抖 value
     */
    protected Object getValue(JoinPoint joinPoint, Debounce debounce) {
        return String.format("%s|%s", LocalDateTime.now(), Thread.currentThread());
    }

    /**
     * 获取提示消息
     */
    protected String getMessage(JoinPoint joinPoint, Debounce debounce) {
        return MemberUtil.getInstanceOpt(debounce.messageProvider())
                .map(messageProvider -> messageProvider.generate(joinPoint, debounce))
                .orElse(DefaultMessageProvider.MESSAGE);
    }

    @After("@within(debounce) || " +
            "@annotation(debounce)")
    public void after(JoinPoint joinPoint, Debounce debounce) {
        this.afterInternal(joinPoint, debounce);
    }

    /**
     * 后置处理
     */
    protected void afterInternal(JoinPoint joinPoint, Debounce debounce) {
        // do nothing
    }
}
