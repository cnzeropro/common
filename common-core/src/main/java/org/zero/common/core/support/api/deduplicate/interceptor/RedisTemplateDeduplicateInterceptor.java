package org.zero.common.core.support.api.deduplicate.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.zero.common.core.support.api.deduplicate.annotation.Deduplicate;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/23
 */
@RequiredArgsConstructor
public class RedisTemplateDeduplicateInterceptor extends BaseDeduplicateInterceptor {
    @SuppressWarnings("rawtypes")
    protected final RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public boolean isPermit(HttpServletRequest request, HandlerMethod handlerMethod, Deduplicate deduplicate) {
        String key = this.getKey(request, handlerMethod, deduplicate);
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            return false;
        }
        Object value = this.getValue(request, handlerMethod, deduplicate);
        redisTemplate.boundValueOps(key).set(value, deduplicate.interval(), deduplicate.timeUnit());
        return true;
    }
}
