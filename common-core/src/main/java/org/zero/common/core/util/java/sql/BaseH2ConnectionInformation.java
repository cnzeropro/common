package org.zero.common.core.util.java.sql;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/9/18
 */
public interface BaseH2ConnectionInformation extends BaseConnectionInformation {
	/**
	 * 数据库类型
	 * <p>
	 * 默认：{@code "H2"}
	 */
	@Override
	default String getDatabaseType() {
		return "H2";
	}

	/**
	 * 用户名
	 * 默认：{@code "sa"}
	 */
	@Override
	default String getUsername() {
		return "sa";
	}

	/**
	 * 驱动类名
	 * <p>
	 * 默认：{@code "org.h2.Driver"}
	 */
	@Override
	default String getDriverClassName() {
		return "org.h2.Driver";
	}

	/**
	 * 模式
	 * <p>
	 * 默认：{@code "PUBLIC"}
	 */
	@Override
	default String getSchema() {
		return "PUBLIC";
	}
}
