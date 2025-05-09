package org.zero.common.data.exception;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Objects;

/**
 * 使用前请先使用 {@link #setMessageSourceAccessor} 注册 {@link MessageSourceAccessor}
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/18
 */
@Slf4j
public class MessageSourceSysError extends BaseSysError.DefaultSysError {
    @Setter
    protected static MessageSourceAccessor messageSourceAccessor;

    public static MessageSourceSysError of(String code, Object... args) {
        return of(code, (String) null, args);
    }

    public static MessageSourceSysError of(String code, String defaultMessage, Object... args) {
        return of(code, defaultMessage, null, args);
    }

    public static MessageSourceSysError of(String code, Locale locale, Object... args) {
        return of(code, null, locale, args);
    }

    public static MessageSourceSysError of(String code, String defaultMessage, Locale locale, Object... args) {
        String message = null;
        try {
            if (Objects.isNull(locale)) {
                message = messageSourceAccessor.getMessage(code, args);
            } else {
                message = messageSourceAccessor.getMessage(code, args, locale);
            }
        } catch (NoSuchMessageException e) {
            // do nothing
        } catch (Exception e) {
            log.warn(String.format("Failed to get message with the code[%s]", code), e);
            if (!StringUtils.hasText(defaultMessage)) {
                message = e.getMessage();
            }
        } finally {
            if (!StringUtils.hasText(message)) {
                message = formatMessage(defaultMessage, locale, args);
            }
        }
        return new MessageSourceSysError(code, message);
    }

    protected MessageSourceSysError(String code, String message) {
        super(code, message);
    }
}
