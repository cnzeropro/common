package org.zero.common.core.exception;

import org.zero.common.core.util.java.lang.CharSequenceUtil;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/23
 */
public interface MessageProvider {
    Logger log = Logger.getLogger(MessageProvider.class.getName());

    default CharSequence provide(CharSequence code, Object... args) {
        return this.provide(code, (CharSequence) null, args);
    }

    default CharSequence provide(CharSequence code, CharSequence defaultMessage, Object... args) {
        return this.provide(code, defaultMessage, null, args);
    }

    default CharSequence provide(CharSequence code, Locale locale, Object... args) {
        return this.provide(code, null, locale, args);
    }

    default CharSequence provide(CharSequence code, CharSequence defaultMessage, Locale locale, Object... args) {
        if (CharSequenceUtil.nonEmpty(defaultMessage)) {
            return formatMessage(defaultMessage, locale, args);
        }
        return code;
    }

    default CharSequence formatMessage(CharSequence message, Locale locale, Object... args) {
        if (CharSequenceUtil.isEmpty(message)) {
            return null;
        }
        if (Objects.isNull(args) || args.length <= 0) {
            return message;
        }
        try {
            MessageFormat messageFormat;
            if (Objects.isNull(locale)) {
                messageFormat = new MessageFormat(message.toString());
            } else {
                messageFormat = new MessageFormat(message.toString(), locale);
            }
            return messageFormat.format(args);
        } catch (Exception e) {
            log.warning(String.format("Failed to format message with the pattern[%s]", message));
            return e.getMessage();
        }
    }
}
