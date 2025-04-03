package org.zero.common.core.support.api.deduplication.provider;

import org.zero.common.core.support.api.deduplication.annotation.Deduplication;

/**
 * 提示信息提供者
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/2
 */
@FunctionalInterface
public interface DeduplicationMessageProvider {
    /**
     * 生成提示信息
     *
     * @param context       上下文。在拦截器中为 {@linkplain org.springframework.web.method.HandlerMethod HandlerMethod}，在切面中为 {@linkplain org.aspectj.lang.JoinPoint JoinPoint}
     * @param deduplication 防重注解
     * @return 提示信息
     */
    String generate(Object context, Deduplication deduplication);
}
