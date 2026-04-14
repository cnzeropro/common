package org.zero.common.core.util.java.lang;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 验证多版本 JAR 在高版本运行时会优先命中 java25 覆盖实现。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/14
 */
class StackUtilMultiReleaseTest {

	@Test
	void getCurrentStackTraceUsesJava25Implementation() {
		StackTraceElement[] stackTraceElements = StackUtil.getCurrentStackTrace();
		Assertions.assertTrue(stackTraceElements.length > 0);

		StackTraceElement firstStackTraceElement = stackTraceElements[0];
		Assertions.assertEquals(StackUtil.class.getName(), firstStackTraceElement.getClassName());
		Assertions.assertEquals("getCurrentStackTrace", firstStackTraceElement.getMethodName());
	}
}
