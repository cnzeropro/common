package org.zero.common.core.util.java.sql;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/9/18
 */
public interface BaseMySQLConnectionInformation extends BaseConnectionInformation {
	/**
	 * 数据库类型
	 * <p>
	 * 默认：{@code "MySQL"}
	 */
	@Override
	default String getDatabaseType() {
		return "MySQL";
	}

	/**
	 * 子协议
	 * <p>
	 * 默认：{@code "mysql"}
	 */
	@Override
	default String getSubprotocol() {
		return "mysql";
	}

	/**
	 * 端口
	 * <p>
	 * 默认：{@code 3306}
	 */
	@Override
	default Integer getPort() {
		return 3306;
	}

	/**
	 * 数据库名称
	 */
	String getDatabaseName();

	/**
	 * 数据库标识
	 *
	 * @see #getDatabaseName()
	 */
	@Override
	default String getDatabaseId() {
		return getDatabaseName();
	}

	/**
	 * 用户名
	 * <p>
	 * 默认：{@code "root"}
	 */
	@Override
	default String getUsername() {
		return "root";
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
	 * 驱动类名
	 * <p>
	 * 默认：{@code "com.mysql.cj.jdbc.Driver"}
	 */
	@Override
	default String getDriverClassName() {
		return "com.mysql.cj.jdbc.Driver";
	}
}
