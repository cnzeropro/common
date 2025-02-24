package org.zero.common.data.exception;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ObjectUtils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * 使用前请先使用 {@link #setJdbcTemplate(JdbcTemplate)} 注册 {@link JdbcTemplate}，
 * 使用 {@link #setQuerySql(String)} 注册用于根据错误码查询错误信息的 SQL
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/18
 */
@Slf4j
public class JdbcTemplateSysError extends BaseSysError.DefaultSysError {
    @Setter
    protected static JdbcTemplate jdbcTemplate;
    @Setter
    protected static String querySql = "SELECT message FROM sys_error_dict WHERE code = ?";
    @Setter
    protected static Locale locale = LocaleContextHolder.getLocale();

    public static JdbcTemplateSysError of(String code, Object... args) {
        return of(code, locale, args);
    }

    public static JdbcTemplateSysError of(String code, Locale locale, Object... args) {
        String message;
        try {
            message = jdbcTemplate.queryForObject(querySql, String.class, code);
            if (Objects.nonNull(message) && !ObjectUtils.isEmpty(args)) {
                // message = MessageFormat.format(message, args);
                MessageFormat messageFormat = new MessageFormat(message, locale);
                message = messageFormat.format(args);
            }
        } catch (Exception e) {
            log.warn(String.format("Failed to query message with the code[%s] in SQL[%s]", code, querySql), e);
            message = e.getMessage();
        }
        return new JdbcTemplateSysError(code, message);
    }

    protected JdbcTemplateSysError(String code, String message) {
        super(code, message);
    }
}
