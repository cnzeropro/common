package org.zero.common.data.model.view;

import org.zero.common.data.enumeration.Status;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 带泛型数据的通用结果接口，提供状态码、消息、数据和时间等标准字段。
 *
 * @param <T> 响应数据类型
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/18
 */
public interface BaseResult<T> extends org.zero.common.data.model.transfer.BaseResult {

	/**
	 * 成功状态码
	 */
	String OK_CODE = Status.OK_CODE;

	/**
	 * 失败状态码
	 */
	String ERROR_CODE = Status.ERROR_CODE;

	/**
	 * 默认成功消息
	 */
	String OK_MESSAGE = "Operation succeeded";

	/**
	 * 默认失败消息
	 */
	String ERROR_MESSAGE = "Operation failed";

	/**
	 * 获取请求状态。
	 *
	 * @return 状态对象
	 */
	Status getStatus();

	/**
	 * 获取状态码。
	 *
	 * @return 状态码，状态为 {@code null} 时返回 {@code null}
	 */
	default Serializable getCode() {
		Status status = getStatus();
		return Objects.nonNull(status) ? status.getCode() : null;
	}

	/**
	 * 获取状态消息。
	 *
	 * @return 消息文本，状态为 {@code null} 时返回 {@code null}
	 */
	default CharSequence getMessage() {
		Status status = getStatus();
		return Objects.nonNull(status) ? status.getMessage() : null;
	}

	/**
	 * 判断操作是否成功：状态非空且 {@link Status#isOk()} 返回 {@code true}。
	 *
	 * @return {@code true} 表示成功
	 */
	@Override
	default boolean isSuccess() {
		Status status = getStatus();
		return Objects.nonNull(status) && status.isOk();
	}

	/**
	 * 获取响应时间。
	 *
	 * @return 响应时间
	 */
	LocalDateTime getTime();

	/**
	 * 获取响应数据。
	 *
	 * @return 数据对象
	 */
	T getData();
}
