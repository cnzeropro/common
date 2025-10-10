package org.zero.common.core.support.api.idempotent.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.zero.common.core.support.api.idempotent.annotation.Idempotent;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/24
 */
@RequiredArgsConstructor
public class RedisTemplateIdempotentInterceptor extends BaseIdempotentInterceptor {
    @SuppressWarnings("rawtypes")
    protected final RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    protected boolean isPermit(HttpServletRequest request, HandlerMethod handlerMethod, Idempotent idempotent) {
        String key = this.getKey(request, handlerMethod, idempotent);
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            Object value = this.getValue(request, handlerMethod, idempotent);
            Object valueRecord = redisTemplate.boundValueOps(key).getAndDelete();
            return Objects.equals(value, valueRecord);
        }
        // 没有幂等缓存信息，需要阻止
        return false;
    }
}
