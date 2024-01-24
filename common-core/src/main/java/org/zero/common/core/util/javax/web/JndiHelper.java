package org.zero.common.core.util.javax.web;

import lombok.SneakyThrows;
import org.zero.common.data.exception.UtilException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@qq.com)
 * @date 2021/8/10 9:15
 */
public class JndiHelper {
    /**
     * name like: "java:comp/env/jdbc/test"
     */
    private final String name;
    private DataSource dataSource;

    public JndiHelper(String name) {
        this.name = name;
    }

    @SneakyThrows
    public void createDataSource() {
        Context context = null;
        try {
            context = new InitialContext();
            dataSource = (DataSource) context.lookup(name);
        } finally {
            if (Objects.nonNull(context)) {
                context.close();
            }
        }
    }

    public synchronized DataSource getDataSource() {
        if (Objects.isNull(dataSource)) {
            createDataSource();
            if (Objects.isNull(dataSource)) {
                throw new UtilException("DataSource is null");
            }
        }

        return dataSource;
    }

    @SneakyThrows
    public Connection getConnection() {
        return getDataSource().getConnection();
    }
}
