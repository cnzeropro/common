package org.zero.common.core.exception;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/4
 */
public class ThrowableMessageSource extends ResourceBundleMessageSource {
    public ThrowableMessageSource() {
        setDefaultEncoding(StandardCharsets.UTF_8.name());
        setBasename("i18n/throwable/message");
    }

    public static MessageSourceAccessor getAccessor() {
        return new MessageSourceAccessor(new ThrowableMessageSource());
    }
}
