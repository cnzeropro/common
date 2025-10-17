package org.zero.common.core.exception.status;

import org.zero.common.data.exception.Status;

import java.util.Locale;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/18
 */
public class JdbcTemplateLazyStatus extends Status.Default {
	private String defaultMessage;
	private Locale locale = JdbcTemplateStatus.locale;
	private Object[] args = {};

	public JdbcTemplateLazyStatus(String code) {
		this(code, (String) null);
	}

	public JdbcTemplateLazyStatus(String code, String defaultMessage) {
		super(code, defaultMessage);
		this.defaultMessage = defaultMessage;
	}

	public JdbcTemplateLazyStatus(String code, Object... args) {
		super(code, null);
		this.args = args;
	}

	public JdbcTemplateLazyStatus(String code, String defaultMessage, Object... args) {
		super(code, defaultMessage);
		this.defaultMessage = defaultMessage;
		this.args = args;
	}

	public JdbcTemplateLazyStatus(String code, String defaultMessage, Locale locale, Object... args) {
		super(code, defaultMessage);
		this.defaultMessage = defaultMessage;
		this.locale = locale;
		this.args = args;
	}

	@Override
	public CharSequence getMessage() {
		JdbcTemplateStatus jdbcTemplateStatus = JdbcTemplateStatus.of(code.toString(), defaultMessage, locale, args);
		return jdbcTemplateStatus.getMessage();
	}
}
