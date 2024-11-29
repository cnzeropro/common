package org.zero.common.data.exception;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * 使用前请先使用{@link MessageSourceSysError#setMessageSource(MessageSource)}注册{@link MessageSource}
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/18
 */
@Slf4j
public class MessageSourceSysError extends BaseSysError.DefaultSysError {
    protected MessageSourceSysError(String code, String message) {
        super(code, message);
    }

    @Setter
    protected static MessageSource messageSource;
    @Setter
    protected static Locale locale = LocaleContextHolder.getLocale();

    public static MessageSourceSysError of(String code, Object... args) {
        return of(code, locale, args);
    }

    public static MessageSourceSysError of(String code, Locale locale, Object... args) {
        String message;
        try {
            message = messageSource.getMessage(code, args, locale);
        } catch (Exception e) {
            log.warn(String.format("Failed to get message with the code[%s] in MessageSource", code), e);
            message = e.getMessage();
        }
        return new MessageSourceSysError(code, message);
    }
}
