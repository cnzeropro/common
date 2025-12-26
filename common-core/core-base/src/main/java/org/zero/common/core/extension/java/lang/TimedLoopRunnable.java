package org.zero.common.core.extension.java.lang;

import java.time.Duration;
import java.time.Instant;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/17
 */
public class TimedLoopRunnable extends LoopRunnable {
	protected final Instant endTime;

	public TimedLoopRunnable(ThrowableRunnable runnable, Duration duration) {
		this(runnable, Instant.now().plus(duration));
	}

	public TimedLoopRunnable(ThrowableRunnable runnable, Instant endTime) {
		super(runnable);
		this.endTime = endTime;
	}

	@Override
	protected boolean shouldRunning() {
		return Instant.now().isBefore(endTime) && super.shouldRunning();
	}
}
