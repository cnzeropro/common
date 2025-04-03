package org.zero.common.core.support.api.debouncing.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.zero.common.core.support.api.debouncing.annotation.Debouncing;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/23
 */
@RequiredArgsConstructor
public class RedisTemplateDebouncingInterceptor extends BaseDebouncingInterceptor {
    @SuppressWarnings("rawtypes")
    protected final RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public boolean isPermit(HttpServletRequest request, HandlerMethod handlerMethod, Debouncing debouncing) {
        String key = this.getKey(request, handlerMethod, debouncing);
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            return false;
        }
        Object value = this.getValue(request, handlerMethod, debouncing);
        redisTemplate.boundValueOps(key).set(value, debouncing.interval(), debouncing.timeUnit());
        return true;
    }
}
