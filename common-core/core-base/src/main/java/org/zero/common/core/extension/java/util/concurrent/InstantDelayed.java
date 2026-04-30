package org.zero.common.core.extension.java.util.concurrent;

import org.zero.common.core.util.java.util.concurrent.TimeUnitUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/17
 */
public interface InstantDelayed extends Delayed {
	long PERPETUITY = Long.MAX_VALUE;

	Instant getExpireTime();

	default boolean isExpired() {
		Instant expireTime = this.getExpireTime();
		return Objects.nonNull(expireTime) && !Instant.now().isBefore(expireTime);
	}

	/**
	 * 获取延迟时间
	 *
	 * @param unit 时间单位
	 * @return 延迟时间
	 */
	@Override
	default long getDelay(TimeUnit unit) {
		Instant expireTime = this.getExpireTime();
		if (Objects.isNull(expireTime)) {
			return PERPETUITY;
		}
		Duration duration = Duration.between(Instant.now(), expireTime);
		return TimeUnitUtil.convert(unit, duration);
	}

	@Override
	default int compareTo(Delayed o) {
		if (o == this) {
			return 0;
		}
		if (o instanceof InstantDelayed) {
			return Comparator.nullsLast(Instant::compareTo).compare(this.getExpireTime(), ((InstantDelayed) o).getExpireTime());
		}
		return Long.compare(this.getDelay(TimeUnit.NANOSECONDS), o.getDelay(TimeUnit.NANOSECONDS));
	}
}
