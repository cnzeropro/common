package org.zero.common.core.util.java.sql;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/9/18
 */
public interface BaseH2EmbeddedModeConnectionInformation extends BaseH2ConnectionInformation {
	/**
	 * 子协议
	 * <p>
	 * 默认：{@code "h2:file"}
	 */
	@Override
	default String getSubprotocol() {
		return "h2:file";
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
	 * 连接的基础 URL
	 * <p>
	 * 默认：{@code 协议:数据库文件路径}
	 *
	 * @see #getProtocol()
	 * @see #getDatabaseFilePath()
	 */
	@Override
	default String getBaseUrl() {
		return getProtocol() + ":" + getDatabaseFilePath();
	}
}
