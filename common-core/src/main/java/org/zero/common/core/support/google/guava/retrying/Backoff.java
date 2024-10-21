package org.zero.common.core.support.google.guava.retrying;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/21
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Backoff {
    /**
     * @return the initial or canonical backoff period in milliseconds (default 0)
     */
    long delay() default 1000;

    /**
     * @return the maximum delay between retries (default 0 = ignored)
     */
    long maxTime() default 3000;

    /**
     * If positive, then used as a multiplier for generating the next delay for backoff.
     *
     * @return a multiplier to use to calculate the next backoff delay (default 0 =
     * ignored)
     */
    long multiplier() default 1;

    TimeUnit unit() default TimeUnit.MILLISECONDS;

    RetryPolicy waitStrategy() default RetryPolicy.FIXED;

    enum RetryPolicy {
        NONE,
        FIXED,
        RANDOM,
        INCREMENTING,
        EXPONENTIAL,
        FIBONACCI,
        ;
    }
}
