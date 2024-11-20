package org.zero.common.core.exception.handler.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.zero.common.data.model.vo.Result;

/**
 * 异常处理器
 * <p>
 * 异常建议从小到大（便于代码阅读和后期维护）
 *
 * @author Zero
 * @since 2020/03/21
 */
@Slf4j
@ControllerAdvice
@ConditionalOnWebApplication
public class JavaExceptionHandler {
    /* *************************************************** SQL异常 *************************************************** */

    @ExceptionHandler(java.sql.SQLIntegrityConstraintViolationException.class)
    public Result<Void> sqlIntegrityConstraintViolationException(java.sql.SQLIntegrityConstraintViolationException e) {
        log.error("SQL integrity constraint violation", e);
        return Result.fail("已存在该数据");
    }

    @ExceptionHandler(java.sql.BatchUpdateException.class)
    public Result<Void> batchUpdateException(java.sql.BatchUpdateException e) {
        log.error("Batch update exception", e);
        return Result.fail("批量更新异常");
    }

    @ExceptionHandler(java.sql.DataTruncation.class)
    public Result<Void> dataTruncation(java.sql.DataTruncation e) {
        log.error(String.format("The data is too long, index: %s, raw size: %s, transfer size: %s", e.getIndex(), e.getDataSize(), e.getTransferSize()), e);
        return Result.fail("数据过长");
    }

    @ExceptionHandler(java.sql.SQLDataException.class)
    public Result<Void> sqlDataException(java.sql.SQLDataException e) {
        log.error("SQL data exception", e);
        return Result.fail("SQL 数据错误");
    }

    @ExceptionHandler(java.sql.SQLSyntaxErrorException.class)
    public Result<Void> sqlSyntaxErrorException(java.sql.SQLSyntaxErrorException e) {
        log.error("SQL syntax error", e);
        return Result.fail("SQL 语法错误");
    }

    @ExceptionHandler(java.sql.SQLException.class)
    public Result<Void> sqlException(java.sql.SQLException e) {
        log.error("Exception accessing the database", e);
        return Result.fail("SQL 错误");
    }

    /* *************************************************** 其他异常 *************************************************** */

    @ExceptionHandler(java.io.IOException.class)
    public Result<Void> ioException(java.io.IOException e) {
        log.error("IO exception", e);
        return Result.fail("IO 异常");
    }

    @ExceptionHandler(java.util.concurrent.RejectedExecutionException.class)
    public Result<Void> rejectedExecutionException(java.util.concurrent.RejectedExecutionException e) {
        log.error("Thread pool is full", e);
        return Result.fail("线程池已满");
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<Void> runtimeException(RuntimeException e) {
        log.error("Runtime exception", e);
        return Result.fail("运行时异常");
    }

    /* *************************************************** 总异常 *************************************************** */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Result<Void> exception(Exception e) {
        log.error("System unknown exception", e);
        return Result.fail("系统未知异常，请联系管理员");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Error.class)
    public Result<Void> error(Error e) {
        log.error("System critical error", e);
        return Result.fail("系统严重错误，请联系管理员");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    public Result<Void> throwable(Throwable t) {
        log.error("System fatal mistake", t);
        return Result.fail("系统严重错误，建议联系管理员并尝试重启");
    }
}
