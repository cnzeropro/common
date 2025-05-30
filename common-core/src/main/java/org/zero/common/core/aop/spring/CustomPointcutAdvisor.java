package org.zero.common.core.aop.spring;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;

/**
 * @author Zero (cnzeropro@163.com)
 * @see org.springframework.aop.framework.ProxyFactory
 * @since 2025/5/28
 */
public class CustomPointcutAdvisor extends DefaultPointcutAdvisor {
    public CustomPointcutAdvisor(Pointcut pointcut, Advice advice) {
        setPointcut(pointcut);
        setAdvice(advice);
    }
}
