package org.zero.common.core.exception.status;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.zero.common.data.enumeration.Status;

import java.util.Locale;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/18
 */
@Slf4j
public class MessageSourceLazyStatus extends Status.Default {
	private String defaultMessage;
	private Locale locale = LocaleContextHolder.getLocale();
	private Object[] args = {};

	public MessageSourceLazyStatus(String code) {
		this(code, (String) null);
	}

	public MessageSourceLazyStatus(String code, String defaultMessage) {
		super(code, defaultMessage);
		this.defaultMessage = defaultMessage;
	}

	public MessageSourceLazyStatus(String code, Object... args) {
		super(code, null);
		this.args = args;
	}

	public MessageSourceLazyStatus(String code, String defaultMessage, Object... args) {
		super(code, defaultMessage);
		this.defaultMessage = defaultMessage;
		this.args = args;
	}

	public MessageSourceLazyStatus(String code, String defaultMessage, Locale locale, Object... args) {
		super(code, defaultMessage);
		this.defaultMessage = defaultMessage;
		this.locale = locale;
		this.args = args;
	}

	@Override
	public CharSequence getMessage() {
		MessageSourceStatus messageSourceStatus = MessageSourceStatus.of(code.toString(), defaultMessage, locale, args);
		return messageSourceStatus.getMessage();
	}
}
