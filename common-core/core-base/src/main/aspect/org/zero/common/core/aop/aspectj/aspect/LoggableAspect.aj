package org.zero.common.core.aop.aspectj.aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 记录带有 {@linkplain org.zero.common.core.aop.aspectj.annotation.Loggable @Loggable}
 * 注解方法耗时的原生 AspectJ 切面。
 * <p>
 * 当前模块发布的是 AspectJ aspect library，下游项目需要自行选择 LTW 或 CTW 接入。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/30
 */
public aspect LoggableAspect {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggableAspect.class);

	/**
	 * 匹配显式标注 {@code @Loggable} 的方法。
	 */
	pointcut loggableMethods(): @annotation(org.zero.common.core.aop.aspectj.annotation.Loggable);

	/**
	 * 环绕记录目标方法的执行耗时，并标识执行结果。
	 *
	 * @return 目标方法返回值
	 */
	Object around(): loggableMethods() {
		long startTime = System.nanoTime();
		boolean success = false;
		try {
			Object result = proceed();
			success = true;
			return result;
		} finally {
			long elapsedNanos = System.nanoTime() - startTime;
			String signature = thisJoinPointStaticPart.getSignature().toLongString();
			LOGGER.info(
					"Method [{}] {}. elapsedMs={}, elapsedNanos={}",
					signature,
					success ? "completed successfully" : "failed",
					elapsedNanos / 1000000L,
					elapsedNanos
			);
		}
	}
}
