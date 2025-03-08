package org.zero.common.core.aop.aspect.log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author zero
 * @since 2022/1/3
 */
@Slf4j
@Aspect
@Order(0)
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class LogTrackerAspect implements InitializingBean, BeanFactoryAware {
    private BeanFactory beanFactory;
    private ObjectMapper objectMapper;

    // @Around("org.zero.common.core.aop.pointcut.Pointcuts.allMethod()")
    @Around("@within(logTracker) || " +
            "@annotation(logTracker)")
    public Object around(ProceedingJoinPoint joinPoint, LogTracker logTracker) throws Throwable {
        if (!logTracker.enable()) {
            return joinPoint.proceed();
        }
        Object[] args = joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();
        // 只处理方法签名，不是方法的签名直接跳过
        if (!(signature instanceof MethodSignature)) {
            return joinPoint.proceed();
        }
        String signatureName = signature.getName();
        MethodSignature methodSignature = (MethodSignature) signature;
        // 确保 @AliasFor 注解生效
        LogLevel logLevel = AnnotationUtils.synthesizeAnnotation(logTracker, null).level();
        long endTime = 0L;
        Object result;
        this.outputLog(logLevel, "Method[%s] begins to execute, args: %s", null, signatureName, this.toExpectedStr(this.toMap(methodSignature.getParameterNames(), args)));
        long startTime = System.nanoTime();
        try {
            result = joinPoint.proceed();
            endTime = System.nanoTime();
            this.outputLog(logLevel, "Method[%s] was executed successfully, result: %s", null, signatureName, this.toExpectedStr(result));
        } catch (Throwable e) {
            endTime = System.nanoTime();
            this.outputLog(logLevel, "Method[%s] execution exception", e, signatureName);
            throw e;
        } finally {
            this.outputLog(logLevel, "Method[%s] execution completed, time consumption: %s", null, signatureName, Duration.ofNanos(endTime - startTime));
        }

        return result;
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
        if (obj instanceof String) {
            return (String) obj;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception ignored) {
            // ignored exception
        }
        Class<?> clazz = obj.getClass();
        if (clazz.isArray()) {
            Object[] array = (Object[]) obj;
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
