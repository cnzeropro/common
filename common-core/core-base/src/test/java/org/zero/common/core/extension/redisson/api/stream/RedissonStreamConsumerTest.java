package org.zero.common.core.extension.redisson.api.stream;

import org.junit.jupiter.api.Test;
import org.redisson.client.RedisTimeoutException;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
class RedissonStreamConsumerTest {
	@Test
	void shouldSleepAfterSuccessfulPollingCycle() throws InterruptedException {
		PollingProcessor processor = new PollingProcessor();
		TrackingRedissonStreamConsumer consumer = new TrackingRedissonStreamConsumer(processor);

		Thread thread = new Thread(consumer, "polling-consumer");
		thread.start();
		thread.join(1000L);

		assertFalse(thread.isAlive());
		assertEquals(1, processor.getInvocationCount());
		assertEquals(1, consumer.getSleepCount());
	}

	@Test
	void shouldNotSleepAfterSuccessfulBlockingCycle() throws InterruptedException {
		InterruptingProcessor processor = new InterruptingProcessor();
		TrackingRedissonStreamConsumer consumer = new TrackingRedissonStreamConsumer(processor);

		Thread thread = new Thread(consumer, "blocking-consumer");
		thread.start();
		thread.join(1000L);

		assertFalse(thread.isAlive());
		assertEquals(1, processor.getInvocationCount());
		assertEquals(0, consumer.getSleepCount());
	}

	@Test
	void shouldStillSleepAfterRedisTimeout() throws InterruptedException {
		TimeoutProcessor processor = new TimeoutProcessor();
		TrackingRedissonStreamConsumer consumer = new TrackingRedissonStreamConsumer(processor);

		Thread thread = new Thread(consumer, "timeout-consumer");
		thread.start();
		thread.join(1000L);

		assertFalse(thread.isAlive());
		assertEquals(1, processor.getInvocationCount());
		assertEquals(1, consumer.getSleepCount());
	}

	private static class TrackingRedissonStreamConsumer extends RedissonStreamConsumer {
		private final AtomicInteger sleepCount = new AtomicInteger();

		private TrackingRedissonStreamConsumer(MessageProcessor processor) {
			super(processor, Duration.ofMillis(1));
		}

		@Override
		protected void sleep() {
			sleepCount.incrementAndGet();
			Thread.currentThread().interrupt();
		}

		private int getSleepCount() {
			return sleepCount.get();
		}
	}

	private static class PollingProcessor implements PollingMessageProcessor {
		private final AtomicInteger invocationCount = new AtomicInteger();

		@Override
		public void process() {
			invocationCount.incrementAndGet();
		}

		private int getInvocationCount() {
			return invocationCount.get();
		}
	}

	private static class InterruptingProcessor implements MessageProcessor {
		private final AtomicInteger invocationCount = new AtomicInteger();

		@Override
		public void process() {
			invocationCount.incrementAndGet();
			Thread.currentThread().interrupt();
		}

		private int getInvocationCount() {
			return invocationCount.get();
		}
	}

	private static class TimeoutProcessor implements MessageProcessor {
		private final AtomicInteger invocationCount = new AtomicInteger();

		@Override
		public void process() {
			invocationCount.incrementAndGet();
			throw new RedisTimeoutException("timeout");
		}

		private int getInvocationCount() {
			return invocationCount.get();
		}
	}
}
