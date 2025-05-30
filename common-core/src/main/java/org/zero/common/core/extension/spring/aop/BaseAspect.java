package org.zero.common.core.extension.spring.aop;

import lombok.Data;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/28
 */
public abstract class BaseAspect implements Ordered, InitializingBean, DisposableBean, ApplicationContextAware {
    public static final int DEFAULT_ORDER = 0;
    protected ApplicationContext applicationContext;

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        // do nothing
    }

    @Override
    public void destroy() {
        // do nothing
    }

    protected Collection<Param> getParams(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();
        if (signature instanceof CodeSignature) {
            CodeSignature codeSignature = (CodeSignature) signature;
            Class<?>[] parameterTypes = codeSignature.getParameterTypes();
            String[] parameterNames = codeSignature.getParameterNames();
            if (signature instanceof MethodSignature) {
                MethodSignature methodSignature = (MethodSignature) signature;
                Method method = methodSignature.getMethod();
                Parameter[] parameters = method.getParameters();
                Type[] genericParameterTypes = method.getGenericParameterTypes();
                int length = min(args.length, parameterNames.length, parameters.length, genericParameterTypes.length);
                List<Param> params = new ArrayList<>(length);
                for (int i = 0; i < length; i++) {
                    params.add(new Param(i, parameters[i], genericParameterTypes[i], parameterNames[i], args[i]));
                }
                return params;
            }
            int length = min(args.length, parameterTypes.length, parameterNames.length);
            List<Param> params = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                params.add(new Param(i, null, parameterTypes[i], parameterNames[i], args[i]));
            }
            return params;
        }
        int length = args.length;
        List<Param> params = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            params.add(new Param(i, null, null, null, args[i]));
        }
        return params;
    }

    protected static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    protected Collection<Param> getParams(Method method, Object[] args) {
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        Parameter[] parameters = method.getParameters();
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        int length = min(args.length, parameterNames.length, parameters.length, genericParameterTypes.length);
        List<Param> params = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            Param param = new Param(i, parameters[i], genericParameterTypes[i], parameterNames[i], args[i]);
            params.add(param);
        }
        return params;
    }

    protected static int min(int... values) {
        return Arrays.stream(values)
                .sorted()
                .findFirst()
                .orElse(0);
    }

    @Data
    protected static class Param {
        private final int index;
        private final Parameter parameter;
        private final Type type;
        private final String name;
        private final Object value;
    }
}
