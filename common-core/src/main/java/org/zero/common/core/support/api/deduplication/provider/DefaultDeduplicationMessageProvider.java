package org.zero.common.core.support.api.deduplication.provider;

import org.zero.common.core.support.api.deduplication.annotation.Deduplication;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/2
 */
public class DefaultDeduplicationMessageProvider implements DeduplicationMessageProvider {
    public static final String MESSAGE = "请勿重复提交";
    public static final DefaultDeduplicationMessageProvider INSTANCE = new DefaultDeduplicationMessageProvider();

    @Override
    public String generate(Object context, Deduplication deduplication) {
        return MESSAGE;
    }
}
