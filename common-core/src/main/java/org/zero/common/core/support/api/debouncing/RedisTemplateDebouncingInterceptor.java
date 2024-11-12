package org.zero.common.core.support.api.debouncing;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/23
 */
@RequiredArgsConstructor
public class RedisTemplateDebouncingInterceptor extends BaseRedisDebouncingInterceptor {
    @SuppressWarnings("rawtypes")
    protected final RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public boolean needPrevent(HttpServletRequest request, Debouncing debouncing) {
        String key = this.getKey(request, debouncing);
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            return true;
        }
        Object value = this.getValue(request, debouncing);
        redisTemplate.boundValueOps(key).set(value, debouncing.interval(), debouncing.timeUnit());
        return false;
    }
}
