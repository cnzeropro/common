package org.zero.common.core.util.spring.expression;

import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;
import org.zero.common.core.support.context.spring.SpringUtils;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author zero
 * @see <a href="https://docs.spring.io/spring-framework/reference/core/expressions.html">SpEL</a>
 * @since 2023/8/25
 */
@UtilityClass
public class SpELUtil {
    public static final ExpressionParser expressionParser = new SpelExpressionParser();
    public static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

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
     * 构造默认的计算上下文，支持 Spring 上下文中的所有 bean 都可作为预定义变量使用
     * <p>
     * 如：systemProperties、systemEnvironment、 environment 等等
     *
     * @see org.springframework.core.env.StandardEnvironment
     */
    public EvaluationContext createDefaultContext() {
        BeanExpressionContext beanExpressionContext = new BeanExpressionContext(SpringUtils.getConfigurableListableBeanFactory(), null);
        return new StandardEvaluationContext(beanExpressionContext);
    }

    /**
     * 通过方法信息和参数构造计算上下文
     */
    public EvaluationContext createContext(Method method, Object[] args) {
        return addToContext(new StandardEvaluationContext(), method, args);
    }

    /**
     * 将方法信息和参数添加到计算上下文
     */
    public EvaluationContext addToContext(EvaluationContext context, Method method, Object[] args) {
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        return addToContext(context, parameterNames, args);
    }

    /**
     * 通过参数名和参数构造计算上下文
     */
    public EvaluationContext createContext(String[] paramNames, Object[] params) {
        return addToContext(new StandardEvaluationContext(), paramNames, params);
    }

    /**
     * 将参数名和参数添加到计算上下文
     */
    public EvaluationContext addToContext(EvaluationContext context, String[] paramNames, Object[] params) {
        if (ObjectUtils.isEmpty(paramNames) || ObjectUtils.isEmpty(params)) {
            return context;
        }
        for (int i = 0; i < paramNames.length && i < params.length; i++) {
            context.setVariable(paramNames[i], params[i]);
        }
        return context;
    }

    /**
     * 通过 {@link Map} 构造计算上下文
     */
    public EvaluationContext createContext(Map<String, Object> map) {
        return addToContext(new StandardEvaluationContext(), map);
    }

    /**
     * 将 {@link Map} 添加到计算上下文
     */
    public EvaluationContext addToContext(EvaluationContext context, Map<String, Object> map) {
        if (!ObjectUtils.isEmpty(map)) {
            map.forEach(context::setVariable);
        }
        return context;
    }
}
