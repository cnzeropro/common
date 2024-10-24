package org.zero.common.core.extension.api.deduplication;

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
public class RedisTemplateDeduplicationAspect extends BaseRedisDeduplicationAspect {
    @SuppressWarnings("rawtypes")
    protected final RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public boolean needPrevent(JoinPoint joinPoint, Deduplication deduplication) {
        String key = this.getKey(joinPoint, deduplication);
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            return true;
        }
        Object value = this.getValue(joinPoint, deduplication);
        redisTemplate.boundValueOps(key).set(value, deduplication.interval(), deduplication.timeUnit());
        return false;
    }
}
