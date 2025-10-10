package org.zero.common.core.util.java.sql;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/9/18
 */
public interface BaseH2SererModeConnectionInformation extends BaseH2ConnectionInformation {
	/**
	 * 子协议
	 * <p>
	 * 默认：{@code "h2:tcp"}
	 */
	@Override
	default String getSubprotocol() {
		return "h2:tcp";
	}

	/**
	 * 端口
	 * <p>
	 * 默认：{@code 9092}
	 */
	@Override
	default Integer getPort() {
		return 9092;
	}
}
