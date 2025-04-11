package org.zero.common.core.support.xss.processor;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/10
 */
public interface XssProcessor {
    String process(String value, Type type);
}
