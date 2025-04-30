package org.zero.common.core.support.log.handler.processor;

import org.aspectj.lang.JoinPoint;

import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/15
 */
public interface LogProcessor {
    /**
     * 前置处理
     *
     * @param joinPoint 切点
     * @param context   上下文信息
     */
    void processBefore(JoinPoint joinPoint, Map<String, Object> context);

    /**
     * 正常后置处理
     *
     * @param joinPoint 切点
     * @param result    返回值
     * @param context   上下文信息
     */
    void processAfterReturning(JoinPoint joinPoint, Object result, Map<String, Object> context);

    /**
     * 异常后置处理
     *
     * @param joinPoint 切点
     * @param throwable 异常
     * @param context   上下文信息
     */
    void processAfterThrowing(JoinPoint joinPoint, Throwable throwable, Map<String, Object> context);

    /**
     * 后置处理
     *
     * @param joinPoint 切点
     * @param context   上下文信息
     */
    void processAfter(JoinPoint joinPoint, Map<String, Object> context);
}
