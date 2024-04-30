package org.zero.common.core.util.spring.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;

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
    private static String message(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return message(code, locale, args);
    }

    private static String message(String code, Locale locale, Object... args) {
        return getMessageSource().getMessage(code, args, locale);
    }

    private static String message(String code, String localeStr, Object... args) {
        Locale locale = StringUtils.parseLocale(localeStr);
        return message(code, Objects.isNull(locale) ? LocaleContextHolder.getLocale() : locale, args);
    }

    private static MessageSource messageSource;

    private static MessageSource getMessageSource() {
        if (Objects.isNull(messageSource)) {
            synchronized (MessageSourceUtils.class) {
                if (Objects.isNull(messageSource)) {
                    messageSource = SpringContextUtils.getBean(MessageSource.class);
                }
            }
        }
        return messageSource;
    }
}
