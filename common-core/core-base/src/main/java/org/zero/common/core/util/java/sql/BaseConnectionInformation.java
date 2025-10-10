package org.zero.common.core.util.java.sql;

import java.io.Serializable;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/9/18
 */
public interface BaseConnectionInformation extends Serializable {
	String MAIN_PROTOCOL = "jdbc";

	/**
	 * 数据库类型
	 */
	String getDatabaseType();

	/**
	 * 协议
	 * <p>
	 * 默认：{@code 主协议:子协议}
	 *
	 * @see #getMainProtocol()
	 * @see #getSubprotocol()
	 */
	default String getProtocol() {
		return getMainProtocol() + ":" + getSubprotocol();
	}

	/**
	 * 主协议
	 * <p>
	 * 默认：{@code "jdbc"}
	 */
	default String getMainProtocol() {
		return MAIN_PROTOCOL;
	}

	/**
	 * 子协议
	 */
	String getSubprotocol();

	/**
	 * 主机名
	 * <p>
	 * 默认：{@code "localhost"}
	 */
	default String getHostname() {
		return "localhost";
	}

	/**
	 * 端口
	 */
	Integer getPort();

	/**
	 * 数据库标识符
	 */
	String getDatabaseId();

	/**
	 * 模式
	 */
	String getSchema();

	/**
	 * 目录
	 * <p>
	 * 默认：null
	 */
	default String getCatalog() {
		return null;
	}

	/**
	 * 连接的基础 URL
	 * <p>
	 * 默认：{@code 协议://主机名:端口/数据库标识符}
	 *
	 * @see #getProtocol()
	 * @see #getHostname()
	 * @see #getPort()
	 * @see #getDatabaseId()
	 */
	default String getBaseUrl() {
		return getProtocol() + "://" + getHostname() + ":" + getPort() + "/" + getDatabaseId();
	}

	/**
	 * 连接 URL
	 * <p>
	 * 默认：{@code 基础 URL?查询字符串}
	 *
	 * @see #getBaseUrl()
	 * @see #getQueryString()
	 */
	default String getUrl() {
		return getBaseUrl() + "?" + getQueryString();
	}

	/**
	 * 连接参数
	 */
	Properties getProperties();

	/**
	 * 用户名
	 */
	String getUsername();

	/**
	 * 密码
	 */
	String getPassword();

	/**
	 * 合并后的连接参数
	 *
	 * @see #getMergedProperties()
	 * @see #getUsername()
	 * @see #getPassword()
	 */
	default Properties getMergedProperties() {
		Properties mergedProps = new Properties();
		Properties connectionProps = this.getProperties();
		if (Objects.nonNull(connectionProps)) {
			mergedProps.putAll(connectionProps);
		}
		String username = this.getUsername();
		if (Objects.nonNull(username)) {
			mergedProps.setProperty("user", username);
		}
		String password = this.getPassword();
		if (Objects.nonNull(password)) {
			mergedProps.setProperty("password", password);
		}
		return mergedProps;
	}

	/**
	 * 查询字符串
	 *
	 * @see #getProperties()
	 */
	default String getQueryString() {
		return getProperties().entrySet()
			.stream()
			.map(entry -> entry.getKey() + "=" + entry.getValue())
			.collect(Collectors.joining("&"));
	}

	/**
	 * 驱动类名
	 */
	String getDriverClassName();
}
