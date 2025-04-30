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
public class JdbcTemplateMessageProvider implements MessageProvider {
    protected static final String DEFAULT_QUERY_SQL = "SELECT message FROM sys_error_dict WHERE code = ?";

    protected final JdbcTemplate jdbcTemplate;
    protected final String querySql;

    public JdbcTemplateMessageProvider(JdbcTemplate jdbcTemplate) {
        this(jdbcTemplate, DEFAULT_QUERY_SQL);
    }

    public JdbcTemplateMessageProvider(JdbcTemplate jdbcTemplate, String querySql) {
        this.jdbcTemplate = jdbcTemplate;
        this.querySql = querySql;
    }

    @Override
    public CharSequence provide(CharSequence code, CharSequence defaultMessage, Locale locale, Object... args) {
        CharSequence message = null;
        try {
            String messageTemplate = jdbcTemplate.queryForObject(querySql, String.class, code);
            message = formatMessage(messageTemplate, locale, args);
        } catch (EmptyResultDataAccessException ignored) {
            // do nothing
        } catch (Exception e) {
            log.warn(String.format("Failed to query message with the code[%s] in SQL[%s]", code, querySql), e);
            if (!StringUtils.hasText(defaultMessage)) {
                message = e.getMessage();
            }
        } finally {
            if (!StringUtils.hasText(defaultMessage)) {
                message = formatMessage(defaultMessage, locale, args);
            }
        }
        return message;
    }
}
