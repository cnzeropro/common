package org.zero.common.core.support.log.handler.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.zero.common.core.support.log.handler.annotation.LogHandler;
import org.zero.common.core.support.log.handler.processor.DefaultLogProcessor;
import org.zero.common.core.support.log.handler.processor.LogProcessor;
import org.zero.common.core.util.java.reflect.MemberUtil;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * 操作日志切面
 * <p>
 * 请使用 {@linkplain  org.springframework.context.annotation.Bean @Bean} 注解把该类注入到 Spring 容器，因为需要指定 {@link Executor}。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/15
 */
@Aspect
@RequiredArgsConstructor
public class LogHandlerAspect {
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    protected final Executor executor;

    @Around("@annotation(logHandler) || @within(logHandler)")
    public Object around(ProceedingJoinPoint joinPoint, LogHandler logHandler) throws Throwable {
        if (!logHandler.enable()) {
            return joinPoint.proceed();
        }
        LogProcessor logProcessor = this.getLogHandler(logHandler);
        Map<String, Object> context = new LinkedHashMap<>();
        this.run(logHandler.async(), () -> logProcessor.processBefore(joinPoint, context));
        context.put(START_TIME, LocalDateTime.now());
        try {
            Object result = joinPoint.proceed();
            context.put(END_TIME, LocalDateTime.now());
            this.run(logHandler.async(), () -> logProcessor.processAfterReturning(joinPoint, result, context));
            return result;
        } catch (Throwable e) {
            context.put(END_TIME, LocalDateTime.now());
            this.run(logHandler.async(), () -> logProcessor.processAfterThrowing(joinPoint, e, context));
            throw e;
        } finally {
            this.run(logHandler.async(), () -> logProcessor.processAfter(joinPoint, context));
        }
    }

    protected void run(boolean async, Runnable task) {
        if (async) {
            executor.execute(task);
        } else {
            task.run();
        }
    }

    protected LogProcessor getLogHandler(LogHandler logHandler) {
        return Optional.ofNullable(logHandler.processor())
                .<LogProcessor>map(clazz -> MemberUtil.getInstance(clazz, true))
                .orElse(DefaultLogProcessor.INSTANCE);
    }
}
