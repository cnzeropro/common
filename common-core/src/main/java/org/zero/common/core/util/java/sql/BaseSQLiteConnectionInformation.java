package org.zero.common.core.util.java.sql;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/9/18
 */
public interface BaseSQLiteConnectionInformation extends BaseConnectionInformation {
	/**
	 * 数据库类型
	 * <p>
	 * 默认：{@code "SQLite"}
	 */
	@Override
	default String getDatabaseType() {
		return "SQLite";
	}

	/**
	 * 子协议
	 * <p>
	 * 默认：{@code "sqlite"}
	 */
	@Override
	default String getSubprotocol() {
		return "sqlite";
	}

	/**
	 * 主机名
	 * <p>
	 * 默认：{@code null}
	 */
	@Override
	default String getHostname() {
		return null;
	}

	/**
	 * 端口
	 * <p>
	 * 默认：{@code null}
	 */
	@Override
	default Integer getPort() {
		return null;
	}

	/**
	 * 数据库文件路径
	 */
	String getDatabaseFilePath();

	/**
	 * 数据库标识
	 *
	 * @see #getDatabaseFilePath()
	 */
	@Override
	default String getDatabaseId() {
		return getDatabaseFilePath();
	}

	/**
	 * 模式
	 * <p>
	 * 默认：{@code null}
	 */
	@Override
	default String getSchema() {
		return null;
	}

	/**
	 * 连接的基础 URL
	 *
	 * @see #getProtocol()
	 * @see #getDatabaseFilePath()
	 */
	@Override
	default String getBaseUrl() {
		return getProtocol() + ":" + getDatabaseFilePath();
	}

	/**
	 * 驱动类名
	 * <p>
	 * 默认：{@code "org.sqlite.JDBC"}
	 */
	@Override
	default String getDriverClassName() {
		return "org.sqlite.JDBC";
	}
}
