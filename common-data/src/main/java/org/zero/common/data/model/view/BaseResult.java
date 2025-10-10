package org.zero.common.data.model.view;

import org.zero.common.data.exception.BaseStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/11/18
 */
public interface BaseResult<T> extends org.zero.common.data.model.transfer.BaseResult {
	String OK_CODE = BaseStatus.OK_CODE;
	String ERROR_CODE = BaseStatus.ERROR_CODE;

	String OK_MSG = "操作成功";
	String ERROR_MSG = "操作失败";

	BaseStatus getStatus();

	default Serializable getCode() {
		BaseStatus status = getStatus();
		return Objects.nonNull(status) ? status.getCode() : null;
	}

	default CharSequence getMessage() {
		BaseStatus status = getStatus();
		return Objects.nonNull(status) ? status.getMessage() : null;
	}

	@Override
	default boolean isSuccess() {
		BaseStatus status = getStatus();
		return Objects.nonNull(status) && status.isOk();
	}

	LocalDateTime getTime();

	T getData();
}
