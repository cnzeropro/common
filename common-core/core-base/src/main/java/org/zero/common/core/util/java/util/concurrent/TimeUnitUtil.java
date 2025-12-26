package org.zero.common.core.util.java.util.concurrent;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/28
 */
public class TimeUnitUtil {
	public static final long NANO_SCALE = NANOSECONDS.toNanos(1);
	public static final long MICRO_SCALE = MICROSECONDS.toNanos(1);
	public static final long MILLISECOND_SCALE = MILLISECONDS.toNanos(1);
	public static final long SECOND_SCALE = SECONDS.toNanos(1);
	public static final long MINUTE_SCALE = MINUTES.toNanos(1);
	public static final long HOUR_SCALE = HOURS.toNanos(1);
	public static final long DAY_SCALE = DAYS.toNanos(1);

	/**
	 * copy from java 9
	 *
	 * @see java.util.concurrent.TimeUnit#toChronoUnit()
	 */
	public static ChronoUnit toChronoUnit(TimeUnit timeUnit) {
		switch (timeUnit) {
			case NANOSECONDS:
				return ChronoUnit.NANOS;
			case MICROSECONDS:
				return ChronoUnit.MICROS;
			case MILLISECONDS:
				return ChronoUnit.MILLIS;
			case SECONDS:
				return ChronoUnit.SECONDS;
			case MINUTES:
				return ChronoUnit.MINUTES;
			case HOURS:
				return ChronoUnit.HOURS;
			case DAYS:
				return ChronoUnit.DAYS;
			default:
				throw new AssertionError("Invalid TimeUnit: " + timeUnit);
		}
	}

	/**
	 * copy from java 11
	 *
	 * @see java.util.concurrent.TimeUnit#convert(Duration)
	 */
	public static long convert(TimeUnit timeUnit, Duration duration) {
		long scale = timeUnit.toNanos(1);
		long secRatio = (scale >= SECOND_SCALE) ? (scale / SECOND_SCALE) : (SECOND_SCALE / scale);
		long maxSecs = Long.MAX_VALUE / secRatio;
		long secs = duration.getSeconds();
		int nano = duration.getNano();
		if (secs < 0 && nano > 0) {
			// use representation compatible with integer division
			secs++;
			nano -= (int) SECOND_SCALE;
		}
		final long s, nanoVal;
		// Optimize for the common case - NANOSECONDS without overflow
		if (timeUnit == NANOSECONDS) {
			nanoVal = nano;
		} else if ((s = scale) < SECOND_SCALE) {
			nanoVal = nano / s;
		} else if (timeUnit == SECONDS) {
			return secs;
		} else {
			return secs / secRatio;
		}
		long val = secs * secRatio + nanoVal;
		return ((secs < maxSecs && secs > -maxSecs) ||
			(secs == maxSecs && val > 0) ||
			(secs == -maxSecs && val < 0))
			? val
			: (secs > 0) ? Long.MAX_VALUE : Long.MIN_VALUE;
	}
}
