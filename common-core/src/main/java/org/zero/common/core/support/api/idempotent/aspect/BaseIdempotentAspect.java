package org.zero.common.core.support.api.idempotent.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.zero.common.core.support.api.idempotent.annotation.Idempotent;
import org.zero.common.core.support.api.idempotent.exception.IdempotentException;
import org.zero.common.core.support.api.idempotent.provider.DefaultMessageProvider;
import org.zero.common.core.util.java.reflect.MemberUtil;

/**
 * 幂等切面
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
public abstract class BaseIdempotentAspect {
    /**
     * 缓存 key 的前缀
     */
    public static final String KEY_PREFIX = "sys:idempotent";

    @Before("@within(idempotent) || " +
            "@annotation(idempotent)")
    public void before(JoinPoint joinPoint, Idempotent idempotent) {
        if (idempotent.value()) {
            if (!this.isPermit(joinPoint, idempotent)) {
                String message = this.getMessage(joinPoint, idempotent);
                throw new IdempotentException(message);
            }
        }
    }

    /**
     * 是否放行
     */
    protected abstract boolean isPermit(JoinPoint joinPoint, Idempotent idempotent);

    /**
     * 获取幂等 key
     * <p>
     * 需要和生成幂等值的接口中生成的 key 逻辑一致
     */
    protected abstract String getKey(JoinPoint joinPoint, Idempotent idempotent);

    /**
     * 获取幂等 value
     * <p>
     * 建议从 header 里面取，如：{@code request.getHeader("X-Idempotence-Id")}
     */
    protected abstract Object getValue(JoinPoint joinPoint, Idempotent idempotent);

    /**
     * 获取提示消息
     */
    protected String getMessage(JoinPoint joinPoint, Idempotent idempotent) {
        return MemberUtil.getInstanceOpt(idempotent.messageProvider())
                .map(messageProvider -> messageProvider.generate(joinPoint, idempotent))
                .orElse(DefaultMessageProvider.MESSAGE);
    }

    @After("@within(idempotent) || " +
            "@annotation(idempotent)")
    public void after(JoinPoint joinPoint, Idempotent idempotent) {
        this.afterInternal(joinPoint, idempotent);
    }

    /**
     * 后置处理
     */
    protected void afterInternal(JoinPoint joinPoint, Idempotent idempotent) {
        // do nothing
    }
}
