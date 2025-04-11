package org.zero.common.core.support.api.debounce.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.zero.common.core.support.api.debounce.annotation.Debounce;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/23
 */
@RequiredArgsConstructor
public class RedisTemplateDebounceInterceptor extends BaseDebounceInterceptor {
    @SuppressWarnings("rawtypes")
    protected final RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public boolean isPermit(HttpServletRequest request, HandlerMethod handlerMethod, Debounce debounce) {
        String key = this.getKey(request, handlerMethod, debounce);
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            return false;
        }
        Object value = this.getValue(request, handlerMethod, debounce);
        redisTemplate.boundValueOps(key).set(value, debounce.interval(), debounce.timeUnit());
        return true;
    }
}
