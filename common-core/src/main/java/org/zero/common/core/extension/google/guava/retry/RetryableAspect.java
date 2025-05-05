package org.zero.common.core.extension.google.guava.retry;

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
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.ThrowableUtil;
import org.zero.common.core.util.java.reflect.ConstructorUtil;
import org.zero.common.core.util.java.reflect.MethodUtil;
import org.zero.common.data.exception.CommonException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
        // 设置需要重试的引发异常
        Class<? extends Throwable>[] includes = retryable.includes();
        for (Class<? extends Throwable> include : includes) {
            retryerBuilder.retryIfExceptionOfType(include);
        }
        // 设置重试策略
        Backoff backoff = retryable.backoff();
        long delay = backoff.delay();
        long maxDelay = backoff.maxDelay();
        long multiplier = backoff.multiplier();
        TimeUnit timeUnit = backoff.timeUnit();
        Backoff.WaitPolicy waitPolicy = backoff.waitPolicy();
        WaitStrategy waitStrategy;
        switch (waitPolicy) {
            case NONE:
                waitStrategy = WaitStrategies.noWait();
                break;
            case FIXED:
                waitStrategy = WaitStrategies.fixedWait(delay, timeUnit);
                break;
            case RANDOM:
                waitStrategy = WaitStrategies.randomWait(maxDelay, timeUnit);
                break;
            case INCREMENTING:
                waitStrategy = WaitStrategies.incrementingWait(delay, timeUnit, multiplier, timeUnit);
                break;
            case EXPONENTIAL:
                waitStrategy = WaitStrategies.exponentialWait(multiplier, maxDelay, timeUnit);
                break;
            case FIBONACCI:
                waitStrategy = WaitStrategies.fibonacciWait(multiplier, maxDelay, timeUnit);
                break;
            default:
                throw new CommonException(String.format("unknown wait strategy: %s", waitPolicy));
        }
        retryerBuilder.withWaitStrategy(waitStrategy);
        // 设置停止重试策略
        int maxAttempt = retryable.maxAttempt();
        StopStrategy stopStrategy;
        if (maxAttempt <= 0) {
            stopStrategy = StopStrategies.neverStop();
        } else {
            stopStrategy = StopStrategies.stopAfterAttempt(maxAttempt);
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
                    throw ThrowableUtil.throwUnchecked(e);
                }
            });
        } catch (Throwable t) {
            return this.recover(joinPoint, retryable, t);
        }
    }

    protected Object recover(ProceedingJoinPoint joinPoint, Retryable retryable, Throwable t) throws Throwable {
        String recover = retryable.recover();
        if (CharSequenceUtil.nonBlank(recover)) {
            Class<?> recoverClass = retryable.recoverClass();
            Object[] args = joinPoint.getArgs();
            Object target;
            if (recoverClass == Object.class) {
                // 获取目标对象（原始对象）实例，而不是代理对象
                target = joinPoint.getTarget();
            } else {
                target = ConstructorUtil.newInstanceOpt(recoverClass)
                        .orElseThrow(() -> new CommonException(String.format("%s does not provide a no-argument constructor", recoverClass.getCanonicalName())));
            }
            Class<?>[] argClasses = Arrays.stream(args)
                    .map(o -> {
                        if (Objects.isNull(o)) {
                            return void.class;
                        }
                        return o.getClass();
                    })
                    .toArray(Class[]::new);
            Class<?> targetClass = target.getClass();
            Optional<Method> recoverMethodOpt = MethodUtil.getMethodOptByNameAndParam(targetClass, recover, argClasses);
            if (recoverMethodOpt.isPresent()) {
                return MethodUtil.invoke(recoverMethodOpt.get(), target, args);
            }
            recoverMethodOpt = MethodUtil.getMethodOptByNameAndParam(targetClass, recover);
            if (recoverMethodOpt.isPresent()) {
                return MethodUtil.invoke(recoverMethodOpt.get(), target);
            }
            throw new CommonException(String.format("no such method like %s in %s", recover, targetClass.getCanonicalName()));
        }
        throw t;
    }
}
