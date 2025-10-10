package org.zero.common.data.exception;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

/**
 * 基础异常
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/6/20
 */
@Getter
public class BaseException extends RuntimeException {
	/**
	 * 错误状态
	 */
	protected final BaseStatus status;

	/* ************************************************************** Exception() ************************************************************** */
	public BaseException() {
		this(BaseStatus.DefaultStatus.ERROR);
	}

	public BaseException(BaseStatus status) {
		this(String.valueOf(status.getMessage()), status);
	}

	/* ************************************************************** Exception(java.lang.String) ************************************************************** */
	public BaseException(String message) {
		this(message, BaseStatus.DefaultStatus.ERROR);
	}

	public BaseException(String message, BaseStatus status) {
		super(message);
		this.status = status;
	}

	/* ************************************************************** Exception(java.lang.Throwable) ************************************************************** */
	public BaseException(Throwable cause) {
		this(cause, BaseStatus.DefaultStatus.ERROR);
	}

	public BaseException(Throwable cause, BaseStatus status) {
		super(cause);
		this.status = status;
	}

	/* ************************************************************** Exception(java.lang.String, java.lang.Throwable) ************************************************************** */
	public BaseException(String message, Throwable cause) {
		this(message, cause, BaseStatus.DefaultStatus.ERROR);
	}

	public BaseException(String message, Throwable cause, BaseStatus status) {
		super(message, cause);
		this.status = status;
	}

	/* ************************************************************** other ************************************************************** */
	public Serializable getErrorCode() {
		return status.getCode();
	}

	public CharSequence getErrorMessage() {
		return status.getMessage();
	}

	@Override
	public String getLocalizedMessage() {
		return Objects.toString(this.getErrorMessage(), null);
	}
}
