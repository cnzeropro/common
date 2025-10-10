package org.zero.common.core.exception;

import java.util.Locale;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/23
 */
public interface ThrowableMessageSupplier extends MessageSupplier {
    default CharSequence supply(Class<? extends Throwable> clazz, Object... args) {
        return this.supply(clazz, (CharSequence) null, args);
    }

    default CharSequence supply(Class<? extends Throwable> clazz, CharSequence defaultMessage, Object... args) {
        return this.supply(clazz, defaultMessage, null, args);
    }

    default CharSequence supply(Class<? extends Throwable> clazz, Locale locale, Object... args) {
        return this.supply(clazz, null, locale, args);
    }

    default CharSequence supply(Class<? extends Throwable> clazz, CharSequence defaultMessage, Locale locale, Object... args) {
        return this.supply(clazz.getCanonicalName(), defaultMessage, locale, args);
    }
}
