package org.zero.common.core.support.api.debounce.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.zero.common.core.support.api.debounce.annotation.Debounce;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
@Aspect
@RequiredArgsConstructor
public class RedisTemplateDebounceAspect extends BaseDebounceAspect {
    @SuppressWarnings("rawtypes")
    protected final RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    protected boolean isPermit(JoinPoint joinPoint, Debounce debounce) {
        String key = this.getKey(joinPoint, debounce);
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            return false;
        }
        Object value = this.getValue(joinPoint, debounce);
        redisTemplate.boundValueOps(key).set(value, debounce.interval(), debounce.timeUnit());
        return true;
    }
}
