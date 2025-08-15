package org.zero.common.data.exception;

import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * 基础系统错误
 *
 * @author Zero
 * @since 2021/8/24
 */
public interface BaseSysError extends Serializable {
    String OK_CODE = "00000";
    String ERROR_CODE = "11111";

    String getCode();

    String getMessage();

    default boolean isOk() {
        return OK_CODE.equals(this.getCode());
    }

    @Getter
    @Log
    class DefaultSysError implements BaseSysError {
        public static final BaseSysError OK = new DefaultSysError(OK_CODE, "ok");
        public static final BaseSysError ERROR = new DefaultSysError(ERROR_CODE, "error");

        protected final String code;
        protected final String message;

        protected DefaultSysError(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public static DefaultSysError of(String code, String message) {
            return new DefaultSysError(code, message);
        }

        protected static String formatMessage(String message, Locale locale, Object... args) {
            if (!StringUtils.hasText(message)) {
                return null;
            }
            if (Objects.isNull(args) || args.length <= 0) {
                return message;
            }
            try {
                MessageFormat messageFormat;
                if (Objects.isNull(locale)) {
                    messageFormat = new MessageFormat(message);
                } else {
                    messageFormat = new MessageFormat(message, locale);
                }
                return messageFormat.format(args);
            } catch (Exception e) {
                log.warning(String.format("Failed to format message with the pattern[%s]", message));
                return e.getMessage();
            }
        }
    }
}
