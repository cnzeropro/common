package org.zero.common.core.aop.aspectj.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记需要由原生 AspectJ 统计执行耗时的方法。
 * <p>
 * 该注解只声明切点语义，不会直接触发 Spring AOP 代理。
 * 下游项目需要通过 LTW 或 CTW 织入
 * {@link org.zero.common.core.aop.aspectj.aspect.LoggableAspect} 后才会生效。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/30
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
}
