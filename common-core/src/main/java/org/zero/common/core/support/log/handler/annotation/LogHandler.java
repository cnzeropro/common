package org.zero.common.core.support.log.handler.annotation;

import org.zero.common.core.support.log.handler.processor.DefaultLogProcessor;
import org.zero.common.core.support.log.handler.processor.LogProcessor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/15
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogHandler {
    /**
     * 是否启用
     */
    boolean enable() default true;

    /**
     * 日志处理器
     *
     * @see LogProcessor
     */
    Class<? extends LogProcessor> processor() default DefaultLogProcessor.class;

    /**
     * 是否异步处理
     * <p>
     * 如无必要不建议异步处理，否则会出现日志错乱的情况。
     */
    boolean async() default false;
}
