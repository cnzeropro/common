package org.zero.common.core.util.spring.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import org.zero.common.core.util.spring.SpringUtils;

import java.util.Locale;
import java.util.Objects;

/**
 * 相关参见：
 * MessageSource: {@link org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration}
 * LocaleResolver: {@link org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.EnableWebMvcConfiguration#localeResolver()}
 *
 * @author zero
 * @since 2021/2/20
 */
@Slf4j
public class MessageSourceUtils {
    public static String message(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return message(code, locale, args);
    }

    public static String message(String code, Object[] args, String defaultMessage) {
        Locale locale = LocaleContextHolder.getLocale();
        return message(code, locale, defaultMessage, args);
    }

    public static String message(String code, Locale locale, Object... args) {
        return getMessageSource().getMessage(code, args, locale);
    }

    public static String message(String code, Locale locale, Object[] args, String defaultMessage) {
        return getMessageSource().getMessage(code, args, defaultMessage, locale);
    }

    public static String message(String code, String localeValue, Object... args) {
        Locale locale = StringUtils.parseLocale(localeValue);
        return message(code, Objects.isNull(locale) ? LocaleContextHolder.getLocale() : locale, args);
    }

    public static String message(String code, String localeValue, Object[] args, String defaultMessage) {
        Locale locale = StringUtils.parseLocale(localeValue);
        return message(code, Objects.isNull(locale) ? LocaleContextHolder.getLocale() : locale, defaultMessage, args);
    }

    private static MessageSource messageSource;

    private static MessageSource getMessageSource() {
        if (Objects.isNull(messageSource)) {
            synchronized (MessageSourceUtils.class) {
                if (Objects.isNull(messageSource)) {
                    messageSource = SpringUtils.getBean(MessageSource.class);
                }
            }
        }
        return messageSource;
    }
}
