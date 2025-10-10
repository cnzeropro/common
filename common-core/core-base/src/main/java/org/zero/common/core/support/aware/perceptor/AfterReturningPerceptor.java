package org.zero.common.core.support.aware.perceptor;

import org.aspectj.lang.JoinPoint;

import java.util.Map;

/**
 * 后置正常感知器
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/15
 */
public interface AfterReturningPerceptor extends Perceptor {
    @Override
    default void beforeProcess(JoinPoint joinPoint, Map<String, Object> context) {
        // do nothing
    }

    @Override
    default void afterThrowingProcess(JoinPoint joinPoint, Throwable throwable, Map<String, Object> context) {
        // do nothing
    }

    @Override
    default void afterProcess(JoinPoint joinPoint, Map<String, Object> context) {
        // do nothing
    }
}
