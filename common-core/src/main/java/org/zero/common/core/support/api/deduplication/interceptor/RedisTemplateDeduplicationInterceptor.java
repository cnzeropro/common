package org.zero.common.core.support.api.deduplication.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.zero.common.core.support.api.deduplication.annotation.Deduplication;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/23
 */
@RequiredArgsConstructor
public class RedisTemplateDeduplicationInterceptor extends BaseRedisDeduplicationInterceptor{
    @SuppressWarnings("rawtypes")
    protected final RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public boolean needPrevent(HttpServletRequest request, Deduplication deduplication) {
        String key = this.getKey(request, deduplication);
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            return true;
        }
        Object value = this.getValue(request, deduplication);
        redisTemplate.boundValueOps(key).set(value, deduplication.interval(), deduplication.timeUnit());
        return false;
    }
}
