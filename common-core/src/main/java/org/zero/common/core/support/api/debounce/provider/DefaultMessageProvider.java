package org.zero.common.core.support.api.debounce.provider;

import org.zero.common.core.support.api.debounce.annotation.Debounce;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/2
 */
public class DefaultMessageProvider implements MessageProvider {
    public static final String MESSAGE = "请勿在短时间内重复触发";
    public static final DefaultMessageProvider INSTANCE = new DefaultMessageProvider();

    @Override
    public String generate(Object context, Debounce debounce) {
        return MESSAGE;
    }
}
