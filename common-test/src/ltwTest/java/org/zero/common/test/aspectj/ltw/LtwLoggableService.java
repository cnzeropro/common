package org.zero.common.test.aspectj.ltw;

import org.zero.common.core.aop.aspectj.annotation.Loggable;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/14
 */
public class LtwLoggableService {
    @Loggable
    public String ok(String input) {
        return "ok:" + input;
    }

    @Loggable
    public void fail() {
        throw new IllegalStateException("boom");
    }
}
