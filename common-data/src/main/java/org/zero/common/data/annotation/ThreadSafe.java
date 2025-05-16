package org.zero.common.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;

/**
 * 指示其线程安全
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/5/13
 */
@Retention(RetentionPolicy.CLASS)
@Target({TYPE, PACKAGE})
@Documented
public @interface ThreadSafe {
}
