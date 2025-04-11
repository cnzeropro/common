package org.zero.common.core.support.api.throttle.provider;

import org.zero.common.core.support.api.throttle.annotation.Throttle;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/2
 */
public class DefaultMessageProvider implements MessageProvider {
    public static final String MESSAGE = "限流中，请稍后重试";
    public static final DefaultMessageProvider INSTANCE = new DefaultMessageProvider();

    @Override
    public String generate(Object context, Throttle throttle) {
        return MESSAGE;
    }
}
