package org.zero.common.core.exception.handler.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zero.common.core.exception.ThrowableMessageProvider;
import org.zero.common.core.exception.handler.ThrowableHandler;
import org.zero.common.data.model.view.Result;

/**
 * 异常处理器
 * <p>
 * 异常建议从小到大（便于代码阅读和后期维护）
 *
 * @author Zero
 * @since 2020/03/21
 */
@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication
public class JavaExceptionHandler extends ThrowableHandler {
    public JavaExceptionHandler(ThrowableMessageProvider throwableMessageProvider) {
        super(throwableMessageProvider);
    }

    /* *************************************************** SQL异常 *************************************************** */

    @ExceptionHandler(java.sql.SQLIntegrityConstraintViolationException.class)
    public Result<Void> sqlIntegrityConstraintViolationException(java.sql.SQLIntegrityConstraintViolationException e) {
        log.error("SQL has integrity constraint violation", e);
        return this.handle(e);
    }

    @ExceptionHandler(java.sql.BatchUpdateException.class)
    public Result<Void> batchUpdateException(java.sql.BatchUpdateException e) {
        log.error("Data batch update exception", e);
        return this.handle(e);
    }

    @ExceptionHandler(java.sql.DataTruncation.class)
    public Result<Void> dataTruncation(java.sql.DataTruncation e) {
        log.error(String.format("The data is too long, index: %s, raw size: %s, transfer size: %s", e.getIndex(), e.getDataSize(), e.getTransferSize()), e);
        return this.handle(e);
    }

    @ExceptionHandler(java.sql.SQLDataException.class)
    public Result<Void> sqlDataException(java.sql.SQLDataException e) {
        log.error("SQL data exception", e);
        return this.handle(e);
    }

    @ExceptionHandler(java.sql.SQLSyntaxErrorException.class)
    public Result<Void> sqlSyntaxErrorException(java.sql.SQLSyntaxErrorException e) {
        log.error("SQL syntax error", e);
        return this.handle(e);
    }

    @ExceptionHandler(java.sql.SQLException.class)
    public Result<Void> sqlException(java.sql.SQLException e) {
        log.error("Exception accessing the database", e);
        return this.handle(e);
    }

    /* *************************************************** 其他异常 *************************************************** */

    @ExceptionHandler(java.io.IOException.class)
    public Result<Void> ioException(java.io.IOException e) {
        log.error("IO exception", e);
        return this.handle(e);
    }

    @ExceptionHandler(java.util.concurrent.RejectedExecutionException.class)
    public Result<Void> rejectedExecutionException(java.util.concurrent.RejectedExecutionException e) {
        log.error("Thread pool is full", e);
        return this.handle(e);
    }

    @ExceptionHandler(java.lang.NullPointerException.class)
    public Result<Void> nullPointerException(java.lang.NullPointerException e) {
        log.error("NPE", e);
        return this.handle(e);
    }

    @ExceptionHandler(java.lang.RuntimeException.class)
    public Result<Void> runtimeException(java.lang.RuntimeException e) {
        log.error("Runtime exception", e);
        return this.handle(e);
    }

    /* *************************************************** 总异常 *************************************************** */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Result<Void> exception(Exception e) {
        log.error("System unknown exception", e);
        return this.handle(e);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Error.class)
    public Result<Void> error(Error e) {
        log.error("System critical error", e);
        return this.handle(e);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public Result<Void> throwable(Throwable t) {
        log.error("System fatal mistake", t);
        return this.handle(t);
    }
}
