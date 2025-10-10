package org.zero.common.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.util.Locale;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/23
 */
@Slf4j
public class JdbcTemplateMessageSupplier implements MessageSupplier {
    /**
     * 默认的查询 SQL
     * <p>
     * 示例：
     * <pre>{@code
     * SELECT message
     * FROM sys_error_dict
     * WHERE code = ?
     *   AND COALESCE(locale, 'default') = COALESCE(?, 'default')
     * }</pre>
     * <pre>{@code
     * SELECT message
     * FROM sys_error_dict
     * WHERE code = ?
     *   AND ((? IS NOT NULL AND locale = ?) OR (? IS NULL AND locale IS NULL))
     * }</pre>
     */
    protected static final String DEFAULT_QUERY_SQL = "SELECT message FROM sys_error_dict WHERE code = ? AND (locale = ? OR (locale IS NULL AND ? IS NULL))";

    protected final JdbcTemplate jdbcTemplate;
    protected final String querySql;

    public JdbcTemplateMessageSupplier(JdbcTemplate jdbcTemplate) {
        this(jdbcTemplate, DEFAULT_QUERY_SQL);
    }

    public JdbcTemplateMessageSupplier(JdbcTemplate jdbcTemplate, String querySql) {
        this.jdbcTemplate = jdbcTemplate;
        this.querySql = querySql;
    }

    @Override
    public CharSequence supply(CharSequence code, CharSequence defaultMessage, Locale locale, Object... args) {
        CharSequence message = null;
        try {
            String messageTemplate = jdbcTemplate.queryForObject(querySql, String.class, code, locale, locale);
            message = MessageSupplier.formatMessage(messageTemplate, locale, args);
        } catch (EmptyResultDataAccessException e) {
            // do nothing
        } catch (Exception e) {
            log.warn(String.format("Failed to query message with the code[%s] in SQL[%s]", code, querySql), e);
            if (!StringUtils.hasText(defaultMessage)) {
                message = e.getMessage();
            }
        } finally {
            if (!StringUtils.hasText(message)) {
                message = MessageSupplier.formatMessage(defaultMessage, locale, args);
            }
        }
        return message;
    }
}
