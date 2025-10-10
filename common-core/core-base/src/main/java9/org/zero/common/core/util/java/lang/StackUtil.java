package org.zero.common.core.util.java.lang;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/10
 */
public class StackUtil {
	public static StackTraceElement[] getCurrentStackTrace(){
		return StackWalker.getInstance()
			.walk(s -> s.map(StackWalker.StackFrame::toStackTraceElement)
				.toArray(StackTraceElement[]::new));
	}

	public static StackTraceElement getInvokeStackTrace(){
		return getCurrentStackTrace()[3];
	}
}
