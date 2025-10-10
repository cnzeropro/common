package org.zero.common.core.support.aware.perceptor;

import org.aspectj.lang.JoinPoint;

import java.util.Map;

/**
 * 前置感知器
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/15
 */
public interface BeforePerceptor extends Perceptor {
    @Override
    default void afterReturningProcess(JoinPoint joinPoint, Object result, Map<String, Object> context) {
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
