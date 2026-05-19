package org.zero.common.core.util.java.lang;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 验证多版本 JAR 在高版本运行时会优先命中 Java 11 覆盖实现。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/04/13
 */
class StackUtilTest {
	@Test
	void getCurrentStackTraceShouldUseJava11Implementation() {
		StackTraceElement[] stackTraceElements = StackUtil.getCurrentStackTrace();

		assertTrue(stackTraceElements.length > 0);

		StackTraceElement firstStackTraceElement = stackTraceElements[0];
		assertEquals(StackUtil.class.getName(), firstStackTraceElement.getClassName());
		assertEquals("getCurrentStackTrace", firstStackTraceElement.getMethodName());
	}
}
