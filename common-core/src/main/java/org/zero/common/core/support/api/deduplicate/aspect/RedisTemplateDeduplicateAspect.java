package org.zero.common.core.support.api.deduplicate.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.zero.common.core.support.api.deduplicate.annotation.Deduplicate;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
@Aspect
@RequiredArgsConstructor
public class RedisTemplateDeduplicateAspect extends BaseDeduplicateAspect {
    @SuppressWarnings("rawtypes")
    protected final RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public boolean isPermit(JoinPoint joinPoint, Deduplicate deduplicate) {
        String key = this.getKey(joinPoint, deduplicate);
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            return false;
        }
        Object value = this.getValue(joinPoint, deduplicate);
        redisTemplate.boundValueOps(key).set(value, deduplicate.interval(), deduplicate.timeUnit());
        return true;
    }
}
