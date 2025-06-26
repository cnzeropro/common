package org.zero.common.core.support.api.deduplicate.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.springframework.util.StringUtils;
import org.zero.common.core.support.api.deduplicate.annotation.Deduplicate;
import org.zero.common.core.support.api.deduplicate.exception.DeduplicateException;
import org.zero.common.core.support.api.deduplicate.provider.DefaultMessageProvider;
import org.zero.common.core.util.java.lang.reflect.MemberUtil;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 防重切面
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
public abstract class BaseDeduplicateAspect {
    /**
     * 缓存 key 的前缀
     */
    public static final String KEY_PREFIX = "sys:deduplicate";

    @Before("@annotation(deduplicate) || @within(deduplicate)")
    public void before(JoinPoint joinPoint, Deduplicate deduplicate) {
        if (deduplicate.value()) {
            if (!this.isPermit(joinPoint, deduplicate)) {
                String message = this.getMessage(joinPoint, deduplicate);
                throw new DeduplicateException(message);
            }
        }
    }

    /**
     * 是否放行
     */
    protected abstract boolean isPermit(JoinPoint joinPoint, Deduplicate deduplicate);

    /**
     * 获取防重 key
     */
    protected String getKey(JoinPoint joinPoint, Deduplicate deduplicate) {
        String key = deduplicate.key();
        if (StringUtils.hasText(key)) {
            return key;
        }
        // 生成默认 key
        String keyPrefix = this.getKeyPrefix(joinPoint, deduplicate);
        if (!StringUtils.hasText(keyPrefix)) {
            keyPrefix = KEY_PREFIX;
        }
        String isolationMark = this.getIsolationMark(joinPoint, deduplicate);
        String equivalentVoucher = this.getEquivalentVoucher(joinPoint, deduplicate);
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
    protected String getKeyPrefix(JoinPoint joinPoint, Deduplicate deduplicate) {
        return KEY_PREFIX;
    }

    /**
     * 获取隔离标识
     * <p>
     * 建议重写，可返回 token、用户名、客户端 ip 等等作为隔离标识
     */
    protected String getIsolationMark(JoinPoint joinPoint, Deduplicate deduplicate) {
        return null;
    }

    /**
     * 获取等效凭证
     */
    protected String getEquivalentVoucher(JoinPoint joinPoint, Deduplicate deduplicate) {
        return Optional.ofNullable(deduplicate.equivalentVouchers())
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .map(equivalentVoucher -> MemberUtil.getInstance(equivalentVoucher, false))
                .map(equivalentVoucher -> equivalentVoucher.generate(joinPoint, deduplicate))
                .collect(Collectors.joining());
    }

    /**
     * 获取防重 value
     */
    protected Object getValue(JoinPoint joinPoint, Deduplicate deduplicate) {
        return String.format("%s|%s", LocalDateTime.now(), Thread.currentThread());
    }

    protected String getMessage(JoinPoint joinPoint, Deduplicate deduplicate) {
        return MemberUtil.getInstanceOpt(deduplicate.messageProvider())
                .map(messageProvider -> messageProvider.generate(joinPoint, deduplicate))
                .orElse(DefaultMessageProvider.MESSAGE);
    }


    @After("@annotation(deduplicate) || @within(deduplicate)")
    public void after(JoinPoint joinPoint, Deduplicate deduplicate) {
        this.afterInternal(joinPoint, deduplicate);
    }

    /**
     * 后置处理
     */
    protected void afterInternal(JoinPoint joinPoint, Deduplicate deduplicate) {
        // do nothing
    }
}
