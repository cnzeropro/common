package org.zero.common.core.aop.spring.pointcut;

import org.aspectj.lang.annotation.Pointcut;

/**
 * Spring 专属切点定义集合。
 * <p>
 * 该类承载与 Spring stereotype、ControllerAdvice 等注解相关的切点，
 * 并通过组合方式复用
 * {@link org.zero.common.core.aop.aspectj.pointcut.Pointcuts} 中的通用切点。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2021/9/13
 */
public class Pointcuts {
	/**
	 * 匹配标注 {@code @Controller} 或 {@code @RestController} 的类型中的方法。
	 * <p>
	 * 适合基于 Spring Web stereotype 的控制层切面。
	 */
	@Pointcut("@within(org.springframework.stereotype.Controller) || " +
			"@within(org.springframework.web.bind.annotation.RestController)")
	public void controllerAnnotationMethod() {
	}

	/**
	 * 匹配标注 {@code @Service} 的类型中的方法。
	 * <p>
	 * 适合基于 Spring stereotype 的业务层切面。
	 */
	@Pointcut("@within(org.springframework.stereotype.Service)")
	public void serviceAnnotationMethod() {
	}

	/**
	 * 匹配标注 {@code @Repository} 的类型中的方法。
	 * <p>
	 * 适合基于 Spring stereotype 的数据访问层切面。
	 */
	@Pointcut("@within(org.springframework.stereotype.Repository)")
	public void repositoryAnnotationMethod() {
	}

	/**
	 * 匹配标注 {@code @ControllerAdvice} 或 {@code @RestControllerAdvice} 的类型中的方法。
	 * <p>
	 * 适合控制层全局通知相关切面。
	 */
	@Pointcut("@within(org.springframework.web.bind.annotation.ControllerAdvice) || " +
			"@within(org.springframework.web.bind.annotation.RestControllerAdvice)")
	public void controllerAdviceMethod() {
	}

	/**
	 * 匹配 Spring Web 层方法。
	 * <p>
	 * 该组合切点复用了 AspectJ 包结构切点与当前类中的 Controller/Advice 注解切点。
	 */
	@Pointcut("org.zero.common.core.aop.aspectj.pointcut.Pointcuts.controllerMethod() || " +
			"controllerAnnotationMethod() || controllerAdviceMethod()")
	public void webMethod() {
	}

	/**
	 * 匹配 Spring 业务层方法。
	 * <p>
	 * 该组合切点复用了 AspectJ 包结构切点与当前类中的 {@code @Service} 注解切点。
	 */
	@Pointcut("org.zero.common.core.aop.aspectj.pointcut.Pointcuts.serviceMethod() || serviceAnnotationMethod()")
	public void businessServiceMethod() {
	}

	/**
	 * 匹配 Spring 数据访问层方法。
	 * <p>
	 * 该组合切点复用了 AspectJ 包结构切点与当前类中的 {@code @Repository} 注解切点。
	 */
	@Pointcut("org.zero.common.core.aop.aspectj.pointcut.Pointcuts.mapperMethod() || " +
			"org.zero.common.core.aop.aspectj.pointcut.Pointcuts.repositoryMethod() || " +
			"org.zero.common.core.aop.aspectj.pointcut.Pointcuts.daoMethod() || " +
			"repositoryAnnotationMethod()")
	public void dataAccessMethod() {
	}

	/**
	 * 匹配 public 的 Spring Web 层方法。
	 * <p>
	 * 该组合切点在 {@link #webMethod()} 基础上叠加了 AspectJ 的 public 方法约束。
	 */
	@Pointcut("org.zero.common.core.aop.aspectj.pointcut.Pointcuts.publicMethod() && webMethod()")
	public void publicWebMethod() {
	}

	/**
	 * 匹配 public 的 Spring 业务层方法。
	 * <p>
	 * 该组合切点在 {@link #businessServiceMethod()} 基础上叠加了 AspectJ 的 public 方法约束。
	 */
	@Pointcut("org.zero.common.core.aop.aspectj.pointcut.Pointcuts.publicMethod() && businessServiceMethod()")
	public void publicBusinessServiceMethod() {
	}

	/**
	 * 匹配 public 的 Spring 数据访问层方法。
	 * <p>
	 * 该组合切点在 {@link #dataAccessMethod()} 基础上叠加了 AspectJ 的 public 方法约束。
	 */
	@Pointcut("org.zero.common.core.aop.aspectj.pointcut.Pointcuts.publicMethod() && dataAccessMethod()")
	public void publicDataAccessMethod() {
	}
}
