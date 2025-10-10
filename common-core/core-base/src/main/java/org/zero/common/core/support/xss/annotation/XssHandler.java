package org.zero.common.core.support.xss.annotation;

import org.zero.common.core.support.aware.perceptor.Perceptor;
import org.zero.common.core.support.xss.processor.XssMode;
import org.zero.common.core.support.xss.processor.XssProcessor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/15
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XssHandler {
    /**
     * 是否启用
     */
    boolean enable() default true;

    /**
     * 日志处理器
     *
     * @see Perceptor
     */
    Class<? extends XssProcessor> processor() default XssMode.class;
}
