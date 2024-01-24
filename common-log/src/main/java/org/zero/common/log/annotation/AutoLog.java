package org.zero.common.log.annotation;

import org.zero.common.log.constant.LogMessageEngine;
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
     * 日志引擎
     */
    LogMessageEngine engine() default LogMessageEngine.DEFAULT;

    /**
     * 是否在切入方法前执行
     */
    boolean before() default false;

    /**
     * 所属项目，为空时默认取当前项目名
     */
    String app() default "";

    /**
     * 所属模块
     */
    String module() default "";

    /**
     * 日志类型
     */
    LogType type() default LogType.UNKNOWN;

    /**
     * 操作类型
     */
    OperateType operateType() default OperateType.UNKNOWN;
}
