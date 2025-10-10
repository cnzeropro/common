package org.zero.common.core.support.api.throttle.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.zero.common.core.support.api.throttle.annotation.Throttle;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
@Aspect
@RequiredArgsConstructor
public class RedisTemplateThrottleAspect extends BaseThrottleAspect {
    @SuppressWarnings("rawtypes")
    protected final RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    protected boolean isPermit(JoinPoint joinPoint, Throttle throttle) {
        String key = this.getKey(joinPoint, throttle);
        BoundValueOperations<Object, Object> boundValueOperations = redisTemplate.boundValueOps(key);
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            long value = Optional.ofNullable(boundValueOperations.get())
                    // 防止传入的 RedisTemplate 没有配置 long 类型相关的反序列化器，因此尝试 parse
                    .map(Objects::toString)
                    .map(Long::parseLong)
                    .orElse(0L);
            boundValueOperations.setIfAbsent(++value, throttle.interval(), throttle.timeUnit());
            long limit = throttle.limit();
            return value <= limit;
        }
        boundValueOperations.setIfAbsent(1L, throttle.interval(), throttle.timeUnit());
        return true;
    }
}
