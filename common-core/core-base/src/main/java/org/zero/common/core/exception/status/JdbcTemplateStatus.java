package org.zero.common.core.exception.status;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.zero.common.data.exception.Status;

import java.util.Locale;

/**
 * 使用前请先使用 {@link #setJdbcTemplate(JdbcTemplate)} 注册 {@link JdbcTemplate}，
 * 使用 {@link #setQuerySql(String)} 注册用于根据错误码查询错误信息的 SQL
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/18
 */
@Slf4j
public class JdbcTemplateStatus extends Status.Default {
	@Setter
	protected static JdbcTemplate jdbcTemplate;
	@Setter
	protected static String querySql = "SELECT message FROM sys_error_dict WHERE code = ? AND (locale = ? OR (locale IS NULL AND ? IS NULL))";
	@Setter
	protected static Locale locale = LocaleContextHolder.getLocale();

	public static JdbcTemplateStatus of(String code, Object... args) {
		return of(code, locale, args);
	}

	public static JdbcTemplateStatus of(String code, String defaultMessage, Object... args) {
		return of(code, defaultMessage, locale, args);
	}

	public static JdbcTemplateStatus of(String code, Locale locale, Object... args) {
		return of(code, null, locale, args);
	}

	public static JdbcTemplateStatus of(String code, String defaultMessage, Locale locale, Object... args) {
		String message = null;
		try {
			String messageTemplate = jdbcTemplate.queryForObject(querySql, String.class, code, locale, locale);
			message = formatMessage(messageTemplate, locale, args);
		} catch (EmptyResultDataAccessException e) {
			// do nothing
		} catch (Exception e) {
			log.warn(String.format("Failed to query message with the code[%s] in SQL[%s]", code, querySql), e);
			if (!StringUtils.hasText(defaultMessage)) {
				message = e.getMessage();
			}
		} finally {
			if (!StringUtils.hasText(message)) {
				message = formatMessage(defaultMessage, locale, args);
			}
		}
		return new JdbcTemplateStatus(code, message);
	}

	protected JdbcTemplateStatus(String code, String message) {
		super(code, message);
	}
}
