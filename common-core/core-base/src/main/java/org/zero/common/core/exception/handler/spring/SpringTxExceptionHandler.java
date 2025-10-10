package org.zero.common.core.exception.handler.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.zero.common.core.exception.ThrowableMessageSupplier;
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
public class SpringTxExceptionHandler extends ThrowableHandler {
    public SpringTxExceptionHandler(ThrowableMessageSupplier throwableMessageProvider) {
        super(throwableMessageProvider);
    }

    /* *************************************************** JDBC异常 *************************************************** */
    @ExceptionHandler(org.springframework.dao.DuplicateKeyException.class)
    public Result<Void> duplicateKeyException(org.springframework.dao.DuplicateKeyException e) {
        log.error("The data violates a primary key or unique constraint", e);
        return this.handle(e);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public Result<Void> dataIntegrityViolationException(org.springframework.dao.DataIntegrityViolationException e) {
        log.error("Data integrity violations (violation of database constraints)", e);
        return this.handle(e);
    }

    @ExceptionHandler(org.springframework.dao.CannotAcquireLockException.class)
    public Result<Void> cannotAcquireLockException(org.springframework.dao.CannotAcquireLockException e) {
        log.error("Unable to acquire database lock, check for deadlock", e);
        return this.handle(e);
    }

    @ExceptionHandler(org.springframework.dao.CannotSerializeTransactionException.class)
    public Result<Void> cannotSerializeTransactionException(org.springframework.dao.CannotSerializeTransactionException e) {
        log.error("Serialize transaction exception", e);
        return this.handle(e);
    }

    @ExceptionHandler(org.springframework.dao.QueryTimeoutException.class)
    public Result<Void> cannotSerializeTransactionException(org.springframework.dao.QueryTimeoutException e) {
        log.error("Database query timed out", e);
        return this.handle(e);
    }

    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public Result<Void> dataAccessException(org.springframework.dao.DataAccessException e) {
        log.error("Data access exception", e);
        return this.handle(e);
    }
}
