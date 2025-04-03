package org.zero.common.core.support.api.debouncing.provider;

import org.zero.common.core.support.api.debouncing.annotation.Debouncing;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/2
 */
public class DefaultDebouncingMessageProvider implements DebouncingMessageProvider {
    public static final String MESSAGE = "请勿在短时间内重复触发";
    public static final DefaultDebouncingMessageProvider INSTANCE = new DefaultDebouncingMessageProvider();

    @Override
    public String generate(Object context, Debouncing debouncing) {
        return MESSAGE;
    }
}
