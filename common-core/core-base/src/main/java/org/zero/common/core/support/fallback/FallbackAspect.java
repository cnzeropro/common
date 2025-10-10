package org.zero.common.core.support.fallback;

import lombok.Data;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.zero.common.core.extension.spring.aop.BaseAspect;
import org.zero.common.core.util.java.lang.ArrayUtil;
import org.zero.common.core.util.java.lang.ClassUtil;
import org.zero.common.core.util.java.lang.reflect.ExecutableUtil;
import org.zero.common.core.util.java.lang.reflect.MemberUtil;
import org.zero.common.core.util.java.lang.reflect.MethodUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/26
 */
@Aspect
public class FallbackAspect extends BaseAspect {
    @Around("@annotation(fallback)")
    public Object aroundMethodAnnotation(ProceedingJoinPoint joinPoint, Fallback fallback) throws Throwable {
        return this.around(joinPoint, fallback);
    }

    @Around("@within(fallback)")
    public Object aroundClassAnnotation(ProceedingJoinPoint joinPoint, Fallback fallback) throws Throwable {
        return this.around(joinPoint, fallback);
    }

    protected Object around(ProceedingJoinPoint joinPoint, Fallback fallback) throws Throwable {
        if (!fallback.enable()) {
            return joinPoint.proceed();
        }
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            return fallback(joinPoint, fallback, throwable);
        }
    }

    protected Object fallback(ProceedingJoinPoint joinPoint, Fallback fallback, Throwable throwable) throws Throwable {
        Class<?> targetClass = fallback.targetClass();
        boolean useJoinPointTarget = false;
        Object joinPointTarget = joinPoint.getTarget();
        if (targetClass == void.class) {
            targetClass = joinPointTarget.getClass();
            useJoinPointTarget = true;
        }
        Signature signature = joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        Class<?>[] signatureParameterTypes;
        Class<?> signatureReturnType = null;
        Method signatureMethod = null;
        if (signature instanceof CodeSignature) {
            CodeSignature codeSignature = (CodeSignature) signature;
            signatureParameterTypes = codeSignature.getParameterTypes();
            if (signature instanceof MethodSignature) {
                MethodSignature methodSignature = (MethodSignature) signature;
                signatureReturnType = methodSignature.getReturnType();
                signatureMethod = methodSignature.getMethod();
            }
        } else {
            signatureParameterTypes = ExecutableUtil.getParameterTypes(args);
        }

        // 确保 AliasFor 生效
        String targetMethodName = AnnotationUtils.synthesizeAnnotation(fallback, null).targetMethod();
        if (!StringUtils.hasText(targetMethodName) && Objects.nonNull(signatureMethod)) {
            targetMethodName = signatureMethod.getName();
        }
        Optional<FallbackMethod> fallbackMethodOpt = this.getFallbackMethod(targetClass, targetMethodName, signatureMethod, signatureReturnType, signatureParameterTypes, args, throwable);
        if (fallbackMethodOpt.isPresent()) {
            FallbackMethod fallbackMethod = fallbackMethodOpt.get();
            return this.invokeFallbackMethod(fallbackMethod, targetClass, joinPointTarget, useJoinPointTarget);
        }
        // 回滚方法不存在时，重新抛出原始异常
        throw throwable;
    }

    protected Optional<FallbackMethod> getFallbackMethod(Class<?> clazz, String targetMethodName,
                                                         Method signatureMethod, Class<?> signatureReturnType, Class<?>[] signatureParameterTypes, Object[] args,
                                                         Throwable throwable) {
        return MethodUtil.listByName(clazz, false, targetMethodName)
                .stream()
                // 排除签名（切点）方法
                .filter(method -> !Objects.equals(method, signatureMethod))
                .filter(method -> {
                    if (Objects.nonNull(signatureReturnType)) {
                        return ClassUtil.isAssignable(method.getReturnType(), signatureReturnType);
                    }
                    return true;
                })
                .map(method -> this.toFallbackMethod(method, signatureParameterTypes, args, throwable))
                .min(Comparator.comparingInt(FallbackMethod::getPriority));
    }

    protected FallbackMethod toFallbackMethod(Method method, Class<?>[] paramTypes, Object[] params, Throwable throwable) {
        int priority = 0;
        Class<?>[] originalParamTypes = ArrayUtil.append(paramTypes, throwable.getClass());
        Object[] originalParams = ArrayUtil.append(params, throwable);
        int originalParamTypesLength = originalParamTypes.length;
        int originalParamsLength = originalParams.length;
        for (int i = 0; i < originalParamTypesLength && i < originalParamsLength; i++, priority++) {
            Class<?>[] modifiedParamTypes = ArrayUtil.sub(originalParamTypes, 0, originalParamTypesLength - i);
            Object[] modifiedParams = ArrayUtil.sub(originalParams, 0, originalParamsLength - i);
            int priorityIncrement = this.calculatePriorityIncrement(method, modifiedParamTypes);
            if (priorityIncrement > 0) {
                return new FallbackMethod(priority + priorityIncrement, method, modifiedParams);
            }
            priority += 2;

            modifiedParamTypes = ArrayUtil.sub(originalParamTypes, i, originalParamTypesLength);
            modifiedParams = ArrayUtil.sub(originalParams, i, originalParamsLength);
            priorityIncrement = this.calculatePriorityIncrement(method, modifiedParamTypes);
            if (priorityIncrement > 0) {
                return new FallbackMethod(priority + priorityIncrement, method, modifiedParams);
            }
            priority += 2;

            // 去除 Throwable 参数再次尝试
            if (i < originalParamTypesLength - 1) {
                modifiedParamTypes = ArrayUtil.sub(originalParamTypes, i, originalParamTypesLength - 1);
                modifiedParams = ArrayUtil.sub(originalParams, i, originalParamsLength - 1);
                priorityIncrement = this.calculatePriorityIncrement(method, modifiedParamTypes);
                if (priorityIncrement > 0) {
                    return new FallbackMethod(priority + priorityIncrement, method, modifiedParams);
                }
            }
            priority += 2;
        }
        // 默认
        Object[] defaultValueParams = Arrays.stream(method.getParameterTypes())
                .map(ClassUtil::getDefaultValue)
                .toArray();
        return new FallbackMethod(Integer.MAX_VALUE, method, defaultValueParams);
    }

    protected Object invokeFallbackMethod(FallbackMethod fallbackMethod, Class<?> targetClass,
                                          Object joinPointTarget, boolean useJoinPointTarget) {
        Method method = fallbackMethod.getMethod();
        Object[] params = fallbackMethod.getParams();
        if (MemberUtil.isStatic(method)) {
            return MethodUtil.invokeStatic(method, false, params);
        }
        if (useJoinPointTarget) {
            return MethodUtil.invoke(method, joinPointTarget, false, params);
        }
        Object target = null;
        // 尝试获取从 Spring 容器中获取实例
        try {
            target = applicationContext.getBean(targetClass);
            if (AopUtils.isAopProxy(target)) {
                target = AopProxyUtils.getSingletonTarget(target);
            }
        } catch (Exception ignored) {
        }
        // 尝试获取实例化对象
        if (Objects.isNull(target)) {
            target = MemberUtil.getInstance(targetClass, false);
        }
        return MethodUtil.invoke(method, target, false, params);
    }

    protected int calculatePriorityIncrement(Method method, Class<?>[] paramTypes) {
        if (ExecutableUtil.ExactParameterTypePredicate.of(paramTypes).test(method)) {
            return 1;
        }
        if (ExecutableUtil.AvailableParameterTypePredicate.of(paramTypes).test(method)) {
            return 2;
        }
        return 0;
    }

    @Data
    protected static class FallbackMethod {
        private final int priority;
        private final Method method;
        private final Object[] params;
    }
}
