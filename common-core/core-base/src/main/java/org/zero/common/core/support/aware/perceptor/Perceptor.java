package org.zero.common.core.support.aware.perceptor;

import org.aspectj.lang.JoinPoint;

import java.util.Map;

/**
 * 感知器
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/15
 */
public interface Perceptor {
    /**
     * 前置感知处理
     *
     * @param joinPoint 切点
     * @param context   上下文信息
     */
    void beforeProcess(JoinPoint joinPoint, Map<String, Object> context);

    /**
     * 后置正常感知处理
     *
     * @param joinPoint 切点
     * @param result    返回值
     * @param context   上下文信息
     */
    void afterReturningProcess(JoinPoint joinPoint, Object result, Map<String, Object> context);

    /**
     * 后置异常感知处理
     *
     * @param joinPoint 切点
     * @param throwable 异常
     * @param context   上下文信息
     */
    void afterThrowingProcess(JoinPoint joinPoint, Throwable throwable, Map<String, Object> context);

    /**
     * 后置感知处理
     *
     * @param joinPoint 切点
     * @param context   上下文信息
     */
    void afterProcess(JoinPoint joinPoint, Map<String, Object> context);
}
