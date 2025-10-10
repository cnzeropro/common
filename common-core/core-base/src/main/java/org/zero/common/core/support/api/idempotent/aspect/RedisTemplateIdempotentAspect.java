package org.zero.common.core.support.api.idempotent.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.zero.common.core.support.api.idempotent.annotation.Idempotent;

import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
@Aspect
@RequiredArgsConstructor
public abstract class RedisTemplateIdempotentAspect extends BaseIdempotentAspect {
    @SuppressWarnings("rawtypes")
    protected final RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    protected boolean isPermit(JoinPoint joinPoint, Idempotent idempotent) {
        String key = this.getKey(joinPoint, idempotent);
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            Object value = this.getValue(joinPoint, idempotent);
            Object valueRecord = redisTemplate.boundValueOps(key).getAndDelete();
            return Objects.equals(value, valueRecord);
        }
        // 没有幂等缓存信息，需要阻止
        return false;
    }
}
