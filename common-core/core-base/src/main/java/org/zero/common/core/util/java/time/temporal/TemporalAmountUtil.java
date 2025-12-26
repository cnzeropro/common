package org.zero.common.core.util.java.time.temporal;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.Period;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/12/18
 */
@UtilityClass
public class TemporalAmountUtil {
    /**
     * Maximum duration.
     */
    public static final Duration MAX_DURATION = Duration.ofSeconds(Long.MAX_VALUE, 999_999_999);
    /**
     * Minimum duration.
     */
    public static final Duration MIN_DURATION = Duration.ofSeconds(Long.MIN_VALUE, 0);

    /**
     * Maximum period.
     */
    public static final Period MAX_PERIOD = Period.of(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
    /**
     * Minimum period.
     */
    public static final Period MIN_PERIOD = Period.of(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
}
