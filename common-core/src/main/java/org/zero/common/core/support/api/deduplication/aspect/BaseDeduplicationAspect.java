package org.zero.common.core.support.api.deduplication.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.springframework.util.StringUtils;
import org.zero.common.core.support.api.deduplication.annotation.Deduplication;
import org.zero.common.core.support.api.deduplication.exception.DeduplicationException;
import org.zero.common.core.support.api.deduplication.provider.DefaultDeduplicationMessageProvider;
import org.zero.common.core.util.java.reflect.MemberUtil;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
public abstract class BaseDeduplicationAspect {
    /**
     * 缓存 key 的前缀
     */
    public static final String KEY_PREFIX = "sys:deduplication";

    @Before("@within(deduplication) || " +
            "@annotation(deduplication)")
    public void before(JoinPoint joinPoint, Deduplication deduplication) {
        if (deduplication.value()) {
            if (!this.isPermit(joinPoint, deduplication)) {
                String message = this.getMessage(joinPoint, deduplication);
                throw new DeduplicationException(message);
            }
        }
    }

    /**
     * 是否放行
     */
    protected abstract boolean isPermit(JoinPoint joinPoint, Deduplication deduplication);

    /**
     * 获取防重 key
     */
    protected String getKey(JoinPoint joinPoint, Deduplication deduplication) {
        String key = deduplication.key();
        if (StringUtils.hasText(key)) {
            return key;
        }
        // 生成默认 key
        String keyPrefix = this.getKeyPrefix(joinPoint, deduplication);
        if (!StringUtils.hasText(keyPrefix)) {
            keyPrefix = KEY_PREFIX;
        }
        String mark = this.getMark();
        String equivalentVoucher = this.getEquivalentVoucher(joinPoint, deduplication);
        if (StringUtils.hasText(mark)) {
            return String.format("%s:%s:%s", keyPrefix, mark, equivalentVoucher);
        }
        return String.format("%s:%s", keyPrefix, equivalentVoucher);
    }

    /**
     * 获取缓存 key 的前缀
     * <p>
     * 建议重写，可返回自定义前缀
     */
    protected String getKeyPrefix(JoinPoint joinPoint, Deduplication deduplication) {
        return KEY_PREFIX;
    }

    /**
     * 获取隔离标识
     * <p>
     * 建议重写，可返回 token、用户名、客户端 ip 等等作为隔离标识
     */
    protected String getMark() {
        return null;
    }

    /**
     * 获取等效凭证
     */
    protected String getEquivalentVoucher(JoinPoint joinPoint, Deduplication deduplication) {
        return Optional.ofNullable(deduplication.equivalentVouchers())
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .map(equivalentVoucher -> MemberUtil.getInstance(equivalentVoucher, true))
                .map(equivalentVoucher -> equivalentVoucher.generate(joinPoint, deduplication))
                .collect(Collectors.joining());
    }

    /**
     * 获取防重 value
     */
    protected Object getValue(JoinPoint joinPoint, Deduplication deduplication) {
        return String.format("%s|%s", LocalDateTime.now(), Thread.currentThread());
    }

    protected String getMessage(JoinPoint joinPoint, Deduplication deduplication) {
        return MemberUtil.getInstanceOpt(deduplication.messageProvider())
                .map(messageProvider -> messageProvider.generate(joinPoint, deduplication))
                .orElse(DefaultDeduplicationMessageProvider.MESSAGE);
    }


    @After("@within(deduplication) || " +
            "@annotation(deduplication)")
    public void after(JoinPoint joinPoint, Deduplication deduplication) {
        this.afterInternal(joinPoint, deduplication);
    }

    /**
     * 后置处理
     */
    protected void afterInternal(JoinPoint joinPoint, Deduplication deduplication) {
        // do nothing
    }
}
