package org.zero.common.core.extension.java.util.concurrent;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/20
 */
class InstantDelayedTest {
	@Test
	void shouldTreatNullExpireTimeAsPerpetual() {
		TestInstantDelayed perpetual = new TestInstantDelayed(null);
		TestInstantDelayed expiring = new TestInstantDelayed(Instant.EPOCH.plusSeconds(1L));

		assertFalse(perpetual.isExpired());
		assertEquals(InstantDelayed.PERPETUITY, perpetual.getDelay(TimeUnit.NANOSECONDS));
		assertTrue(perpetual.compareTo(expiring) > 0);
		assertTrue(expiring.compareTo(perpetual) < 0);
	}

	@Test
	void shouldReportExpiredStateFromExpireTime() {
		assertTrue(new TestInstantDelayed(Instant.EPOCH).isExpired());
		assertFalse(new TestInstantDelayed(null).isExpired());
		assertFalse(new TestInstantDelayed(Instant.now().plusSeconds(60L)).isExpired());
	}

	@Test
	void shouldPollExpiredEntryBeforePerpetualEntry() {
		DelayQueue<TestInstantDelayed> queue = new DelayQueue<>();
		TestInstantDelayed perpetual = new TestInstantDelayed(null);
		TestInstantDelayed expired = new TestInstantDelayed(Instant.EPOCH);

		queue.add(perpetual);
		queue.add(expired);

		assertSame(expired, queue.poll());
		assertSame(perpetual, queue.peek());
	}

	private static final class TestInstantDelayed implements InstantDelayed {
		private final Instant expireTime;

		private TestInstantDelayed(Instant expireTime) {
			this.expireTime = expireTime;
		}

		@Override
		public Instant getExpireTime() {
			return this.expireTime;
		}
	}
}
