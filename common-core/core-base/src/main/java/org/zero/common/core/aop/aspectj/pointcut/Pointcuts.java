package org.zero.common.core.aop.aspectj.pointcut;

import org.aspectj.lang.annotation.Pointcut;

/**
 * AspectJ 通用切点定义集合。
 * <p>
 * 该类仅保留与 Spring 容器无关的可移植切点，适合在纯 AspectJ
 * 或 Spring AOP 场景中复用。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2021/9/13
 */
public class Pointcuts {
	/**
	 * 匹配任意方法执行。
	 * <p>
	 * 适合需要覆盖全部方法的基础切面。
	 */
    @Pointcut("execution(* *(..))")
    public void allMethod() {
    }

	/**
	 * 匹配任意 public 方法执行。
	 * <p>
	 * 适合仅关注对外暴露方法的切面。
	 */
    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
    }

	/**
	 * 匹配 Java Bean 风格的 setter 方法。
	 */
    @Pointcut("execution(* set*(..))")
    public void setMethod() {
    }

	/**
	 * 匹配 Java Bean 风格的 getter 方法。
	 */
    @Pointcut("execution(* get*(..))")
    public void getMethod() {
    }

	/**
	 * 匹配 Java Bean 风格的 boolean getter 方法。
	 */
	@Pointcut("execution(* is*())")
	public void isMethod() {
	}

	/**
	 * 匹配常见访问器方法。
	 * <p>
	 * 该组合切点聚合了 getter、boolean getter 与 setter 三类方法。
	 */
	@Pointcut("getMethod() || isMethod() || setMethod()")
	public void accessorMethod() {
	}

	/**
	 * 匹配位于 {@code ..controller..} 包层级中的方法。
	 * <p>
	 * 适合基于包结构约定的 Web 层切面。
	 */
	@Pointcut("execution(* *..controller..*.*(..))")
    public void controllerMethod() {
    }

	/**
	 * 匹配位于 {@code ..service..} 包层级中的方法。
	 * <p>
	 * 适合基于包结构约定的业务层切面。
	 */
	@Pointcut("execution(* *..service..*.*(..))")
    public void serviceMethod() {
    }

	/**
	 * 匹配位于 {@code ..mapper..} 包层级中的方法。
	 * <p>
	 * 适合基于包结构约定的数据访问层切面。
	 */
	@Pointcut("execution(* *..mapper..*.*(..))")
    public void mapperMethod() {
    }

	/**
	 * 匹配位于 {@code ..repository..} 包层级中的方法。
	 * <p>
	 * 适合基于包结构约定的 Repository 层切面。
	 */
	@Pointcut("execution(* *..repository..*.*(..))")
	public void repositoryMethod() {
	}

	/**
	 * 匹配位于 {@code ..dao..} 包层级中的方法。
	 * <p>
	 * 适合基于包结构约定的 DAO 层切面。
	 */
	@Pointcut("execution(* *..dao..*.*(..))")
	public void daoMethod() {
	}
}
