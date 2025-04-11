package org.zero.common.core.support.api.throttle.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.zero.common.core.support.api.throttle.annotation.Throttle;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/23
 */
@RequiredArgsConstructor
public class RedisTemplateThrottleInterceptor extends BaseThrottleInterceptor {
    @SuppressWarnings("rawtypes")
    protected final RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public boolean isPermit(HttpServletRequest request, HandlerMethod handlerMethod, Throttle throttle) {
        String key = this.getKey(request, handlerMethod, throttle);
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
