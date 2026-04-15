package org.zero.common.core.aop.aspectj.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记需要由原生 AspectJ 记录执行耗时的方法。
 * <p>
 * 当前模块发布的是 AspectJ aspect library，下游项目需要自行选择 LTW 或 CTW 接入。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/30
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
}
