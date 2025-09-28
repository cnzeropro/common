package org.zero.common.core.util.java.sql;

import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/9/18
 */
public interface BaseSQLServerConnectionInformation extends BaseConnectionInformation {
	/**
	 * 数据库类型
	 * <p>
	 * 默认：{@code "SQL Server"}
	 */
	@Override
	default String getDatabaseType() {
		return "SQL Server";
	}

	/**
	 * 子协议
	 * <p>
	 * 默认：{@code "sqlserver"}
	 */
	@Override
	default String getSubprotocol() {
		return "sqlserver";
	}

	/**
	 * 端口
	 * <p>
	 * 默认：{@code 1433}
	 */
	@Override
	default Integer getPort() {
		return 1433;
	}

	/**
	 * 数据库名称
	 */
	String getDatabaseName();

	/**
	 * 数据库ID
	 * <p>
	 *
	 * @see #getDatabaseName()
	 */
	@Override
	default String getDatabaseId() {
		return getDatabaseName();
	}

	/**
	 * 模式
	 * <p>
	 * 默认：{@code "dbo"}
	 */
	@Override
	default String getSchema() {
		return "dbo";
	}

	/**
	 * 连接的基础 URL
	 *
	 * @see #getProtocol()
	 * @see #getHostname()
	 * @see #getPort()
	 */
	@Override
	default String getBaseUrl() {
		return getProtocol() + "://" + getHostname() + ":" + getPort();
	}

	/**
	 * 连接 URL
	 */
	@Override
	default String getUrl() {
		return getBaseUrl() + ";" + getQueryString();
	}

	@Override
	default Properties getMergedProperties() {
		Properties mergedProps = BaseConnectionInformation.super.getMergedProperties();
		String databaseName = this.getDatabaseName();
		if (Objects.nonNull(databaseName)) {
			mergedProps.setProperty("databaseName", databaseName);
		}
		return mergedProps;
	}

	@Override
	default String getQueryString() {
		Properties mergedProps = new Properties();
		Properties connectionProps = this.getProperties();
		if (Objects.nonNull(connectionProps)) {
			mergedProps.putAll(connectionProps);
		}
		String databaseName = this.getDatabaseName();
		if (Objects.nonNull(databaseName)) {
			mergedProps.setProperty("databaseName", databaseName);
		}
		return mergedProps.entrySet()
			.stream()
			.map(entry -> entry.getKey() + "=" + entry.getValue())
			.collect(Collectors.joining(";"));
	}

	/**
	 * 用户名
	 * <p>
	 * 默认：{@code "sa"}
	 */
	@Override
	default String getUsername() {
		return "sa";
	}

	/**
	 * 驱动类名称
	 * <p>
	 * 默认：{@code "com.microsoft.sqlserver.jdbc.SQLServerDriver"}
	 */
	@Override
	default String getDriverClassName() {
		return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	}
}
