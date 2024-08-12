package org.zero.common.core.extend.mybatis;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/8/12
 */
public abstract class BaseJsonTypeHandler<T> extends BaseTypeHandler<T> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        String jsonStr = this.toJson(parameter);
        ps.setString(i, jsonStr);
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        final String jsonStr = rs.getString(columnName);
        return Objects.isNull(jsonStr) ? null : this.parseJson(jsonStr);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        final String jsonStr = rs.getString(columnIndex);
        return Objects.isNull(jsonStr) ? null : this.parseJson(jsonStr);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        final String jsonStr = cs.getString(columnIndex);
        return Objects.isNull(jsonStr) ? null : this.parseJson(jsonStr);
    }

    protected abstract T parseJson(String json);

    protected abstract String toJson(T object);
}
