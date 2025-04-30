package org.zero.common.core.support.log.handler.processor;

import org.aspectj.lang.JoinPoint;

import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/15
 */
public class DefaultLogProcessor implements LogProcessor {
    public static final DefaultLogProcessor INSTANCE = new DefaultLogProcessor();

    @Override
    public void processBefore(JoinPoint joinPoint, Map<String, Object> context) {
    }

    @Override
    public void processAfterReturning(JoinPoint joinPoint, Object result, Map<String, Object> context) {
    }

    @Override
    public void processAfterThrowing(JoinPoint joinPoint, Throwable throwable, Map<String, Object> context) {
    }

    @Override
    public void processAfter(JoinPoint joinPoint, Map<String, Object> context) {
    }
}
