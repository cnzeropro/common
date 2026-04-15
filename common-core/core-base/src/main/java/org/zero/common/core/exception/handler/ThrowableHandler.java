package org.zero.common.core.exception.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.zero.common.core.exception.ThrowableMessageSupplier;
import org.zero.common.data.model.view.Result;

import java.util.Collection;
import java.util.Objects;

/**
 * 将异常转换为统一 {@link Result} 错误响应的基础处理器。
 * <p>
 * 该类提供多组重载方法，支持按默认错误码、指定业务码或 {@link HttpStatus} 构造错误结果，
 * 并委托 {@link ThrowableMessageSupplier} 统一生成最终错误消息。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/23
 */
@Getter
@RequiredArgsConstructor
public class ThrowableHandler {
    protected final ThrowableMessageSupplier throwableMessageProvider;

	/**
	 * 使用默认的 {@link HttpStatus#INTERNAL_SERVER_ERROR} 处理异常。
	 *
	 * @param throwable 异常对象
	 * @param args      消息格式化参数
	 * @return 统一错误结果
	 */
    public Result<Void> handle(Throwable throwable, Object... args) {
        return this.handle(throwable, null, args);
	}

	/**
	 * 使用默认的 {@link HttpStatus#INTERNAL_SERVER_ERROR} 处理异常。
	 *
	 * @param throwable 异常对象
	 * @param args 消息格式化参数集合
	 * @return 统一错误结果
	 */
    public Result<Void> handle(Throwable throwable, Collection<Object> args) {
        return this.handle(throwable, args.toArray());
	}

	/**
	 * 使用默认错误码处理异常，并允许提供兜底消息。
	 *
	 * @param throwable 异常对象
	 * @param defaultMessage 当异常本身缺少消息时使用的兜底消息
	 * @param args 消息格式化参数
	 * @return 统一错误结果
	 */
    public Result<Void> handle(Throwable throwable, CharSequence defaultMessage, Object... args) {
        return this.handle(HttpStatus.INTERNAL_SERVER_ERROR, throwable, defaultMessage, args);
	}

	/**
	 * 使用默认错误码处理异常，并允许提供兜底消息。
	 *
	 * @param throwable 异常对象
	 * @param defaultMessage 当异常本身缺少消息时使用的兜底消息
	 * @param args 消息格式化参数集合
	 * @return 统一错误结果
	 */
    public Result<Void> handle(Throwable throwable, CharSequence defaultMessage, Collection<Object> args) {
        return this.handle(throwable, defaultMessage, args.toArray());
	}

	/**
	 * 使用指定业务码处理异常。
	 *
	 * @param code 业务错误码
	 * @param throwable 异常对象
	 * @param args 消息格式化参数
	 * @return 统一错误结果
	 */
    public Result<Void> handle(int code, Throwable throwable, Object... args) {
        return this.handle(code, throwable, null, args);
	}

	/**
	 * 使用指定业务码处理异常。
	 *
	 * @param code 业务错误码
	 * @param throwable 异常对象
	 * @param args 消息格式化参数集合
	 * @return 统一错误结果
	 */
    public Result<Void> handle(int code, Throwable throwable, Collection<Object> args) {
        return this.handle(code, throwable, args.toArray());
	}

	/**
	 * 使用指定业务码处理异常，并生成最终错误结果。
	 *
	 * @param code 业务错误码
	 * @param throwable 异常对象
	 * @param defaultMessage 当异常本身缺少消息时使用的兜底消息
	 * @param args 消息格式化参数
	 * @return 统一错误结果
	 */
    public Result<Void> handle(int code, Throwable throwable, CharSequence defaultMessage, Object... args) {
        CharSequence message = throwableMessageProvider.supply(throwable.getClass(),
                Objects.nonNull(defaultMessage) ? defaultMessage : throwable.getMessage(),
                args);
        return Result.error(code, message);
	}

	/**
	 * 使用指定业务码处理异常，并允许通过参数集合格式化消息。
	 *
	 * @param code 业务错误码
	 * @param throwable 异常对象
	 * @param defaultMessage 当异常本身缺少消息时使用的兜底消息
	 * @param args 消息格式化参数集合
	 * @return 统一错误结果
	 */
    public Result<Void> handle(int code, Throwable throwable, CharSequence defaultMessage, Collection<Object> args) {
        return this.handle(code, throwable, defaultMessage, args.toArray());
	}

	/**
	 * 使用指定 HTTP 状态码处理异常。
	 *
	 * @param httpStatus HTTP 状态码
	 * @param throwable 异常对象
	 * @param args 消息格式化参数
	 * @return 统一错误结果
	 */
    public Result<Void> handle(HttpStatus httpStatus, Throwable throwable, Object... args) {
        return this.handle(httpStatus, throwable, null, args);
	}

	/**
	 * 使用指定 HTTP 状态码处理异常。
	 *
	 * @param httpStatus HTTP 状态码
	 * @param throwable 异常对象
	 * @param args 消息格式化参数集合
	 * @return 统一错误结果
	 */
    public Result<Void> handle(HttpStatus httpStatus, Throwable throwable, Collection<Object> args) {
        return this.handle(httpStatus, throwable, args.toArray());
	}

	/**
	 * 使用指定 HTTP 状态码处理异常，并委托业务码版本完成最终组装。
	 *
	 * @param httpStatus HTTP 状态码
	 * @param throwable 异常对象
	 * @param defaultMessage 当异常本身缺少消息时使用的兜底消息
	 * @param args 消息格式化参数
	 * @return 统一错误结果
	 */
    public Result<Void> handle(HttpStatus httpStatus, Throwable throwable, CharSequence defaultMessage, Object... args) {
        return this.handle(httpStatus.value(), throwable, defaultMessage, args);
	}

	/**
	 * 使用指定 HTTP 状态码处理异常，并允许通过参数集合格式化消息。
	 *
	 * @param httpStatus HTTP 状态码
	 * @param throwable 异常对象
	 * @param defaultMessage 当异常本身缺少消息时使用的兜底消息
	 * @param args 消息格式化参数集合
	 * @return 统一错误结果
	 */
    public Result<Void> handle(HttpStatus httpStatus, Throwable throwable, CharSequence defaultMessage, Collection<Object> args) {
        return this.handle(httpStatus, throwable, defaultMessage, args.toArray());
    }
}
