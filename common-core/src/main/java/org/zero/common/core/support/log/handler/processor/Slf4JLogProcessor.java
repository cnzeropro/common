package org.zero.common.core.support.log.handler.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.util.ObjectUtils;
import org.zero.common.core.support.context.spring.SpringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.zero.common.core.support.log.handler.aspect.LogHandlerAspect.END_TIME;
import static org.zero.common.core.support.log.handler.aspect.LogHandlerAspect.START_TIME;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/15
 */
@Slf4j
public class Slf4JLogProcessor extends DefaultLogProcessor {
    public static final Slf4JLogProcessor INSTANCE = new Slf4JLogProcessor();

    @Override
    public void processBefore(JoinPoint joinPoint, Map<String, Object> context) {
        if (log.isTraceEnabled()) {
            Object[] args = joinPoint.getArgs();
            Signature signature = joinPoint.getSignature();
            String signatureName = signature.getName();
            if (signature instanceof CodeSignature) {
                Map<String, Object> parameterMap = this.toMap(((CodeSignature) signature).getParameterNames(), args);
                log.trace("Method[{}] begins to execute, args: {}", signatureName, this.toExpectedStr(parameterMap));
            } else {
                log.trace("Method[{}] begins to execute, args: {}", signatureName, this.toExpectedStr(args));
            }
        }
    }

    @Override
    public void processAfterReturning(JoinPoint joinPoint, Object result, Map<String, Object> context) {
        if (log.isTraceEnabled()) {
            log.trace("Method[{}] was executed successfully, result: {}", joinPoint.getSignature().getName(), this.toExpectedStr(result));
        }
    }

    @Override
    public void processAfterThrowing(JoinPoint joinPoint, Throwable throwable, Map<String, Object> context) {
        if (log.isTraceEnabled()) {
            log.trace(String.format("Method[%s] execution exception", joinPoint.getSignature().getName()), throwable);
        }
    }

    @Override
    public void processAfter(JoinPoint joinPoint, Map<String, Object> context) {
        if (log.isTraceEnabled()) {
            LocalDateTime startTime = Optional.ofNullable(context.get(START_TIME))
                    .filter(LocalDateTime.class::isInstance)
                    .map(LocalDateTime.class::cast)
                    .orElse(null);
            LocalDateTime endTime = Optional.ofNullable(context.get(END_TIME))
                    .filter(LocalDateTime.class::isInstance)
                    .map(LocalDateTime.class::cast)
                    .orElse(null);
            Duration duration;
            if (Objects.isNull(startTime) || Objects.isNull(endTime)) {
                duration = Duration.ZERO;
            } else {
                duration = Duration.between(startTime, endTime);
            }
            log.trace("Method[{}] execution completed, time consumption: {}", joinPoint.getSignature().getName(), duration);
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
            SpringUtils.getBean(ObjectMapper.class)
                    .writeValueAsString(obj);
        } catch (Exception ignored) {
            // ignored exception
        }
        if (ObjectUtils.isArray(obj)) {
            Object[] array = ObjectUtils.toObjectArray(obj);
            return Arrays.toString(array);
        }
        return Objects.toString(obj);
    }
}
