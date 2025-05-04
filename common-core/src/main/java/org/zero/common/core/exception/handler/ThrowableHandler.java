package org.zero.common.core.exception.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.zero.common.core.exception.ThrowableMessageProvider;
import org.zero.common.data.model.view.BaseResult;
import org.zero.common.data.model.view.Result;

import java.util.Collection;
import java.util.Objects;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/23
 */
@Getter
@RequiredArgsConstructor
public class ThrowableHandler {
    protected final ThrowableMessageProvider throwableMessageProvider;

    public Result<Void> handleThrowable(Throwable throwable, Object... args) {
        return this.handleThrowable(throwable, null, args);
    }

    public Result<Void> handleThrowable(Throwable throwable, Collection<Object> args) {
        return this.handleThrowable(throwable, args.toArray());
    }

    public Result<Void> handleThrowable(Throwable throwable, CharSequence defaultMessage, Object... args) {
        return this.handleThrowable(BaseResult.FAIL_CODE, throwable, defaultMessage, args);
    }

    public Result<Void> handleThrowable(Throwable throwable, CharSequence defaultMessage, Collection<Object> args) {
        return this.handleThrowable(throwable, defaultMessage, args.toArray());
    }

    public Result<Void> handleThrowable(int code, Throwable throwable, Object... args) {
        return this.handleThrowable(code, throwable, null, args);
    }

    public Result<Void> handleThrowable(int code, Throwable throwable, Collection<Object> args) {
        return this.handleThrowable(code, throwable, args.toArray());
    }

    public Result<Void> handleThrowable(int code, Throwable throwable, CharSequence defaultMessage, Object... args) {
        CharSequence message = throwableMessageProvider.provide(throwable.getClass(),
                Objects.nonNull(defaultMessage) ? defaultMessage : throwable.getMessage(),
                args);
        return Result.fail(code, message);
    }

    public Result<Void> handleThrowable(int code, Throwable throwable, CharSequence defaultMessage, Collection<Object> args) {
        return this.handleThrowable(code, throwable, defaultMessage, args.toArray());
    }

    public Result<Void> handleThrowable(HttpStatus httpStatus, Throwable throwable, Object... args) {
        return this.handleThrowable(httpStatus, throwable, null, args);
    }

    public Result<Void> handleThrowable(HttpStatus httpStatus, Throwable throwable, Collection<Object> args) {
        return this.handleThrowable(httpStatus, throwable, args.toArray());
    }

    public Result<Void> handleThrowable(HttpStatus httpStatus, Throwable throwable, CharSequence defaultMessage, Object... args) {
        return this.handleThrowable(httpStatus.value(), throwable, defaultMessage, args);
    }

    public Result<Void> handleThrowable(HttpStatus httpStatus, Throwable throwable, CharSequence defaultMessage, Collection<Object> args) {
        return this.handleThrowable(httpStatus, throwable, defaultMessage, args.toArray());
    }
}
