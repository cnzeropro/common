package org.zero.common.core.support.fallback;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 降级注解
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/26
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Fallback {
    /**
     * 是否启用
     */
    boolean enable() default true;

    /**
     * 降级处理类
     * <p>
     * {@code void.class} 表示使用当前类
     */
    Class<?> targetClass() default void.class;

    /**
     * 降级处理方法，同 {@link #value()}
     * <p>
     * {@code ""} 表示使用其同名方法
     */
    @AliasFor("value")
    String targetMethod() default "";

    /**
     * 降级处理方法，同 {@link #targetMethod()}
     */
    @AliasFor("method")
    String value() default "";
}
