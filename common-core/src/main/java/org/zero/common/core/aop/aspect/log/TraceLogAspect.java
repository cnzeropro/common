package org.zero.common.core.aop.aspect.log;

import cn.hutool.core.lang.Opt;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.zero.common.core.util.spring.context.SpringContextUtils;

import java.time.Duration;
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
public class TraceLogAspect implements InitializingBean {
    private ObjectMapper objectMapper;

    // @Around("org.zero.common.core.aop.pointcut.Pointcuts.allMethod()")
    @Around("@within(org.zero.common.core.aop.aspect.log.TraceLog) || " +
            "@annotation(org.zero.common.core.aop.aspect.log.TraceLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        // 因为切点表达式匹配的签名是方法，使用可以直接进行强制转换
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String signatureName = signature.getName();
        LogLevel logLevel = Opt.ofNullable(AnnotationUtils.findAnnotation(signature.getMethod(), TraceLog.class))
                .or(() -> Opt.ofNullable(AnnotationUtils.findAnnotation(signature.getDeclaringType(), TraceLog.class)))
                .map(TraceLog::value)
                .orElse(LogLevel.TRACE);

        long endTime = 0L;
        Object result;
        outputLog(logLevel, "Method[{}] starts executing, args: {}", null, signatureName, toExpectedStr(toMap(signature.getParameterNames(), args)));
        long startTime = System.nanoTime();
        try {
            result = joinPoint.proceed();
            endTime = System.nanoTime();
            outputLog(logLevel, "Method[{}] execution succeeded, result: {}", null, signatureName, toExpectedStr(result));
        } catch (Throwable e) {
            endTime = System.nanoTime();
            outputLog(logLevel, "Method[{}] execution exception", e, signatureName);
            throw e;
        } finally {
            outputLog(logLevel, "Method[{}] execution completes, time-consuming: {}", null, signatureName, Duration.ofNanos(endTime - startTime));
        }

        return result;
    }

    private Map<String, Object> toMap(String[] parameterNames, Object[] args) {
        int length = parameterNames.length;
        Map<String, Object> map = new LinkedHashMap<>((int) (length / MapUtil.DEFAULT_LOAD_FACTOR));
        for (int i = 0; i < length; i++) {
            String parameterName = parameterNames[i];
            map.put(parameterName, args[i]);
        }
        return map;
    }

    private String toExpectedStr(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception ignored) {
            // ignored
        }

        return StrUtil.utf8Str(obj);
    }

    private void outputLog(LogLevel logLevel, String msg, Throwable throwable, Object... args) {
        switch (logLevel) {
            case NONE:
                break;
            case TRACE:
                if (log.isTraceEnabled()) {
                    if (Objects.isNull(throwable)) {
                        log.trace(StrFormatter.format(msg, args));
                    } else {
                        log.trace(StrFormatter.format(msg, args), throwable);
                    }
                }
                break;
            case DEBUG:
                if (log.isDebugEnabled()) {
                    if (Objects.isNull(throwable)) {
                        log.debug(StrFormatter.format(msg, args));
                    } else {
                        log.debug(StrFormatter.format(msg, args), throwable);
                    }
                }
                break;
            case INFO:
                if (log.isInfoEnabled()) {
                    if (Objects.isNull(throwable)) {
                        log.info(StrFormatter.format(msg, args));
                    } else {
                        log.info(StrFormatter.format(msg, args), throwable);
                    }
                }
                break;
            case WARN:
                if (log.isWarnEnabled()) {
                    if (Objects.isNull(throwable)) {
                        log.warn(StrFormatter.format(msg, args));
                    } else {
                        log.warn(StrFormatter.format(msg, args), throwable);
                    }
                }
                break;
            case ERROR:
                if (log.isErrorEnabled()) {
                    if (Objects.isNull(throwable)) {
                        log.error(StrFormatter.format(msg, args));
                    } else {
                        log.error(StrFormatter.format(msg, args), throwable);
                    }
                }
                break;
            default:
        }
    }

    @Override
    public void afterPropertiesSet() {
        // 从容器中的对象copy而来，因为要进行配置调整，避免影响到全局
        objectMapper = SpringContextUtils.getBean(ObjectMapper.class).copy();
        // 序列化对象的所有属性，包括为Null的属性
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // 关闭 序列化时间日期为时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 关闭 空对象报错（对应属性没有get方法）
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }
}
