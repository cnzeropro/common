package org.zero.common.core.support.api.debouncing.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.zero.common.core.support.api.debouncing.annotation.Debouncing;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
@Aspect
@RequiredArgsConstructor
public class RedisTemplateDebouncingAspect extends BaseDebouncingAspect {
    @SuppressWarnings("rawtypes")
    protected final RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    protected boolean isPermit(JoinPoint joinPoint, Debouncing debouncing) {
        String key = this.getKey(joinPoint, debouncing);
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            return false;
        }
        Object value = this.getValue(joinPoint, debouncing);
        redisTemplate.boundValueOps(key).set(value, debouncing.interval(), debouncing.timeUnit());
        return true;
    }
}
