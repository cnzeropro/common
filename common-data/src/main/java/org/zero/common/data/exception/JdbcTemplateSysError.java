package org.zero.common.data.exception;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 使用前请先使用{@link JdbcTemplateSysError#setJdbcTemplate(JdbcTemplate)}注册{@link JdbcTemplate}，
 * 使用{@link JdbcTemplateSysError#setQuerySql(String)}注册用于根据错误码查询错误信息的 SQL
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/18
 */
@Slf4j
public class JdbcTemplateSysError extends BaseSysError.DefaultSysError {
    protected JdbcTemplateSysError(String code, String message) {
        super(code, message);
    }

    @Setter
    protected static JdbcTemplate jdbcTemplate;
    @Setter
    protected static String querySql = "SELECT message FROM sys_error_dict WHERE code = ?";

    public static JdbcTemplateSysError of(String code) {
        String message;
        try {
            message = jdbcTemplate.queryForObject(querySql, String.class, code);
        } catch (Exception e) {
            log.warn(String.format("Failed to query message with the SQL[%s] and code[%s]", querySql, code), e);
            message = e.getMessage();
        }
        return new JdbcTemplateSysError(code, message);
    }
}
