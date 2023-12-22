package org.zero.common.core.util.spring;

import lombok.experimental.UtilityClass;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;

/**
 * @author zero
 * @since 2023/8/25
 */
@UtilityClass
public class SpELUtil {
    private static final SpelExpressionParser expressionParser = new SpelExpressionParser();
    private static final LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    /**
     * 获取SpEL值
     */
    public <T> T evaluate(Method method, Object[] args, String key, Class<T> clazz) {
        return evaluate(getContext(method, args), key, clazz);
    }

    /**
     * 获取SpEL值
     */
    public <T> T evaluate(EvaluationContext context, String key, Class<T> clazz) {
        Expression expression = expressionParser.parseExpression(key);
        return expression.getValue(context, clazz);
    }

    /**
     * 获取参数容器
     */
    public EvaluationContext getContext(Method method, Object[] args) {
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        EvaluationContext context = new StandardEvaluationContext();
        if (ObjectUtils.isEmpty(parameterNames)) {
            return context;
        }
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        return context;
    }
}
