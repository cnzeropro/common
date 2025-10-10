package org.zero.common.core.util.spring.expression;

import lombok.experimental.UtilityClass;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.zero.common.core.support.context.spring.SpringUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * @author zero
 * @see <a href="https://docs.spring.io/spring-framework/reference/core/expressions.html">SpEL</a>
 * @since 2023/8/25
 */
@UtilityClass
public class SpELUtil {
	public static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();
	public static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

	/**
	 * 获取 SpEL 值
	 * <p>
	 * 无需上下文的 SpEL 求值，例如："#{1+1}"、"#{'黑色'}" 等等
	 */
	public static <T> T evaluate(String expressionString, Class<T> desiredResultType) {
		return parseExpression(expressionString).getValue(desiredResultType);
	}

	/**
	 * 获取 SpEL 值
	 * <p>
	 * 无需上下文的 SpEL 求值，例如："#{1+1}"、"#{'黑色'}" 等等
	 */
	public static <T> T evaluate(String expressionString, Object rootObject, Class<T> desiredResultType) {
		return parseExpression(expressionString).getValue(rootObject, desiredResultType);
	}

	/**
	 * 获取 SpEL 值
	 * <p>
	 * 需要提供上下文的 SpEL 求值，例如："#user.isAdmin()"、"#user.name" 等等
	 */
	public static <T> T evaluate(String expressionString, EvaluationContext context, Class<T> desiredResultType) {
		return parseExpression(expressionString).getValue(context, desiredResultType);
	}

	/**
	 * 获取 SpEL 值
	 * <p>
	 * 需要提供上下文的 SpEL 求值，例如："#user.isAdmin()"、"#user.name" 等等
	 */
	public static <T> T evaluate(String expressionString, EvaluationContext context, Object rootObject, Class<T> desiredResultType) {
		return parseExpression(expressionString).getValue(context, rootObject, desiredResultType);
	}

	public static Expression parseExpression(String expressionString) {
		return EXPRESSION_PARSER.parseExpression(expressionString);
	}

	public static Expression parseExpression(String expressionString, ParserContext context) {
		return EXPRESSION_PARSER.parseExpression(expressionString, context);
	}

	/**
	 * 通过 {@link Map} 构造计算上下文
	 */
	public static EvaluationContext createContext(Object root, Map<String, Object> map) {
		return addToContext(new StandardEvaluationContext(root), map);
	}

	/**
	 * 将 {@link Map} 添加到计算上下文
	 */
	public static EvaluationContext addToContext(EvaluationContext context, Map<String, Object> map) {
		if (!CollectionUtils.isEmpty(map)) {
			map.forEach(context::setVariable);
		}
		return context;
	}

	/**
	 * 构造默认的计算上下文，支持 Spring 上下文中的所有 bean 都可作为预定义变量使用
	 * <p>
	 * 如：systemProperties、systemEnvironment、 environment 等等
	 *
	 * @see org.springframework.core.env.StandardEnvironment
	 */
	public static EvaluationContext createDefaultContext() {
		StandardEvaluationContext context = new StandardEvaluationContext();
		context.setBeanResolver(new BeanFactoryResolver(SpringUtils.getBeanFactory()));
		return context;
	}

	/**
	 * 通过 {@link JoinPoint} 信息构造计算上下文
	 */
	public static EvaluationContext createContext(JoinPoint joinPoint) {
		Signature signature = joinPoint.getSignature();
		if (signature instanceof MethodSignature) {
			MethodSignature methodSignature = (MethodSignature) signature;
			return new MethodBasedEvaluationContext(joinPoint.getTarget(), methodSignature.getMethod(), joinPoint.getArgs(), PARAMETER_NAME_DISCOVERER);
		}
		return addToContext(new StandardEvaluationContext(), joinPoint);
	}


	/**
	 * 将 {@link JoinPoint} 信息添加到计算上下文
	 */
	public static EvaluationContext addToContext(EvaluationContext context, JoinPoint joinPoint) {
		Object target = joinPoint.getTarget();
		if (Objects.nonNull(target) &&
			TypedValue.NULL.equals(context.getRootObject()) &&
			context instanceof StandardEvaluationContext) {
			((StandardEvaluationContext) context).setRootObject(target);
		}
		Signature signature = joinPoint.getSignature();
		if (signature instanceof CodeSignature) {
			CodeSignature codeSignature = (CodeSignature) signature;
			String[] parameterNames = codeSignature.getParameterNames();
			Object[] args = joinPoint.getArgs();
			addToContext(context, parameterNames, args);
			return context;
		}
		return context;
	}

	/**
	 * 通过方法信息和参数构造计算上下文
	 */
	public static EvaluationContext createContext(Method method, Object[] args) {
		return createContext(null, method, args);
	}

	/**
	 * 通过方法信息和参数构造计算上下文
	 */
	public static EvaluationContext createContext(Object target, Method method, Object[] args) {
		return new MethodBasedEvaluationContext(target, method, args, PARAMETER_NAME_DISCOVERER);
	}

	/**
	 * 将方法信息和参数添加到计算上下文
	 */
	public static EvaluationContext addToContext(EvaluationContext context, Method method, Object[] args) {
		String[] parameterNames = PARAMETER_NAME_DISCOVERER.getParameterNames(method);
		return addToContext(context, parameterNames, args);
	}

	/**
	 * 将方法信息和参数添加到计算上下文
	 */
	public static EvaluationContext addToContext(EvaluationContext context, Object target, Method method, Object[] args) {
		String[] parameterNames = PARAMETER_NAME_DISCOVERER.getParameterNames(method);
		return addToContext(context, target, parameterNames, args);
	}

	/**
	 * 通过参数名和参数构造计算上下文
	 */
	public static EvaluationContext createContext(String[] paramNames, Object[] params) {
		return addToContext(new StandardEvaluationContext(), paramNames, params);
	}

	/**
	 * 通过参数名和参数构造计算上下文
	 */
	public static EvaluationContext createContext(Object target, String[] paramNames, Object[] params) {
		return addToContext(new StandardEvaluationContext(target), paramNames, params);
	}

	/**
	 * 将参数名和参数添加到计算上下文
	 */
	public static EvaluationContext addToContext(EvaluationContext context, String[] paramNames, Object[] params) {
		return addToContext(context, null, paramNames, params);
	}

	/**
	 * 将参数名和参数添加到计算上下文
	 */
	public static EvaluationContext addToContext(EvaluationContext context, Object target, String[] paramNames, Object[] params) {
		if (Objects.nonNull(target) &&
			TypedValue.NULL.equals(context.getRootObject()) &&
			context instanceof StandardEvaluationContext) {
			((StandardEvaluationContext) context).setRootObject(target);
		}
		if (ObjectUtils.isEmpty(paramNames) || ObjectUtils.isEmpty(params)) {
			return context;
		}
		for (int i = 0; i < paramNames.length && i < params.length; i++) {
			context.setVariable(paramNames[i], params[i]);
		}
		return context;
	}
}
