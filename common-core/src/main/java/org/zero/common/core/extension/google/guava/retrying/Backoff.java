package org.zero.common.core.extension.google.guava.retrying;

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
     * @return the initial or canonical backoff period
     */
    long delay() default 1000;

    /**
     * @return the maximum delay between retries
     */
    long maxDelay() default 3000;

    /**
     * If positive, then used as a multiplier for generating the next delay for backoff.
     *
     * @return a multiplier to use to calculate the next backoff delay
     */
    long multiplier() default 1;

    /**
     * @return the time unit for the {@link #delay()}, {@link #maxDelay()} and {@link #multiplier()}
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * @return the wait policy to use for backoff
     */
    WaitPolicy waitPolicy() default WaitPolicy.FIXED;

    /**
     * wait policy
     */
    enum WaitPolicy {
        NONE,
        FIXED,
        RANDOM,
        INCREMENTING,
        EXPONENTIAL,
        FIBONACCI,
        ;
    }
}
