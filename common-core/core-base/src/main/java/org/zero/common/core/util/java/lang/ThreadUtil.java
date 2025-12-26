package org.zero.common.core.util.java.lang;

import org.zero.common.core.util.java.util.concurrent.TimeUnitUtil;

import java.time.Duration;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/28
 */
public class ThreadUtil {
	public static final int NANOSECONDS_THRESHOLD = 1_000_000;

	/**
	 * copy from java 19
	 *
	 * @see java.lang.Thread#sleep(Duration)
	 */
	public static void sleep(Duration duration) throws InterruptedException {
		// MAX_VALUE if > 292 years
		long nanos = TimeUnitUtil.convert(NANOSECONDS, duration);
		if (nanos < 0) {
			return;
		}
		if (nanos < NANOSECONDS_THRESHOLD) {
			Thread.sleep(0L, (int) nanos);
		}
		long millis = TimeUnitUtil.convert(MILLISECONDS, duration);
		if (millis < 0) {
			return;
		}
		Thread.sleep(millis);
	}
}
