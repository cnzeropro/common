package org.zero.common.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.Locale;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/23
 */
@Slf4j
public class MessageSourceThrowableMessageProvider extends MessageSourceMessageProvider implements ThrowableMessageProvider {
    public MessageSourceThrowableMessageProvider(MessageSourceAccessor messageSourceAccessor) {
        super(messageSourceAccessor);
    }

    public MessageSourceThrowableMessageProvider(MessageSource messageSource) {
        super(messageSource);
    }

    public MessageSourceThrowableMessageProvider(MessageSource messageSource, Locale defaultLocale) {
        super(messageSource, defaultLocale);
    }
}
