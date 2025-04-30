package org.zero.common.core.util.spring.expression;

import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.PropertySource;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;
import org.zero.common.core.support.context.spring.SpringUtils;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author zero
 * @since 2023/8/25
 */
@UtilityClass
public class SpELUtil {
    private static final ExpressionParser expressionParser = new SpelExpressionParser();
    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 获取 SpEL 值
     * <p>
     * 无需上下文的 SpEL 求值，例如："#{1+1}"、"#{'黑色'}" 等等
     */
    public <T> T evaluate(String expressionString, Class<T> desiredResultType) {
        return evaluate(expressionString, null, desiredResultType);
    }

    /**
     * 获取 SpEL 值
     * <p>
     * 无需上下文的 SpEL 求值，例如："#{1+1}"、"#{'黑色'}" 等等
     */
    public <T> T evaluate(String expressionString, Object rootObject, Class<T> desiredResultType) {
        return expressionParser.parseExpression(expressionString)
                .getValue(rootObject, desiredResultType);
    }

    /**
     * 获取 SpEL 值
     * <p>
     * 需要提供上下文的 SpEL 求值，例如："#user.isAdmin()"、"#user.name" 等等
     */
    public <T> T evaluate(String expressionString, EvaluationContext context, Class<T> desiredResultType) {
        return evaluate(expressionString, context, null, desiredResultType);
    }

    /**
     * 获取 SpEL 值
     * <p>
     * 需要提供上下文的 SpEL 求值，例如："#user.isAdmin()"、"#user.name" 等等
     */
    public <T> T evaluate(String expressionString, EvaluationContext context, Object rootObject, Class<T> desiredResultType) {
        return expressionParser.parseExpression(expressionString)
                .getValue(context, rootObject, desiredResultType);
    }

    /**
     * 构造默认的计算上下文，与原生spring的上下文不同
     * <p>
     * 系统环境变量默认名称为：systemEnv；
     * 系统属性默认名称为：systemProperties；
     * Spring 环境变量默认名称为：springEnv
     */
    public EvaluationContext createDefaultContext() {
        EvaluationContext context = new StandardEvaluationContext();
        // 获取属性源
        ObjectProvider<PropertySource<?>> propertySourceObjectProvider = SpringUtils.getBeanProvider(ResolvableType.forClass(PropertySource.class));
        propertySourceObjectProvider.stream().forEach(ps -> context.setVariable(ps.getName(), ps.getSource()));
        context.setVariable("systemEnv", System.getenv());
        context.setVariable("systemProperties", System.getProperties());
        context.setVariable("springEnv", SpringUtils.getEnvironment());
        return context;
    }

    /**
     * 通过方法信息和参数构造计算上下文
     */
    public EvaluationContext createContext(Method method, Object[] args) {
        return add2Context(new StandardEvaluationContext(), method, args);
    }

    /**
     * 将方法信息和参数添加到计算上下文
     */
    public EvaluationContext add2Context(EvaluationContext context, Method method, Object[] args) {
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        return add2Context(context, parameterNames, args);
    }

    /**
     * 通过参数名和参数构造计算上下文
     */
    public EvaluationContext createContext(String[] paramNames, Object[] params) {
        return add2Context(new StandardEvaluationContext(), paramNames, params);
    }

    /**
     * 将参数名和参数添加到计算上下文
     */
    public EvaluationContext add2Context(@Nonnull EvaluationContext context, String[] paramNames, Object[] params) {
        if (ObjectUtils.isEmpty(paramNames) || ObjectUtils.isEmpty(params)) {
            return context;
        }
        for (int i = 0; i < paramNames.length && i < params.length; i++) {
            context.setVariable(paramNames[i], params[i]);
        }
        return context;
    }

    /**
     * 通过Map构造计算上下文
     */
    public EvaluationContext createContext(Map<String, Object> map) {
        return add2Context(new StandardEvaluationContext(), map);
    }

    /**
     * 将Map添加到计算上下文
     */
    public EvaluationContext add2Context(@Nonnull EvaluationContext context, Map<String, Object> map) {
        if (!ObjectUtils.isEmpty(map)) {
            map.forEach(context::setVariable);
        }
        return context;
    }
}
