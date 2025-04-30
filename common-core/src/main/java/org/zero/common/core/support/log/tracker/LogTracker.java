package org.zero.common.core.support.log.tracker;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zero
 * @since 2022/1/3
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogTracker {
    @AliasFor("level")
    LogLevel value() default LogLevel.TRACE;

    @AliasFor("value")
    LogLevel level() default LogLevel.TRACE;

    boolean enable() default true;
}
