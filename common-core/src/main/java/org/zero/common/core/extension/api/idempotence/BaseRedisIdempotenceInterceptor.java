package org.zero.common.core.extension.api.idempotence;

import org.zero.common.core.aop.aspect.idempotence.Idempotence;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/24
 */
public abstract class BaseRedisIdempotenceInterceptor extends BaseIdempotenceInterceptor {
    /**
     * 缓存 key 的前缀
     */
    public static final String KEY_PREFIX = "sys:api:idempotence";

    /**
     * 获取幂等 key
     * <p>
     * 和生成幂等值的接口中生成的 key 逻辑一致，建议提供静态方法，一起共用
     */
    protected abstract String getKey(HttpServletRequest request, Idempotence idempotence);

    /**
     * 获取幂等 value
     * <p>
     * 建议从 header 里面取，如：request.getHeader("X-Idempotence-Id")
     */
    protected abstract Object getValue(HttpServletRequest request, Idempotence idempotence);
}
