package org.zero.common.core.support.google.guava.retrying;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/21
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Retryable {
    int DEFAULT_MAX_ATTEMPT = 3;

    /**
     * @return the maximum number of attempts (including the first failure), defaults to 3
     */
    int maxAttempts() default DEFAULT_MAX_ATTEMPT;

    /**
     * Exception types that are retryable. Defaults to empty (and if excludes is also
     * empty all exceptions are retried).
     * @return exception types to retry
     */
    Class<? extends Throwable>[] include() default {RuntimeException.class};

    /**
     * Name of method in this class to use for recover. Method had to be marked with
     * {@link Recover} annotation.
     * @return the name of recover method
     */
    String recover() default "";

    Class<?> recoverClass() default Object.class;

    /**
     * Specify the backoff properties for retrying this operation. The default is a simple
     * {@link Backoff} specification with no properties - see its documentation for
     * defaults.
     * @return a backoff specification
     */
    Backoff backoff() default @Backoff();

}
