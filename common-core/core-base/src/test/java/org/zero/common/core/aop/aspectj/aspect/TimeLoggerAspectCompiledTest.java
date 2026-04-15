package org.zero.common.core.aop.aspectj.aspect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/14
 */
class TimeLoggerAspectCompiledTest {
	@Test
	void aspectClassShouldBeAvailableOnTestClasspath() throws ClassNotFoundException {
		Class<?> aspectClass = Class.forName("org.zero.common.core.aop.aspectj.aspect.TimeLoggerAspect");
		assertNotNull(aspectClass);
	}
}
