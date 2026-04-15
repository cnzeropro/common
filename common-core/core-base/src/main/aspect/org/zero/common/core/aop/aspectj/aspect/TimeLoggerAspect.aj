package org.zero.common.core.aop.aspectj.aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * 记录带有 {@linkplain org.zero.common.core.aop.aspectj.annotation.Loggable @Loggable}
 * 注解方法耗时的原生 AspectJ 切面。
 * <p>
 * 当前模块发布的是 AspectJ aspect library，下游项目需要自行选择 LTW 或 CTW 接入。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/30
 */
public aspect TimeLoggerAspect {
	private static final Logger LOGGER = LoggerFactory.getLogger(TimeLoggerAspect.class);

	pointcut loggableMethods(): @annotation(org.zero.common.core.aop.aspectj.annotation.Loggable);

	Object around(): loggableMethods() {
		long startTime = System.nanoTime();
		try {
			return proceed();
		} finally {
			Duration duration = Duration.ofNanos(System.nanoTime() - startTime);
			String signature = thisJoinPointStaticPart.getSignature().toLongString();
			LOGGER.info("Method[{}] execution time: {}", signature, duration);
		}
	}
}
