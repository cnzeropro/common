package org.zero.common.core.support.xss.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.zero.common.core.support.xss.annotation.XssHandler;
import org.zero.common.core.util.java.util.MapUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/17
 */
@Aspect
public class XssHandlerAspect {
    // protected static final Cache<Method, Parameter> cache = MapCache.of(() -> new ConcurrentLruCache<>(10000,));

    @Around("org.zero.common.core.aop.aspectj.pointcut.Pointcuts.allMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature)) {
            return joinPoint.proceed();
        }
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        Parameter[] parameters = method.getParameters();
        Map<Parameter, Object> parameterMap = MapUtil.of(true, parameters, args);

        parameterMap.forEach((parameter, arg)->{
            Class<?> type = parameter.getType();
        });


        List<Parameter> annotatedParameters = Arrays.stream(parameters)
                .filter(parameter -> parameter.isAnnotationPresent(XssHandler.class))
                .collect(Collectors.toList());
        XssHandler xssHandler = AnnotatedElementUtils.findMergedAnnotation(method, XssHandler.class);
        if (Objects.nonNull(xssHandler)) {
            for (Parameter parameter : parameters) {
            }
        }

        return null;
    }
}
