package org.zero.common.core.util.java.lang;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/1/10
 */
public class StackUtil {
    /**
     * 获取当前调用栈
     */
    public static StackTraceElement[] getCurrentStackTrace(){
        return Thread.currentThread().getStackTrace();
    }

    /**
     * 获取执行该方法的上层调用栈
     */
    public static StackTraceElement getInvokeStackTrace(){
        return getCurrentStackTrace()[3];
    }

    protected StackUtil(){
        throw new UnsupportedOperationException();
    }
}
