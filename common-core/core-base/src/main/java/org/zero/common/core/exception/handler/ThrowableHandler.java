package org.zero.common.core.exception.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.zero.common.core.exception.ThrowableMessageSupplier;
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
    protected final ThrowableMessageSupplier throwableMessageProvider;

    public Result<Void> handle(Throwable throwable, Object... args) {
        return this.handle(throwable, null, args);
    }

    public Result<Void> handle(Throwable throwable, Collection<Object> args) {
        return this.handle(throwable, args.toArray());
    }

    public Result<Void> handle(Throwable throwable, CharSequence defaultMessage, Object... args) {
        return this.handle(HttpStatus.INTERNAL_SERVER_ERROR, throwable, defaultMessage, args);
    }

    public Result<Void> handle(Throwable throwable, CharSequence defaultMessage, Collection<Object> args) {
        return this.handle(throwable, defaultMessage, args.toArray());
    }

    public Result<Void> handle(int code, Throwable throwable, Object... args) {
        return this.handle(code, throwable, null, args);
    }

    public Result<Void> handle(int code, Throwable throwable, Collection<Object> args) {
        return this.handle(code, throwable, args.toArray());
    }

    public Result<Void> handle(int code, Throwable throwable, CharSequence defaultMessage, Object... args) {
        CharSequence message = throwableMessageProvider.supply(throwable.getClass(),
                Objects.nonNull(defaultMessage) ? defaultMessage : throwable.getMessage(),
                args);
        return Result.error(code, message);
    }

    public Result<Void> handle(int code, Throwable throwable, CharSequence defaultMessage, Collection<Object> args) {
        return this.handle(code, throwable, defaultMessage, args.toArray());
    }

    public Result<Void> handle(HttpStatus httpStatus, Throwable throwable, Object... args) {
        return this.handle(httpStatus, throwable, null, args);
    }

    public Result<Void> handle(HttpStatus httpStatus, Throwable throwable, Collection<Object> args) {
        return this.handle(httpStatus, throwable, args.toArray());
    }

    public Result<Void> handle(HttpStatus httpStatus, Throwable throwable, CharSequence defaultMessage, Object... args) {
        return this.handle(httpStatus.value(), throwable, defaultMessage, args);
    }

    public Result<Void> handle(HttpStatus httpStatus, Throwable throwable, CharSequence defaultMessage, Collection<Object> args) {
        return this.handle(httpStatus, throwable, defaultMessage, args.toArray());
    }
}
