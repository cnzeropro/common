package org.zero.common.core.aop.pointcut;

import org.aspectj.lang.annotation.Pointcut;

/**
 * @author zero
 * @since 2021/9/13
 */
public class Pointcuts {
    @Pointcut("execution(* *(..))")
    public void allMethod() {
    }

    @Pointcut("execution(public * *(..))")
    public void allPublicMethod() {
    }

    @Pointcut("execution(* set*(..))")
    public void allSetMethod() {
    }

    @Pointcut("execution(* get*(..))")
    public void allGetMethod() {
    }

    @Pointcut("execution(* org.zero..*.controller..*.*(..))")
    public void controllerMethod() {
    }

    @Pointcut("execution(* org.zero..*.service..*.*(..))")
    public void serviceMethod() {
    }
}
