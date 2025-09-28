package org.zero.common.core.util.java.sql;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/9/18
 */
public interface BasePostgreSQLConnectionInformation extends BaseConnectionInformation {
	/**
	 * 数据库类型
	 * <p>
	 * 默认：{@code "PostgreSQL"}
	 */
	@Override
	default String getDatabaseType() {
		return "PostgreSQL";
	}

	/**
	 * 子协议
	 * <p>
	 * 默认：{@code "postgresql"}
	 */
	@Override
	default String getSubprotocol() {
		return "postgresql";
	}

	/**
	 * 端口
	 * <p>
	 * 默认：{@code 5432}
	 */
	@Override
	default Integer getPort() {
		return 5432;
	}

	/**
	 * 用户名
	 * <p>
	 * 默认：{@code "postgres"}
	 */
	@Override
	default String getUsername() {
		return "postgres";
	}

	/**
	 * 模式名
	 * <p>
	 * 默认：{@code "public"}
	 */
	@Override
	default String getSchema() {
		return "public";
	}

	/**
	 * 驱动类名
	 * <p>
	 * 默认：{@code "org.postgresql.Driver"}
	 */
	@Override
	default String getDriverClassName() {
		return "org.postgresql.Driver";
	}
}
