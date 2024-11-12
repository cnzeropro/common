package org.zero.common.core.support.api.debouncing;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
@Aspect
@RequiredArgsConstructor
public class RedisTemplateDebouncingAspect extends BaseRedisDebouncingAspect {
    @SuppressWarnings("rawtypes")
    protected final RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    protected boolean needPrevent(JoinPoint joinPoint, Debouncing debouncing) {
        String key = this.getKey(joinPoint, debouncing);
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            return true;
        }
        Object value = this.getValue(joinPoint, debouncing);
        redisTemplate.boundValueOps(key).set(value, debouncing.interval(), debouncing.timeUnit());
        return false;
    }
}
