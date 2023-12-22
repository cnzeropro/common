package org.zero.common.core.aop.aspect.log;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
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
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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

    @Around("org.zero.common.core.aop.pointcut.Pointcuts.allMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        long endTime = 0L;
        Object result;
        outputLog("Method[{}] starts executing, args: {}", null, method, toExpectedStr(toMap(method.getParameters(), args)));
        long startTime = System.nanoTime();
        try {
            result = joinPoint.proceed();
            endTime = System.nanoTime();
            outputLog("Method[{}] execution succeeded, result: {}", null, method, toExpectedStr(result));
        } catch (Throwable e) {
            endTime = System.nanoTime();
            outputLog("Method[{}] execution exception", e, method);
            throw e;
        } finally {
            outputLog("Method[{}] execution completes, time-consuming: {}", null, method, Duration.ofNanos(endTime - startTime));
        }

        return result;
    }

    private Map<String, Object> toMap(Parameter[] parameters, Object[] args) {
        int length = parameters.length;
        Map<String, Object> map = new LinkedHashMap<>(length);
        for (int i = 0; i < length; i++) {
            Parameter parameter = parameters[i];
            map.put(parameter.getName(), args[i]);
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

    private void outputLog(String msg, Throwable throwable, Object... args) {
        if (log.isTraceEnabled()) {
            if (Objects.isNull(throwable)) {
                log.trace(StrFormatter.format(msg, args));
            } else {
                log.trace(StrFormatter.format(msg, args), throwable);
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        // 从容器中的对象copy而来，因为要进行配置调整，避免影响到全局
        objectMapper = SpringUtil.getBean(ObjectMapper.class).copy();
        // 序列化对象的所有属性，包括为Null的属性
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // 关闭 序列化时间日期为时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 关闭 空对象报错（对应属性没有get方法）
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }
}
