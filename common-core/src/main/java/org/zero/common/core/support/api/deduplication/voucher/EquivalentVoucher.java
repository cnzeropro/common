package org.zero.common.core.support.api.deduplication.voucher;

import org.zero.common.core.support.api.deduplication.annotation.Deduplication;

/**
 * 等效凭证
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/2
 */
@FunctionalInterface
public interface EquivalentVoucher {
    /**
     * 生成凭证
     *
     * @param context       上下文。在拦截器中为 {@linkplain org.springframework.web.method.HandlerMethod HandlerMethod}，在切面中为 {@linkplain org.aspectj.lang.JoinPoint JoinPoint}
     * @param deduplication 防重注解
     * @return 凭证，判别何为重复
     */
    String generate(Object context, Deduplication deduplication);
}
