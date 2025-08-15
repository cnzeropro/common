package org.zero.common.core.exception.handler.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
public class SpringDataRedisExceptionHandler extends ThrowableHandler {
    public SpringDataRedisExceptionHandler(ThrowableMessageProvider throwableMessageProvider) {
        super(throwableMessageProvider);
    }

    /* *************************************************** Redis 异常 *************************************************** */

    @ExceptionHandler(org.springframework.data.redis.connection.PoolException.class)
    public Result<Void> poolException(org.springframework.data.redis.connection.PoolException e) {
        log.error("Redis connection pool exception", e);
        return this.handle(e);
    }

    @ExceptionHandler(org.springframework.data.redis.RedisSystemException.class)
    public Result<Void> redisSystemException(org.springframework.data.redis.RedisSystemException e) {
        log.error("Redis system exception", e);
        return this.handle(e);
    }

    @ExceptionHandler(org.springframework.data.redis.RedisConnectionFailureException.class)
    public Result<Void> redisConnectionFailureException(org.springframework.data.redis.RedisConnectionFailureException e) {
        log.error("Redis connection failed", e);
        return this.handle(e);
    }
}
