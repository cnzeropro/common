package org.zero.common.core.extension.api.idempotence;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 调用需要幂等的接口前，需要先请求获取幂等值的接口，该接口生成幂等值，并将其缓存到 Redis，
 * 然后在调用幂等接口时，可通过 header 等形式携带幂等值，服务端通过幂等值判断，如果两者相同，则请求通过，否则认为其非幂等请求不予通过
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/24
 */
@RequiredArgsConstructor
public abstract class RedisTemplateIdempotenceInterceptor extends BaseRedisIdempotenceInterceptor {
    @SuppressWarnings("rawtypes")
    protected final RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    protected boolean needPrevent(HttpServletRequest request, Idempotence idempotence) {
        String key = this.getKey(request, idempotence);
        Object value = this.getValue(request, idempotence);
        Boolean hasKey = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            Object valueRecord = redisTemplate.boundValueOps(key).getAndDelete();
            return !Objects.equals(value, valueRecord);
        }
        // 没有幂等缓存信息，需要阻止
        return true;
    }
}
