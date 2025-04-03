package org.zero.common.core.aop.aspect;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/30
 */
public aspect TimeLoggerAspect {
    pointcut loggableMethods(): @annotation(org.zero.common.core.aop.annotation.Loggable);

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
