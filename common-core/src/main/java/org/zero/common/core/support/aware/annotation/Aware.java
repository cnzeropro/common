package org.zero.common.core.support.aware.annotation;

import org.zero.common.core.support.aware.perceptor.DefaultPerceptor;
import org.zero.common.core.support.aware.perceptor.Perceptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/15
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Aware {
    /**
     * 是否启用
     */
    boolean enable() default true;

    /**
     * 感知处理器
     *
     * @see Perceptor
     */
    Class<? extends Perceptor> perceptor() default DefaultPerceptor.class;

    /**
     * 是否异步处理
     */
    boolean async() default false;
}
