package org.zero.common.core.support.api.debouncing;

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
public abstract class BaseDebouncingAspect {
    @Before("@within(debouncing) || " +
            "@annotation(debouncing)")
    public void before(JoinPoint joinPoint, Debouncing debouncing) {
        if (debouncing.value()) {
            if (this.needPrevent(joinPoint, debouncing)) {
                throw new CommonException(debouncing.message());
            }
        }
    }

    @After("@within(debouncing) || " +
            "@annotation(debouncing)")
    public void after(JoinPoint joinPoint, Debouncing debouncing) {
        this.afterInternal(joinPoint, debouncing);
    }

    /**
     * 验证是否需要阻止
     */
    protected abstract boolean needPrevent(JoinPoint joinPoint, Debouncing debouncing);

    /**
     * 后置处理
     */
    protected void afterInternal(JoinPoint joinPoint, Debouncing debouncing) {
        // do nothing
    }

    /**
     * 获取防抖 key
     */
    protected String getKey(JoinPoint joinPoint, Debouncing debouncing) {
        String key = debouncing.key();
        if (StringUtils.hasText(key)) {
            return key;
        }
        return this.getDefaultKey(joinPoint,debouncing);
    }

    /**
     * 获取默认的 key
     */
    protected abstract String getDefaultKey(JoinPoint joinPoint, Debouncing debouncing);

    /**
     * 获取防抖 value
     */
    protected Object getValue(JoinPoint joinPoint, Debouncing debouncing) {
        return LocalDateTime.now().toString();
    }
}
