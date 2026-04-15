package org.zero.common.core.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.zero.common.core.exception.ThrowableMessageSupplier;
import org.zero.common.data.model.view.Result;

import java.util.Collection;
import java.util.Objects;

/**
 * 将异常转换为统一错误响应的 Boot 3 适配处理器。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/15
 */
public class ThrowableHandler {
	protected final ThrowableMessageSupplier throwableMessageProvider;
	protected final ThrowableResponseType responseType;

	public ThrowableHandler(ThrowableMessageSupplier throwableMessageProvider) {
		this(throwableMessageProvider, ThrowableResponseType.PROBLEM_DETAIL);
	}

	public ThrowableHandler(ThrowableMessageSupplier throwableMessageProvider, ThrowableResponseType responseType) {
		this.throwableMessageProvider = Objects.requireNonNull(throwableMessageProvider, "throwableMessageProvider must not be null");
		this.responseType = Objects.requireNonNullElse(responseType, ThrowableResponseType.PROBLEM_DETAIL);
	}

	protected static HttpStatus resolveHttpStatus(int code) {
		HttpStatus httpStatus = HttpStatus.resolve(code);
		return Objects.nonNull(httpStatus) ? httpStatus : HttpStatus.INTERNAL_SERVER_ERROR;
	}

	protected static Object[] toArray(Collection<Object> args) {
		return Objects.nonNull(args) ? args.toArray() : new Object[0];
	}

	protected static String toMessage(CharSequence message) {
		return Objects.nonNull(message) ? message.toString() : null;
	}

	public Object handle(Throwable throwable, Object... args) {
		return this.handle(throwable, null, args);
	}

	public Object handle(Throwable throwable, Collection<Object> args) {
		return this.handle(throwable, toArray(args));
	}

	public Object handle(Throwable throwable, CharSequence defaultMessage, Object... args) {
		return this.handle(HttpStatus.INTERNAL_SERVER_ERROR, throwable, defaultMessage, args);
	}

	public Object handle(Throwable throwable, CharSequence defaultMessage, Collection<Object> args) {
		return this.handle(throwable, defaultMessage, toArray(args));
	}

	public Object handle(int code, Throwable throwable, Object... args) {
		return this.handle(code, throwable, null, args);
	}

	public Object handle(int code, Throwable throwable, Collection<Object> args) {
		return this.handle(code, throwable, toArray(args));
	}

	public Object handle(int code, Throwable throwable, CharSequence defaultMessage, Object... args) {
		CharSequence message = this.throwableMessageProvider.supply(
				throwable.getClass(),
				Objects.nonNull(defaultMessage) ? defaultMessage : throwable.getMessage(),
				args
		);
		return this.createResponse(code, message);
	}

	public Object handle(int code, Throwable throwable, CharSequence defaultMessage, Collection<Object> args) {
		return this.handle(code, throwable, defaultMessage, toArray(args));
	}

	public Object handle(HttpStatus httpStatus, Throwable throwable, Object... args) {
		return this.handle(httpStatus, throwable, null, args);
	}

	public Object handle(HttpStatus httpStatus, Throwable throwable, Collection<Object> args) {
		return this.handle(httpStatus, throwable, toArray(args));
	}

	public Object handle(HttpStatus httpStatus, Throwable throwable, CharSequence defaultMessage, Object... args) {
		return this.handle(httpStatus.value(), throwable, defaultMessage, args);
	}

	public Object handle(HttpStatus httpStatus, Throwable throwable, CharSequence defaultMessage, Collection<Object> args) {
		return this.handle(httpStatus, throwable, defaultMessage, toArray(args));
	}

	protected Object createResponse(int code, CharSequence message) {
		if (this.responseType == ThrowableResponseType.RESULT) {
			return Result.error(code, message);
		}
		ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(resolveHttpStatus(code), toMessage(message));
		problemDetail.setProperty("code", code);
		return problemDetail;
	}
}
