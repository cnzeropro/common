package org.zero.common.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示可能抛出的异常（声明/文档用途）。
 * <p>
 * 可用于包、类型、方法或构造器上，表达“调用方需要关注的异常类型”。该注解不会改变 Java 的 throws 语义，
 * 主要用于源码阅读、生成文档或被工具扫描。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/13
 */
@Repeatable(Throws.class)
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Documented
public @interface Throw {
    /**
     * 异常类型。
     * <p>
     * 默认 {@link Throwable} 表示未明确具体异常类型（不建议长期保留默认值）。
     */
    Class<? extends Throwable> value() default Throwable.class;
}
