package org.zero.common.core.aop.spring.fixture.advice;

import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/15
 */
@RestControllerAdvice
public class SampleRestControllerAdvice {
	public void handleException(RuntimeException exception) {
	}
}
