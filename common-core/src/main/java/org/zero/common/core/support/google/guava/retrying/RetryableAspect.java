package org.zero.common.core.support.google.guava.retrying;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.StopStrategy;
import com.github.rholder.retry.WaitStrategies;
import com.github.rholder.retry.WaitStrategy;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.StringUtils;
import org.zero.common.core.util.java.reflect.ReflectUtil;
import org.zero.common.data.exception.CommonException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/21
 */
@Slf4j
@Aspect
public class RetryableAspect {
    @Around("@within(retryable) || " +
            "@annotation(retryable)")
    public Object around(ProceedingJoinPoint joinPoint, Retryable retryable) throws Throwable {
        RetryerBuilder<Object> retryerBuilder = RetryerBuilder.newBuilder();

        Class<? extends Throwable>[] includes = retryable.include();
        for (Class<? extends Throwable> include : includes) {
            retryerBuilder.retryIfExceptionOfType(include);
        }
        Backoff backoff = retryable.backoff();
        Backoff.RetryPolicy retryPolicy = backoff.waitStrategy();
        WaitStrategy waitStrategy;
        switch (retryPolicy) {
            case NONE:
                waitStrategy = WaitStrategies.noWait();
                break;
            case FIXED:
                waitStrategy = WaitStrategies.fixedWait(backoff.delay(), backoff.unit());
                break;
            case RANDOM:
                waitStrategy = WaitStrategies.randomWait(backoff.maxTime(), backoff.unit());
                break;
            case INCREMENTING:
                waitStrategy = WaitStrategies.incrementingWait(backoff.delay(), backoff.unit(), backoff.multiplier(), backoff.unit());
                break;
            case EXPONENTIAL:
                waitStrategy = WaitStrategies.exponentialWait(backoff.multiplier(), backoff.maxTime(), backoff.unit());
                break;
            case FIBONACCI:
                waitStrategy = WaitStrategies.fibonacciWait(backoff.multiplier(), backoff.maxTime(), backoff.unit());
                break;
            default:
                throw new CommonException(String.format("unknown wait strategy: %s", retryPolicy));
        }
        retryerBuilder.withWaitStrategy(waitStrategy);

        int maxAttempts = retryable.maxAttempts();
        StopStrategy stopStrategy;
        if (maxAttempts <= 0) {
            stopStrategy = StopStrategies.neverStop();
        } else {
            stopStrategy = StopStrategies.stopAfterAttempt(maxAttempts);
        }
        retryerBuilder.withStopStrategy(stopStrategy);

        Retryer<Object> retryer = retryerBuilder.build();
        try {
            return retryer.call(() -> {
                try {
                    return joinPoint.proceed();
                } catch (Exception e) {
                    throw e;
                } catch (Throwable e) {
                    throw new CommonException("invocation failed", e);
                }
            });
        } catch (Throwable t) {
            return recover(joinPoint, retryable, t);
        }
    }

    protected Object recover(ProceedingJoinPoint joinPoint, Retryable retryable, Throwable t) throws Throwable {
        String recover = retryable.recover();
        if (StringUtils.hasText(recover)) {
            Class<?> recoverClass = retryable.recoverClass();
            Object[] args = joinPoint.getArgs();
            Object target;
            if (recoverClass == Object.class) {
                // 获取目标对象（原始对象）实例，而不是代理对象
                target = joinPoint.getTarget();
            } else {
                target = ReflectUtil.newInstanceOpt(recoverClass)
                        .orElseThrow(() -> new CommonException(String.format("%s does not provide a no-argument constructor", recoverClass.getCanonicalName())));
            }
            Class<?>[] argClasses = Arrays.stream(args)
                    .map(Object::getClass)
                    .toArray(Class[]::new);
            Optional<Method> recoverMethodOpt = ReflectUtil.getMethodOptByNameAndParam(target.getClass(), recover, argClasses);
            if (recoverMethodOpt.isPresent()) {
                return ReflectUtil.invoke(recoverMethodOpt.get(), target, args);
            } else {
                recoverMethodOpt = ReflectUtil.getMethodOptByNameAndParam(target.getClass(), recover);
                if (recoverMethodOpt.isPresent()) {
                    return ReflectUtil.invoke(recoverMethodOpt.get(), target);
                }
                throw new CommonException(String.format("no such method like %s in %s", recover, target.getClass().getCanonicalName()));
            }
        } else {
            throw t;
        }
    }
}
