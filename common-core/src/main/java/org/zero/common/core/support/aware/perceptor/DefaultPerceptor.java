package org.zero.common.core.support.aware.perceptor;

import org.aspectj.lang.JoinPoint;

import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/15
 */
public class DefaultPerceptor implements Perceptor {
    public static final DefaultPerceptor INSTANCE = new DefaultPerceptor();

    @Override
    public void beforeProcess(JoinPoint joinPoint, Map<String, Object> context) {
        // do nothing
    }

    @Override
    public void afterReturningProcess(JoinPoint joinPoint, Object result, Map<String, Object> context) {
        // do nothing
    }

    @Override
    public void afterThrowingProcess(JoinPoint joinPoint, Throwable throwable, Map<String, Object> context) {
        // do nothing
    }

    @Override
    public void afterProcess(JoinPoint joinPoint, Map<String, Object> context) {
        // do nothing
    }
}
