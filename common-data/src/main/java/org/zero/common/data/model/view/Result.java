package org.zero.common.data.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.zero.common.data.enumeration.Status;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 通用响应结果，封装状态码、消息、数据及响应时间。
 *
 * <p>提供 {@code ok(...)} / {@code error(...)} / {@code of(...)} 三组静态工厂方法，覆盖绝大多数响应场景。
 *
 * @param <T> 响应数据类型
 * @author Zero (cnzeropro@qq.com)
 * @since 2018/11/29
 */
@Data
@SuperBuilder(toBuilder = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public final class Result<T> implements BaseResult<T> {
	private static final long serialVersionUID = 7893804841950761019L;

	/**
	 * 请求状态
	 */
	private Status status;

	/** 成功标志 */
	private boolean success;

	/** 响应时间 */
	@Builder.Default
	private LocalDateTime time = LocalDateTime.now();

	/** 数据对象 */
	private T data;

	/* ******************************************************** 请求成功 ******************************************************** */

	/**
	 * 返回无数据的成功结果。
	 *
	 * @param <T> 数据类型
	 * @return 成功结果
	 */
	public static <T> Result<T> ok() {
		return ok((T) null);
	}

	/**
	 * 返回带消息的成功结果。
	 *
	 * @param message 消息
	 * @param <T>     数据类型
	 * @return 成功结果
	 */
	public static <T> Result<T> ok(CharSequence message) {
		return ok(OK_CODE, message, null);
	}

	/**
	 * 返回指定状态的成功结果。
	 *
	 * @param status 状态
	 * @param <T>   数据类型
	 * @return 成功结果
	 */
	public static <T> Result<T> ok(Status status) {
		return ok(status, (T) null);
	}

	/**
	 * 返回带状态码和消息的成功结果。
	 *
	 * @param code    状态码
	 * @param message 消息
	 * @param <T>     数据类型
	 * @return 成功结果
	 */
	public static <T> Result<T> ok(Serializable code, CharSequence message) {
		return ok(code, message, null);
	}

	/**
	 * 返回带数据的成功结果。
	 *
	 * @param data 数据
	 * @param <T>  数据类型
	 * @return 成功结果
	 */
	public static <T> Result<T> ok(T data) {
		return ok(Status.Default.OK, data);
	}

	/**
	 * 返回带消息和数据的成功结果。
	 *
	 * @param message 消息
	 * @param data    数据
	 * @param <T>     数据类型
	 * @return 成功结果
	 */
	public static <T> Result<T> ok(CharSequence message, T data) {
		return ok(OK_CODE, message, data);
	}

	/**
	 * 返回带状态码、消息和数据的成功结果。
	 *
	 * @param code    状态码
	 * @param message 消息
	 * @param data    数据
	 * @param <T>     数据类型
	 * @return 成功结果
	 */
	public static <T> Result<T> ok(Serializable code, CharSequence message, T data) {
		return of(code, message, data, true);
	}

	/**
	 * 返回指定状态和数据的成功结果。
	 *
	 * @param status 状态
	 * @param data   数据
	 * @param <T>   数据类型
	 * @return 成功结果
	 */
	public static <T> Result<T> ok(Status status, T data) {
		return of(status, data, true);
	}

	/* ******************************************************** 请求失败 ******************************************************** */

	/**
	 * 返回无数据的失败结果。
	 *
	 * @param <T> 数据类型
	 * @return 失败结果
	 */
	public static <T> Result<T> error() {
		return error((T) null);
	}

	/**
	 * 返回带消息的失败结果。
	 *
	 * @param message 消息
	 * @param <T>     数据类型
	 * @return 失败结果
	 */
	public static <T> Result<T> error(CharSequence message) {
		return error(ERROR_CODE, message, null);
	}

	/**
	 * 返回指定状态的失败结果。
	 *
	 * @param status 状态
	 * @param <T>   数据类型
	 * @return 失败结果
	 */
	public static <T> Result<T> error(Status status) {
		return error(status, (T) null);
	}

	/**
	 * 返回带状态码和消息的失败结果。
	 *
	 * @param code    状态码
	 * @param message 消息
	 * @param <T>     数据类型
	 * @return 失败结果
	 */
	public static <T> Result<T> error(Serializable code, CharSequence message) {
		return error(code, message, null);
	}

	/**
	 * 返回带数据的失败结果。
	 *
	 * @param data 数据
	 * @param <T>  数据类型
	 * @return 失败结果
	 */
	public static <T> Result<T> error(T data) {
		return error(Status.Default.ERROR, data);
	}

	/**
	 * 返回带状态码、消息和数据的失败结果。
	 *
	 * @param code    状态码
	 * @param message 消息
	 * @param data    数据
	 * @param <T>     数据类型
	 * @return 失败结果
	 */
	public static <T> Result<T> error(Serializable code, CharSequence message, T data) {
		return of(code, message, data, false);
	}

	/**
	 * 返回指定状态和数据的失败结果。
	 *
	 * @param status 状态
	 * @param data   数据
	 * @param <T>   数据类型
	 * @return 失败结果
	 */
	public static <T> Result<T> error(Status status, T data) {
		return of(status, data, false);
	}

	/* ******************************************************** 通用构造 ******************************************************** */

	/**
	 * 根据状态码、消息和数据构建结果，成功与否由 {@link Status#isOk()} 决定。
	 *
	 * @param code    状态码
	 * @param message 消息
	 * @param data    数据
	 * @param <T>     数据类型
	 * @return 结果
	 */
	public static <T> Result<T> of(Serializable code, CharSequence message, T data) {
		return of(Status.Default.of(code, message), data);
	}

	/**
	 * 根据状态码、消息、数据和成功标志构建结果。
	 *
	 * @param code    状态码
	 * @param message 消息
	 * @param data    数据
	 * @param success 是否成功
	 * @param <T>     数据类型
	 * @return 结果
	 */
	public static <T> Result<T> of(Serializable code, CharSequence message, T data, boolean success) {
		return of(Status.Default.of(code, message), data, success);
	}

	/**
	 * 根据状态码、消息、数据和响应时间构建结果，成功与否由 {@link Status#isOk()} 决定。
	 *
	 * @param code    状态码
	 * @param message 消息
	 * @param data    数据
	 * @param time    响应时间
	 * @param <T>     数据类型
	 * @return 结果
	 */
	public static <T> Result<T> of(Serializable code, CharSequence message, T data, LocalDateTime time) {
		return of(Status.Default.of(code, message), data, time);
	}

	/**
	 * 根据状态码、消息、数据、成功标志和响应时间构建结果。
	 *
	 * @param code    状态码
	 * @param message 消息
	 * @param data    数据
	 * @param success 是否成功
	 * @param time    响应时间
	 * @param <T>     数据类型
	 * @return 结果
	 */
	public static <T> Result<T> of(Serializable code, CharSequence message, T data, boolean success, LocalDateTime time) {
		return of(Status.Default.of(code, message), data, success, time);
	}

	/**
	 * 根据状态和数据构建结果，成功与否由 {@link Status#isOk()} 决定。
	 *
	 * @param status 状态
	 * @param data   数据
	 * @param <T>   数据类型
	 * @return 结果
	 */
	public static <T> Result<T> of(Status status, T data) {
		return of(status, data, Objects.nonNull(status) && status.isOk());
	}

	/**
	 * 根据状态、数据和成功标志构建结果。
	 *
	 * @param status  状态
	 * @param data    数据
	 * @param success 是否成功
	 * @param <T>    数据类型
	 * @return 结果
	 */
	public static <T> Result<T> of(Status status, T data, boolean success) {
		return of(status, data, success, LocalDateTime.now());
	}

	/**
	 * 根据状态、数据和响应时间构建结果，成功与否由 {@link Status#isOk()} 决定。
	 *
	 * @param status 状态
	 * @param data   数据
	 * @param time   响应时间
	 * @param <T>   数据类型
	 * @return 结果
	 */
	public static <T> Result<T> of(Status status, T data, LocalDateTime time) {
		return of(status, data, Objects.nonNull(status) && status.isOk(), time);
	}

	/**
	 * 根据状态、数据、成功标志和响应时间构建结果（最终构建入口）。
	 *
	 * @param status  状态
	 * @param data    数据
	 * @param success 是否成功
	 * @param time    响应时间
	 * @param <T>    数据类型
	 * @return 结果
	 */
	public static <T> Result<T> of(Status status, T data, boolean success, LocalDateTime time) {
		return Result.<T>builder()
			.status(status)
			.success(success)
			.time(time)
			.data(data)
			.build();
	}
}
