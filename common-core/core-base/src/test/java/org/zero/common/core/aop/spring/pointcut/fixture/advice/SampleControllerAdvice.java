package org.zero.common.core.aop.spring.pointcut.fixture.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/15
 */
@ControllerAdvice
public class SampleControllerAdvice {
	public void handleException(RuntimeException exception) {
	}
}
