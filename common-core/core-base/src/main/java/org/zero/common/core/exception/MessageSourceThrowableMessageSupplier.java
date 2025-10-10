package org.zero.common.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

/**
 * @author Zero (cnzeropro@163.com)
 * @see org.zero.common.core.exception.ThrowableMessageSource
 * @since 2025/4/23
 */
@Slf4j
public class MessageSourceThrowableMessageSupplier extends MessageSourceMessageSupplier implements ThrowableMessageSupplier {
    public MessageSourceThrowableMessageSupplier(MessageSourceAccessor messageSourceAccessor) {
        super(messageSourceAccessor);
    }

    public MessageSourceThrowableMessageSupplier(MessageSource messageSource) {
        super(messageSource);
    }

    public MessageSourceThrowableMessageSupplier(MessageSource messageSource, Locale defaultLocale) {
        super(messageSource, defaultLocale);
    }
}
