package org.zero.common.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

/**
 * 指示资源不会被关闭
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/13
 */
@Retention(RetentionPolicy.CLASS)
@Target({PARAMETER, LOCAL_VARIABLE, TYPE_PARAMETER, TYPE_USE})
@Documented
public @interface WillNotClose {
}
