package org.zero.common.core.aop.aspect;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/30
 */
public aspect TimeLoggerAspect {
    // 定义切点：匹配带有 @Loggable 注解的方法
    pointcut loggableMethods(): @annotation(org.zero.common.core.aop.annotation.Loggable);

    // 修正后的 around advice
    Object around(): loggableMethods() {
        long startTime = System.currentTimeMillis();
        try {
            return proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            System.out.println("Method execution time: " + (endTime - startTime) + " milliseconds.");
        }
    }
}
