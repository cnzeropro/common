package org.zero.common.core.support.log.tracker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author zero
 * @see org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint
 * @since 2022/1/3
 */
@Slf4j
@Aspect
@Order(0)
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LogTrackerAspect implements InitializingBean, BeanFactoryAware {
    protected ThreadLocal<Long> startTimeContext = new ThreadLocal<>();
    protected BeanFactory beanFactory;
    protected ObjectMapper objectMapper;

    @Before("@annotation(logTracker)")
    public void beforeMethodAnnotation(JoinPoint joinPoint, LogTracker logTracker) {
        this.before(joinPoint, logTracker);
    }

    @Before("@within(logTracker)")
    public void beforeClassAnnotation(JoinPoint joinPoint, LogTracker logTracker) {
        this.before(joinPoint, logTracker);
    }

    protected void before(JoinPoint joinPoint, LogTracker logTracker) {
        try {
            if (!logTracker.enable()) {
                return;
            }
            // 确保 @AliasFor 注解生效
            LogLevel logLevel = AnnotationUtils.synthesizeAnnotation(logTracker, null).level();
            Object[] args = joinPoint.getArgs();
            Signature signature = joinPoint.getSignature();
            String signatureName = signature.getName();
            if (signature instanceof CodeSignature) {
                CodeSignature codeSignature = (CodeSignature) signature;
                this.outputLog(logLevel, "Method[%s] begins to execute, args: %s", null, signatureName, this.toExpectedStr(this.toMap(codeSignature.getParameterNames(), args)));
                return;
            }
            this.outputLog(logLevel, "Method[%s] begins to execute, args: %s", null, signatureName, this.toExpectedStr(args));
        } finally {
            startTimeContext.set(System.nanoTime());
        }
    }

    @AfterReturning(pointcut = "@annotation(logTracker)", returning = "result")
    public void afterReturningMethodAnnotation(JoinPoint joinPoint, LogTracker logTracker, Object result) {
        this.afterReturning(joinPoint, logTracker, result);
    }

    @AfterReturning(pointcut = "@within(logTracker)", returning = "result")
    public void afterReturningClassAnnotation(JoinPoint joinPoint, LogTracker logTracker, Object result) {
        this.afterReturning(joinPoint, logTracker, result);
    }

    protected void afterReturning(JoinPoint joinPoint, LogTracker logTracker, Object result) {
        if (!logTracker.enable()) {
            return;
        }
        // 确保 @AliasFor 注解生效
        LogLevel logLevel = AnnotationUtils.synthesizeAnnotation(logTracker, null).level();
        this.outputLog(logLevel, "Method[%s] was executed successfully, result: %s", null, joinPoint.getSignature().getName(), this.toExpectedStr(result));
    }

    @AfterThrowing(pointcut = "@annotation(logTracker)", throwing = "throwable")
    public void afterThrowingMethodAnnotation(JoinPoint joinPoint, LogTracker logTracker, Throwable throwable) {
        this.afterThrowing(joinPoint, logTracker, throwable);
    }

    @AfterThrowing(pointcut = "@within(logTracker)", throwing = "throwable")
    public void afterThrowingClassAnnotation(JoinPoint joinPoint, LogTracker logTracker, Throwable throwable) {
        this.afterThrowing(joinPoint, logTracker, throwable);
    }

    protected void afterThrowing(JoinPoint joinPoint, LogTracker logTracker, Throwable throwable) {
        if (!logTracker.enable()) {
            return;
        }
        // 确保 @AliasFor 注解生效
        LogLevel logLevel = AnnotationUtils.synthesizeAnnotation(logTracker, null).level();
        this.outputLog(logLevel, "Method[%s] execution exception", throwable, joinPoint.getSignature().getName());
    }

    @After("@annotation(logTracker)")
    public void afterMethodAnnotation(JoinPoint joinPoint, LogTracker logTracker) {
        this.after(joinPoint, logTracker);
    }

    @After("@within(logTracker)")
    public void afterClassAnnotation(JoinPoint joinPoint, LogTracker logTracker) {
        this.after(joinPoint, logTracker);
    }

    protected void after(JoinPoint joinPoint, LogTracker logTracker) {
        try {
            if (!logTracker.enable()) {
                return;
            }
            long endTime = System.nanoTime();
            Long startTime = startTimeContext.get();
            Duration duration = Duration.ZERO;
            if (Objects.nonNull(startTime)) {
                duration = Duration.ofNanos(endTime - startTime);
            }
            // 确保 @AliasFor 注解生效
            LogLevel logLevel = AnnotationUtils.synthesizeAnnotation(logTracker, null).level();
            this.outputLog(logLevel, "Method[%s] execution completed, time consumption: %s", null, joinPoint.getSignature().getName(), duration);
        } finally {
            startTimeContext.remove();
        }
    }

    private Map<String, Object> toMap(String[] parameterNames, Object[] args) {
        int length = parameterNames.length;
        Map<String, Object> map = new LinkedHashMap<>(length, 1.0F);
        for (int i = 0; i < length && i < args.length; i++) {
            String parameterName = parameterNames[i];
            map.put(parameterName, args[i]);
        }
        return map;
    }

    private String toExpectedStr(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).toString();
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception ignored) {
            // ignored exception
        }

        if (ObjectUtils.isArray(obj)) {
            Object[] array = ObjectUtils.toObjectArray(obj);
            return Arrays.toString(array);
        }
        return Objects.toString(obj);
    }

    private void outputLog(LogLevel logLevel, String msg, Throwable throwable, Object... args) {
        switch (logLevel) {
            case NONE:
                break;
            case TRACE:
                this.outputTraceLog(msg, throwable, args);
                break;
            case DEBUG:
                this.outputDebugLog(msg, throwable, args);
                break;
            case INFO:
                this.outputInfoLog(msg, throwable, args);
                break;
            case WARN:
                this.outputWarnLog(msg, throwable, args);
                break;
            case ERROR:
                this.outputErrorLog(msg, throwable, args);
                break;
            default:
        }
    }

    private void outputTraceLog(String msg, Throwable throwable, Object... args) {
        if (log.isTraceEnabled()) {
            if (Objects.isNull(throwable)) {
                log.trace(String.format(msg, args));
            } else {
                log.trace(String.format(msg, args), throwable);
            }
        }
    }

    private void outputDebugLog(String msg, Throwable throwable, Object... args) {
        if (log.isDebugEnabled()) {
            if (Objects.isNull(throwable)) {
                log.debug(String.format(msg, args));
            } else {
                log.debug(String.format(msg, args), throwable);
            }
        }
    }

    private void outputInfoLog(String msg, Throwable throwable, Object... args) {
        if (log.isInfoEnabled()) {
            if (Objects.isNull(throwable)) {
                log.info(String.format(msg, args));
            } else {
                log.info(String.format(msg, args), throwable);
            }
        }
    }

    private void outputWarnLog(String msg, Throwable throwable, Object... args) {
        if (log.isWarnEnabled()) {
            if (Objects.isNull(throwable)) {
                log.warn(String.format(msg, args));
            } else {
                log.warn(String.format(msg, args), throwable);
            }
        }
    }

    private void outputErrorLog(String msg, Throwable throwable, Object... args) {
        if (log.isErrorEnabled()) {
            if (Objects.isNull(throwable)) {
                log.error(String.format(msg, args));
            } else {
                log.error(String.format(msg, args), throwable);
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        // 从容器中的对象copy而来，因为要进行配置调整，避免影响到全局
        objectMapper = beanFactory.getBean(ObjectMapper.class).copy();
        // 序列化对象的所有属性，包括为 null 的属性
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // 关闭 序列化时间日期为时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 关闭 空对象报错（对应属性没有get方法）
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
