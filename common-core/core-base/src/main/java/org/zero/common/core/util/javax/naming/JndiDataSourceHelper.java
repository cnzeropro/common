package org.zero.common.core.util.javax.naming;

import lombok.SneakyThrows;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2021/8/10 9:15
 */
public class JndiDataSourceHelper {
    private final DataSource dataSource;

    /**
     * 构造
     *
     * @param name jndi name, like: {@code java:comp/env/jdbc/test}
     */
    @SneakyThrows
    public JndiDataSourceHelper(String name) {
        Context context = new InitialContext();
        try {
            dataSource = (DataSource) context.lookup(name);
        } finally {
            context.close();
        }
    }

    public DataSource getDataSource() {
        Objects.requireNonNull(dataSource, "DataSource is null");
        return dataSource;
    }

    @SneakyThrows
    public Connection getConnection() {
        return getDataSource().getConnection();
    }
}
