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
    public void publicMethod() {
    }

    @Pointcut("execution(* set*(..))")
    public void setMethod() {
    }

    @Pointcut("execution(* get*(..))")
    public void getMethod() {
    }

    @Pointcut("execution(* *.controller..*.*(..))")
    public void controllerMethod() {
    }

    @Pointcut("execution(* *.service..*.*(..))")
    public void serviceMethod() {
    }

    @Pointcut("execution(* *.mapper..*.*(..))")
    public void mapperMethod() {
    }
}
