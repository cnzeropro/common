package org.zero.common.core.aop.spring;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;

/**
 * 简化 {@link DefaultPointcutAdvisor} 的构造式封装。
 * <p>
 * 适合在手动注册 advisor 时一次性绑定 {@link Pointcut} 与 {@link Advice}，
 * 避免先创建实例再分别调用 setter。
 *
 * @author Zero (cnzeropro@163.com)
 * @see org.springframework.aop.framework.ProxyFactory
 * @since 2025/5/28
 */
public class CustomPointcutAdvisor extends DefaultPointcutAdvisor {
	/**
	 * 使用给定的切点与增强创建 advisor。
	 *
	 * @param pointcut 切点定义
	 * @param advice   增强逻辑
	 */
    public CustomPointcutAdvisor(Pointcut pointcut, Advice advice) {
        this.setPointcut(pointcut);
        this.setAdvice(advice);
    }
}
