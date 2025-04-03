package org.zero.common.core.support.api.deduplication.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.zero.common.core.support.api.deduplication.annotation.Deduplication;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
@Aspect
@RequiredArgsConstructor
public class RedisTemplateDeduplicationAspect extends BaseDeduplicationAspect {
    @SuppressWarnings("rawtypes")
    protected final RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public boolean isPermit(JoinPoint joinPoint, Deduplication deduplication) {
        String key = this.getKey(joinPoint, deduplication);
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            return false;
        }
        Object value = this.getValue(joinPoint, deduplication);
        redisTemplate.boundValueOps(key).set(value, deduplication.interval(), deduplication.timeUnit());
        return true;
    }
}
