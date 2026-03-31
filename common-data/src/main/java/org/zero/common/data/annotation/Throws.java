package org.zero.common.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link Throw} 的容器注解，用于支持 {@link java.lang.annotation.Repeatable @Repeatable}。
 * <p>
 * 仅作为编译期/文档期声明：表达多个“可能抛出的异常类型”。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/13
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Documented
public @interface Throws {
    /**
     * 异常声明列表。
     */
    Throw[] value();
}
