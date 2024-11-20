package org.zero.common.data.enumeration;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 使用前请先使用{@link DataBaseSysError#setJdbcTemplate(JdbcTemplate)}注册{@link JdbcTemplate}，
 * 使用{@link DataBaseSysError#setQuerySql(String)}注册用于根据错误码查询错误信息的 SQL
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/18
 */
@Slf4j
public class DataBaseSysError extends BaseSysError.DefaultSysError {
    protected DataBaseSysError(String code, String message) {
        super(code, message);
    }

    @Setter
    private static JdbcTemplate jdbcTemplate;
    @Setter
    private static String querySql = "SELECT `value` FROM `sys_error_dict` WHERE `key` = ?";

    public static DataBaseSysError of(String code) {
        String message;
        try {
            message = jdbcTemplate.queryForObject(querySql, String.class, code);
        } catch (Exception e) {
            log.warn(String.format("Failed to query with the SQL: %s", querySql), e);
            message = e.getMessage();
        }
        return new DataBaseSysError(code, message);
    }
}
