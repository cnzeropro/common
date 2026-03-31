package org.zero.common.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link NeedLib} 的容器注解，用于支持 {@link java.lang.annotation.Repeatable @Repeatable}。
 * <p>
 * 仅作为编译期/文档期声明：表达某个类型依赖的多个三方库坐标。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/13
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@Documented
public @interface NeedLibs {
    /**
     * 依赖声明列表。
     */
    NeedLib[] value();
}
