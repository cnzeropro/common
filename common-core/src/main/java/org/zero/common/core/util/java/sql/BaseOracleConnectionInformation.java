package org.zero.common.core.util.java.sql;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/9/18
 */
public interface BaseOracleConnectionInformation extends BaseConnectionInformation {
	/**
	 * 数据库类型
	 * <p>
	 * 默认：{@code "Oracle"}
	 */
	@Override
	default String getDatabaseType() {
		return "Oracle";
	}

	/**
	 * 子协议
	 * <p>
	 * 默认：{@code "oracle:thin"}
	 */
	@Override
	default String getSubprotocol() {
		return "oracle:thin";
	}

	/**
	 * 端口
	 * <p>
	 * 默认：{@code 1521}
	 */
	@Override
	default Integer getPort() {
		return 1521;
	}

	/**
	 * 服务器名称
	 */
	String getServerName();

	/**
	 * 数据库标识
	 *
	 * @see #getServerName()
	 */
	@Override
	default String getDatabaseId() {
		return getServerName();
	}

	/**
	 * 连接的基础 URL
	 */
	@Override
	default String getBaseUrl() {
		return getProtocol() + ":@" + getHostname() + ":" + getPort() + ":" + getServerName();
	}

	/**
	 * 驱动类名称
	 * <p>
	 * 默认：{@code "oracle.jdbc.driver.OracleDriver"}
	 */
	@Override
	default String getDriverClassName() {
		return "oracle.jdbc.driver.OracleDriver";
	}
}
