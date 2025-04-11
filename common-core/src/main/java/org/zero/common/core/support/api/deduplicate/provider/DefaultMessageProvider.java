package org.zero.common.core.support.api.deduplicate.provider;

import org.zero.common.core.support.api.deduplicate.annotation.Deduplicate;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/2
 */
public class DefaultMessageProvider implements MessageProvider {
    public static final String MESSAGE = "请勿重复提交";
    public static final DefaultMessageProvider INSTANCE = new DefaultMessageProvider();

    @Override
    public String generate(Object context, Deduplicate deduplicate) {
        return MESSAGE;
    }
}
