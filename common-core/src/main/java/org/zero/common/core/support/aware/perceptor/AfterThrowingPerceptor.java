package org.zero.common.core.support.aware.perceptor;

import org.aspectj.lang.JoinPoint;

import java.util.Map;

/**
 * 后置异常感知器
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/15
 */
public interface AfterThrowingPerceptor extends Perceptor {
    @Override
    default void beforeProcess(JoinPoint joinPoint, Map<String, Object> context) {
        // do nothing
    }

    @Override
    default void afterReturningProcess(JoinPoint joinPoint, Object result, Map<String, Object> context){
        // do nothing
    }

    @Override
    default void afterProcess(JoinPoint joinPoint, Map<String, Object> context) {
        // do nothing
    }
}
