package org.zero.common.core.util.java.lang;

import org.junit.jupiter.api.Test;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/13
 */
class StackUtilTest {

    @Test
    void getCurrentStackTrace() {
        StackTraceElement[] stackTraces = StackUtil.getCurrentStackTrace();
        for (StackTraceElement stackTrace : stackTraces) {
            System.out.println(stackTrace);
        }
    }

    @Test
    void getInvokeStackTrace() {
        // StackTraceElement stackTraceElement = StackUtil.getInvokeStackTrace();
        StackTraceElement stackTraceElement = wrap();
        System.out.println(stackTraceElement.toString());
        System.out.println(stackTraceElement.getMethodName());
    }

    StackTraceElement wrap(){
        System.out.println("start");
        return StackUtil.getInvokeStackTrace();
    }
}