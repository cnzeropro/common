package org.zero.common.data.exception;

import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * 基础状态
 *
 * @author Zero
 * @since 2021/8/24
 */
public interface BaseStatus extends Serializable {
	String OK_CODE = "00000";
	String ERROR_CODE = "11111";

	Serializable getCode();

	CharSequence getMessage();

	default boolean isOk() {
		return Objects.equals(this.getCode(), OK_CODE);
	}

	@Getter
	@Log
	class DefaultStatus implements BaseStatus {
		public static final BaseStatus OK = new DefaultStatus(OK_CODE, "ok");
		public static final BaseStatus ERROR = new DefaultStatus(ERROR_CODE, "error");

		protected final Serializable code;
		protected final CharSequence message;

		protected DefaultStatus(Serializable code, CharSequence message) {
			this.code = code;
			this.message = message;
		}

		public static DefaultStatus of(Serializable code, CharSequence message) {
			return new DefaultStatus(code, message);
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
