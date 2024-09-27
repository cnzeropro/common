package org.zero.common.core.aop.aspect.deduplication;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.springframework.util.StringUtils;
import org.zero.common.data.exception.CommonException;

import java.time.LocalDateTime;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
public abstract class BaseDeduplicationAspect {
    @Before("@within(deduplication) || " +
            "@annotation(deduplication)")
    public void before(JoinPoint joinPoint, Deduplication deduplication) {
        if (deduplication.value()) {
            if (this.needPrevent(joinPoint, deduplication)) {
                throw new CommonException(deduplication.message());
            }
        }
    }

    @After("@within(deduplication) || " +
            "@annotation(deduplication)")
    public void after(JoinPoint joinPoint, Deduplication deduplication) {
        this.afterInternal(joinPoint, deduplication);
    }

    /**
     * 验证是否需要阻止
     */
    protected abstract boolean needPrevent(JoinPoint joinPoint, Deduplication deduplication);

    /**
     * 后置处理
     */
    protected void afterInternal(JoinPoint joinPoint, Deduplication deduplication) {
        // do nothing
    }

    /**
     * 获取防抖 key
     */
    protected String getKey(JoinPoint joinPoint, Deduplication deduplication) {
        String key = deduplication.key();
        if (StringUtils.hasText(key)) {
            return key;
        }
        return this.getDefaultKey(joinPoint, deduplication);
    }

    /**
     * 获取默认的 key
     */
    protected abstract String getDefaultKey(JoinPoint joinPoint, Deduplication deduplication);

    /**
     * 获取防抖 value
     */
    protected Object getValue(JoinPoint joinPoint, Deduplication deduplication) {
        return LocalDateTime.now().toString();
    }
}
