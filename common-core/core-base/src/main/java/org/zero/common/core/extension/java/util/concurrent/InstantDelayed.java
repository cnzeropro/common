package org.zero.common.core.extension.java.util.concurrent;

import org.zero.common.core.util.java.util.concurrent.TimeUnitUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 基于 {@link Instant} 表示到期时间的 {@link Delayed}。
 * <p>
 * 过期时间为 {@code null} 表示对象永久有效；在 {@link java.util.concurrent.DelayQueue} 排序中，
 * 永久有效对象会排在有明确到期时间的对象之后。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/17
 */
public interface InstantDelayed extends Delayed {
	/**
	 * 永久有效对象的最大延迟值。
	 */
	long PERPETUITY = Long.MAX_VALUE;

	/**
	 * 返回对象到期时间。
	 *
	 * @return 到期时间；返回 {@code null} 表示永久有效
	 */
	Instant getExpireTime();

	/**
	 * 判断对象是否已经到期。
	 *
	 * @return 存在到期时间且当前时间不早于到期时间时返回 {@code true}
	 */
	default boolean isExpired() {
		Instant expireTime = this.getExpireTime();
		return Objects.nonNull(expireTime) && !Instant.now().isBefore(expireTime);
	}

	/**
	 * 获取距离到期时间的剩余延迟。
	 * <p>
	 * 永久有效对象返回 {@link #PERPETUITY}，已到期对象返回负数或零，具体精度由目标时间单位决定。
	 *
	 * @param unit 目标时间单位
	 * @return 按目标时间单位换算后的剩余延迟
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

	/**
	 * 比较两个延迟对象的到期顺序。
	 * <p>
	 * 同为 {@link InstantDelayed} 时直接比较到期时间，并将 {@code null} 到期时间排在最后；
	 * 其他 {@link Delayed} 实现则按纳秒级延迟值比较。
	 *
	 * @param o 待比较的延迟对象
	 * @return 负数表示当前对象更早到期，零表示顺序相同，正数表示当前对象更晚到期
	 */
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
