package org.zero.common.core.util.java.lang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 验证多版本 JAR 在高版本运行时会优先命中 Java 11 覆盖实现。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/13
 */
class StackUtilMultiReleaseTest {

	@Test
	void getCurrentStackTraceUsesJava11Implementation() {
		StackTraceElement[] stackTraceElements = StackUtil.getCurrentStackTrace();
		Assertions.assertTrue(stackTraceElements.length > 0);

		StackTraceElement firstStackTraceElement = stackTraceElements[0];
		Assertions.assertEquals(StackUtil.class.getName(), firstStackTraceElement.getClassName());
		Assertions.assertEquals("getCurrentStackTrace", firstStackTraceElement.getMethodName());
	}
}
