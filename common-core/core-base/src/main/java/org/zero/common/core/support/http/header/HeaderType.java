package org.zero.common.core.support.http.header;

/**
 * HTTP 标头类别
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/1/9
 */
public enum HeaderCategory {
	/**
	 * 请求标头
	 * <p>
	 * 只出现在 HTTP 请求消息中
	 */
	REQUEST,
	/**
	 * 响应标头
	 * <p>
	 * 只出现在 HTTP 响应消息中
	 */
	RESPONSE,
	/**
	 * 通用标头
	 * <p>
	 * 可能同时出现在 HTTP 请求和响应消息中
	 */
	COMMON;
}
