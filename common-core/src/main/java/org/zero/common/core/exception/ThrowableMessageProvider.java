package org.zero.common.core.exception;

import java.util.Locale;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/23
 */
public interface ThrowableMessageProvider extends MessageProvider {
    default CharSequence provide(Class<? extends Throwable> clazz, Object... args) {
        return this.provide(clazz, (CharSequence) null, args);
    }

    default CharSequence provide(Class<? extends Throwable> clazz, CharSequence defaultMessage, Object... args) {
        return this.provide(clazz, defaultMessage, null, args);
    }

    default CharSequence provide(Class<? extends Throwable> clazz, Locale locale, Object... args) {
        return this.provide(clazz, null, locale, args);
    }

    default CharSequence provide(Class<? extends Throwable> clazz, CharSequence defaultMessage, Locale locale, Object... args) {
        return this.provide(clazz.getCanonicalName(), defaultMessage, locale, args);
    }
}
