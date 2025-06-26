package org.zero.common.core.support.api.throttle.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.springframework.util.StringUtils;
import org.zero.common.core.support.api.throttle.annotation.Throttle;
import org.zero.common.core.support.api.throttle.exception.ThrottleException;
import org.zero.common.core.support.api.throttle.provider.DefaultMessageProvider;
import org.zero.common.core.util.java.lang.reflect.MemberUtil;

/**
 * 限流切面
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
public abstract class BaseThrottleAspect {
    /**
     * 缓存 key 的前缀
     */
    public static final String KEY_PREFIX = "sys:throttle";

    @Before("@annotation(throttle) || @within(throttle)")
    public void before(JoinPoint joinPoint, Throttle throttle) {
        if (throttle.value()) {
            if (!this.isPermit(joinPoint, throttle)) {
                String message = this.getMessage(joinPoint, throttle);
                throw new ThrottleException(message);
            }
        }
    }

    /**
     * 是否放行
     */
    protected abstract boolean isPermit(JoinPoint joinPoint, Throttle throttle);

    /**
     * 获取限流 key
     */
    protected String getKey(JoinPoint joinPoint, Throttle throttle) {
        String key = throttle.key();
        if (StringUtils.hasText(key)) {
            return key;
        }
        // 生成默认 key
        String keyPrefix = this.getKeyPrefix(joinPoint, throttle);
        if (!StringUtils.hasText(keyPrefix)) {
            keyPrefix = KEY_PREFIX;
        }
        String isolationMark = this.getIsolationMark(joinPoint, throttle);
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
    protected String getKeyPrefix(JoinPoint joinPoint, Throttle throttle) {
        return KEY_PREFIX;
    }

    /**
     * 获取隔离标识
     * <p>
     * 建议重写，可返回 token、用户名、客户端 ip 等等作为隔离标识
     */
    protected String getIsolationMark(JoinPoint joinPoint, Throttle throttle) {
        return null;
    }

    /**
     * 获取提示消息
     */
    protected String getMessage(JoinPoint joinPoint, Throttle throttle) {
        return MemberUtil.getInstanceOpt(throttle.messageProvider())
                .map(messageProvider -> messageProvider.generate(joinPoint, throttle))
                .orElse(DefaultMessageProvider.MESSAGE);
    }

    @After("@annotation(throttle) || @within(throttle)")
    public void after(JoinPoint joinPoint, Throttle throttle) {
        this.afterInternal(joinPoint, throttle);
    }

    /**
     * 后置处理
     */
    protected void afterInternal(JoinPoint joinPoint, Throttle throttle) {
        // do nothing
    }
}
