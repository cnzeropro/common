package org.zero.common.core.aop.aspectj.aspect;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/30
 */
public aspect TimeLoggerAspect {
    pointcut loggableMethods(): @annotation(org.zero.common.core.aop.aspectj.annotation.Loggable);

    Object around(): loggableMethods() {
        LocalDateTime startTime = LocalDateTime.now();
        try {
            return proceed();
        } finally {
            LocalDateTime endTime = LocalDateTime.now();
            String signatureName = thisJoinPoint.getSignature().getName();
            System.out.printf("Method[%s] execution time: %s%n", signatureName, Duration.between(startTime, endTime));
        }
    }
}
