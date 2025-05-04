package org.zero.common.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @see org.zero.common.core.exception.ThrowableMessageSource
 * @since 2025/4/23
 */
@Slf4j
public class MessageSourceMessageProvider implements ThrowableMessageProvider {
    protected final MessageSourceAccessor messageSourceAccessor;

    public MessageSourceMessageProvider(MessageSourceAccessor messageSourceAccessor) {
        this.messageSourceAccessor = messageSourceAccessor;
    }

    public MessageSourceMessageProvider(MessageSource messageSource) {
        this(new MessageSourceAccessor(messageSource));
    }

    public MessageSourceMessageProvider(MessageSource messageSource, Locale defaultLocale) {
        this(new MessageSourceAccessor(messageSource, defaultLocale));
    }

    @Override
    public CharSequence provide(CharSequence code, CharSequence defaultMessage, Locale locale, Object... args) {
        CharSequence message = null;
        try {
            if (Objects.isNull(locale)) {
                message = messageSourceAccessor.getMessage(code.toString(), args);
            } else {
                message = messageSourceAccessor.getMessage(code.toString(), args, locale);
            }
        } catch (Exception e) {
            log.warn(String.format("Failed to get message with the code[%s]", code), e);
            if (!StringUtils.hasText(defaultMessage)) {
                message = e.getMessage();
            }
        } finally {
            if (!StringUtils.hasText(message)) {
                message = this.formatMessage(defaultMessage, locale, args);
            }
        }
        return message;
    }
}
