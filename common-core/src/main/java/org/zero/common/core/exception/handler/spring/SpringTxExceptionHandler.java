package org.zero.common.core.exception.handler.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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
@RestControllerAdvice
@ConditionalOnWebApplication
public class SpringTxExceptionHandler {
    /* *************************************************** JDBC异常 *************************************************** */
    @ExceptionHandler(org.springframework.dao.DuplicateKeyException.class)
    public Result<Void> duplicateKeyException(org.springframework.dao.DuplicateKeyException e) {
        log.error("The data violates a primary key or unique constraint", e);
        return Result.fail("已存在该数据");
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public Result<Void> dataIntegrityViolationException(org.springframework.dao.DataIntegrityViolationException e) {
        log.error("Data integrity violations (violation of database constraints)", e);
        return Result.fail("违反数据库约束");
    }

    @ExceptionHandler(org.springframework.dao.CannotAcquireLockException.class)
    public Result<Void> cannotAcquireLockException(org.springframework.dao.CannotAcquireLockException e) {
        log.error("Unable to acquire database lock, check for deadlock", e);
        return Result.fail("获取数据库锁失败");
    }

    @ExceptionHandler(org.springframework.dao.CannotSerializeTransactionException.class)
    public Result<Void> cannotSerializeTransactionException(org.springframework.dao.CannotSerializeTransactionException e) {
        log.error("Serialize transaction exception", e);
        return Result.fail("无法序列化事务");
    }

    @ExceptionHandler(org.springframework.dao.QueryTimeoutException.class)
    public Result<Void> cannotSerializeTransactionException(org.springframework.dao.QueryTimeoutException e) {
        log.error("Database query timed out", e);
        return Result.fail("数据查询超时");
    }

    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public Result<Void> dataAccessException(org.springframework.dao.DataAccessException e) {
        log.error("Data access exception", e);
        return Result.fail("数据访问层错误");
    }
}
