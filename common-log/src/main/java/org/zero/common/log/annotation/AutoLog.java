package org.zero.common.log.annotation;

import org.zero.common.log.constant.LogType;
import org.zero.common.log.constant.OperateType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zero
 * @date 2022/1/3
 */
@Inherited
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoLog {
    /**
     * 日志描述（支持SpEL）
     */
    String value() default "";

    /**
     * 日志所属项目，默认当前项目名
     */
    String app() default "";

    /**
     * 日志所属模块
     */
    String module() default "";

    /**
     * 日志类型
     */
    LogType type() default LogType.OTHER;

    /**
     * 日志操作类型
     */
    OperateType operateType() default OperateType.OTHER;

    /**
     * 是否保存请求的参数
     */
    boolean withParam() default true;

    /**
     * 是否保存响应的参数
     */
    boolean withReturnData() default true;

    /**
     * 排除指定的请求参数
     */
    String[] excludedParamNames() default {};
}
