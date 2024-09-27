package org.zero.common.core.aop.aspect.idempotence;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
@Aspect
@RequiredArgsConstructor
public abstract class RedisTemplateIdempotenceAspect extends BaseRedisIdempotenceAspect {
    @SuppressWarnings("rawtypes")
    protected final RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    protected boolean needPrevent(JoinPoint joinPoint, Idempotence idempotence) {
        String key = this.getKey(joinPoint, idempotence);
        Object value = this.getValue(joinPoint, idempotence);
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            Object valueRecord = redisTemplate.boundValueOps(key).getAndDelete();
            return !Objects.equals(value, valueRecord);
        }
        // 没有幂等缓存信息，需要阻止
        return true;
    }
}
