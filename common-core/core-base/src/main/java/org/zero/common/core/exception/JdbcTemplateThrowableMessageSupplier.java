package org.zero.common.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/23
 */
@Slf4j
public class JdbcTemplateThrowableMessageSupplier extends JdbcTemplateMessageSupplier implements ThrowableMessageSupplier {
    public JdbcTemplateThrowableMessageSupplier(JdbcTemplate jdbcTemplate) {
        this(jdbcTemplate, DEFAULT_QUERY_SQL);
    }

    public JdbcTemplateThrowableMessageSupplier(JdbcTemplate jdbcTemplate, String querySql) {
        super(jdbcTemplate, querySql);
    }
}
