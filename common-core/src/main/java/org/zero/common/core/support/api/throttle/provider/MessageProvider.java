package org.zero.common.core.support.api.throttle.provider;

import org.zero.common.core.support.api.throttle.annotation.Throttle;

/**
 * 提示信息提供者
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/2
 */
@FunctionalInterface
public interface MessageProvider {
    /**
     * 生成提示信息
     *
     * @param context  上下文。在拦截器中为 {@linkplain org.springframework.web.method.HandlerMethod HandlerMethod}，在切面中为 {@linkplain org.aspectj.lang.JoinPoint JoinPoint}
     * @param throttle 限流注解
     * @return 提示信息
     */
    String generate(Object context, Throttle throttle);
}
