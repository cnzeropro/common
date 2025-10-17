package org.zero.common.data.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.zero.common.data.exception.Status;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2018/11/29
 */
@Data
@Builder(toBuilder = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public final class Result<T> implements BaseResult<T> {
	private static final long serialVersionUID = 7893804841950761019L;
	/**
	 * 请求状态
	 */
	private Status status;
	/**
	 * 成功标志
	 */
	private boolean success;
	/**
	 * 响应时间
	 */
	@Builder.Default
	private LocalDateTime time = LocalDateTime.now();
	/**
	 * 数据对象
	 */
	private T data;

	/* ******************************************************** 请求成功 ******************************************************** */
	public static <T> Result<T> ok() {
		return ok((T) null);
	}

	public static <T> Result<T> ok(CharSequence message) {
		return ok(OK_CODE, message, null);
	}

	public static <T> Result<T> ok(Status status) {
		return ok(status, (T) null);
	}

	public static <T> Result<T> ok(Serializable code, CharSequence message) {
		return ok(code, message, null);
	}

	public static <T> Result<T> ok(T data) {
		return ok(Status.Default.OK, data);
	}

	public static <T> Result<T> ok(CharSequence message, T data) {
		return ok(OK_CODE, message, data);
	}

	public static <T> Result<T> ok(Serializable code, CharSequence message, T data) {
		return of(code, message, data, true);
	}

	public static <T> Result<T> ok(Status status, T data) {
		return of(status, data, true);
	}

	/* ******************************************************** 请求失败 ******************************************************** */
	public static <T> Result<T> error() {
		return error((T) null);
	}

	public static <T> Result<T> error(CharSequence message) {
		return error(ERROR_CODE, message, null);
	}

	public static <T> Result<T> error(Status status) {
		return error(status, (T) null);
	}

	public static <T> Result<T> error(Serializable code, CharSequence message) {
		return error(code, message, null);
	}

	public static <T> Result<T> error(T data) {
		return error(Status.Default.ERROR, data);
	}

	public static <T> Result<T> error(Serializable code, CharSequence message, T data) {
		return of(code, message, data, false);
	}

	public static <T> Result<T> error(Status status, T data) {
		return of(status, data, false);
	}

	/* ******************************************************** 通用构造 ******************************************************** */
	public static <T> Result<T> of(Serializable code, CharSequence message, T data) {
		return of(Status.Default.of(code, message), data);
	}

	public static <T> Result<T> of(Serializable code, CharSequence message, T data, boolean success) {
		return of(Status.Default.of(code, message), data, success);
	}

	public static <T> Result<T> of(Serializable code, CharSequence message, T data, LocalDateTime time) {
		return of(Status.Default.of(code, message), data, time);
	}

	public static <T> Result<T> of(Serializable code, CharSequence message, T data, boolean success, LocalDateTime time) {
		return of(Status.Default.of(code, message), data, success, time);
	}

	public static <T> Result<T> of(Status status, T data) {
		return of(status, data, Objects.nonNull(status) && status.isOk());
	}

	public static <T> Result<T> of(Status status, T data, boolean success) {
		return of(status, data, success, LocalDateTime.now());
	}

	public static <T> Result<T> of(Status status, T data, LocalDateTime time) {
		return of(status, data, Objects.nonNull(status) && status.isOk(), time);
	}

	public static <T> Result<T> of(Status status, T data, boolean success, LocalDateTime time) {
		return Result.<T>builder()
			.status(status)
			.success(success)
			.time(time)
			.data(data)
			.build();
	}
}
