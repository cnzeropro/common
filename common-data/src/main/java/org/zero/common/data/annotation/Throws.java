package org.zero.common.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示抛出的异常
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/13
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Documented
public @interface Throws {
    Throw[] value();
}
