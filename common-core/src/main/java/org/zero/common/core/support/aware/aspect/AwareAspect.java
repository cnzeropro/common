package org.zero.common.core.support.aware.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.zero.common.core.support.aware.annotation.Aware;
import org.zero.common.core.support.aware.perceptor.DefaultPerceptor;
import org.zero.common.core.support.aware.perceptor.Perceptor;
import org.zero.common.core.util.java.reflect.MemberUtil;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;

/**
 * 方法感知切面
 * <p>
 * 请使用 {@linkplain  org.springframework.context.annotation.Bean @Bean} 注解把该类注入到 Spring 容器，因为需要指定 {@link Executor}。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/15
 */
@Aspect
@RequiredArgsConstructor
public class AwareAspect {
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    protected final Executor executor;

    @Around("@annotation(aware)")
    public Object aroundMethodAnnotation(ProceedingJoinPoint joinPoint, Aware aware) throws Throwable {
        return this.around(joinPoint, aware);
    }

    @Around("@within(aware)")
    public Object aroundClassAnnotation(ProceedingJoinPoint joinPoint, Aware aware) throws Throwable {
        return this.around(joinPoint, aware);
    }

    protected Object around(ProceedingJoinPoint joinPoint, Aware aware) throws Throwable {
        if (!aware.enable()) {
            return joinPoint.proceed();
        }
        Perceptor perceptor = this.getPerceptor(aware);
        Map<String, Object> context = new LinkedHashMap<>();
        this.run(aware.async(), () -> perceptor.beforeProcess(joinPoint, context));
        context.put(START_TIME, LocalDateTime.now());
        try {
            Object result = joinPoint.proceed();
            context.put(END_TIME, LocalDateTime.now());
            this.run(aware.async(), () -> perceptor.afterReturningProcess(joinPoint, result, context));
            return result;
        } catch (Throwable e) {
            context.put(END_TIME, LocalDateTime.now());
            this.run(aware.async(), () -> perceptor.afterThrowingProcess(joinPoint, e, context));
            throw e;
        } finally {
            this.run(aware.async(), () -> perceptor.afterProcess(joinPoint, context));
        }
    }

    protected void run(boolean async, Runnable task) {
        if (async) {
            executor.execute(task);
        } else {
            task.run();
        }
    }

    protected Perceptor getPerceptor(Aware aware) {
        return Optional.ofNullable(aware.perceptor())
                .<Perceptor>map(clazz -> MemberUtil.getInstance(clazz, true))
                .orElse(DefaultPerceptor.INSTANCE);
    }
}
