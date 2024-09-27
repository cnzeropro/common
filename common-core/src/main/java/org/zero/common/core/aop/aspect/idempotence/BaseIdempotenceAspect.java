package org.zero.common.core.aop.aspect.idempotence;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.zero.common.data.exception.CommonException;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/9/25
 */
public abstract class BaseIdempotenceAspect {
    @Before("@within(idempotence) || " +
            "@annotation(idempotence)")
    public void before(JoinPoint joinPoint, Idempotence idempotence) {
        if (idempotence.value()) {
            if (this.needPrevent(joinPoint, idempotence)) {
                throw new CommonException(idempotence.message());
            }
        }
    }

    @After("@within(idempotence) || " +
            "@annotation(idempotence)")
    public void after(JoinPoint joinPoint, Idempotence idempotence) {
        this.afterInternal(joinPoint, idempotence);
    }

    /**
     * 验证是否需要阻止
     */
    protected abstract boolean needPrevent(JoinPoint joinPoint, Idempotence idempotence);

    /**
     * 后置处理
     */
    protected void afterInternal(JoinPoint joinPoint, Idempotence idempotence) {
        // do nothing
    }
}
