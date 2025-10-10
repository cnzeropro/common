package org.zero.common.core.support.aware.perceptor;

import org.aspectj.lang.JoinPoint;

import java.util.Map;

/**
 * 后置感知器
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/15
 */
public interface AfterPerceptor extends Perceptor {
    @Override
    default void beforeProcess(JoinPoint joinPoint, Map<String, Object> context) {
        // do nothing
    }

    @Override
    default void afterReturningProcess(JoinPoint joinPoint, Object result, Map<String, Object> context) {
        // do nothing
    }

    @Override
    default void afterThrowingProcess(JoinPoint joinPoint, Throwable throwable, Map<String, Object> context) {
        // do nothing
    }
}
