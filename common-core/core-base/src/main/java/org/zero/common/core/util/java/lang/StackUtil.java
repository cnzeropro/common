package org.zero.common.core.util.java.lang;

import org.zero.common.core.util.java.security.SecurityUtil;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/10
 */
public class StackUtil {
    /**
     * 获取当前调用栈
     */
    public static StackTraceElement[] getCurrentStackTrace() {
        Thread currentThread = Thread.currentThread();
        return SecurityUtil.doPrivileged(currentThread::getStackTrace);
    }

    /**
     * 获取执行该方法的上层调用栈
     * <pre>
     * Stack 0 -> {@link java.lang.Thread#getStackTrace()}
     * Stack 1 -> {@link #getCurrentStackTrace()}
     * Stack 2 -> {@link #getInvokeStackTrace()}
     * // xxx 表示具体的调用方法
     * Stack 3 -> xxx
     * </pre>
     */
    public static StackTraceElement getInvokeStackTrace() {
        return getCurrentStackTrace()[3];
    }

    protected StackUtil() {
        throw new UnsupportedOperationException();
    }
}
