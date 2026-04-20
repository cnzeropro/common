package org.zero.common.core.extension.java.util.concurrent.locks;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/20
 */
class ReentrantSegmentedLockTest {
	@Test
	void shouldReturnFalseWhenObjectLockIsOccupied() throws Exception {
		ReentrantSegmentedLock lock = new ReentrantSegmentedLock(4);
		Object key = "occupied";
		ExecutorService executor = Executors.newSingleThreadExecutor();

		lock.lock(key);
		try {
			Future<Boolean> failedTryLock = executor.submit(() -> lock.tryLock(key));
			assertFalse(failedTryLock.get(1L, TimeUnit.SECONDS));
		} finally {
			lock.unlock(key);
		}

		try {
			Future<Boolean> successTryLock = executor.submit(() -> {
				boolean acquired = lock.tryLock(key);
				if (acquired) {
					lock.unlock(key);
				}
				return acquired;
			});
			assertTrue(successTryLock.get(1L, TimeUnit.SECONDS));
		} finally {
			executor.shutdownNow();
			assertTrue(executor.awaitTermination(1L, TimeUnit.SECONDS));
		}
	}

	@Test
	void shouldRespectTimeoutWhenTryingObjectLock() throws Exception {
		ReentrantSegmentedLock lock = new ReentrantSegmentedLock(4);
		Object key = "timeout";
		ExecutorService executor = Executors.newSingleThreadExecutor();

		lock.lock(key);
		try {
			Future<Boolean> timeoutResult = executor.submit(() -> lock.tryLock(key, Duration.ofMillis(150L)));
			assertFalse(timeoutResult.get(1L, TimeUnit.SECONDS));

			Future<Boolean> successAfterRelease = executor.submit(() -> {
				boolean acquired = lock.tryLock(key, Duration.ofSeconds(1L));
				if (acquired) {
					lock.unlock(key);
				}
				return acquired;
			});
			assertFalse(successAfterRelease.isDone());
			lock.unlock(key);
			assertTrue(successAfterRelease.get(1L, TimeUnit.SECONDS));
		} finally {
			if (lock.tryLock(key)) {
				lock.unlock(key);
			}
			executor.shutdownNow();
			assertTrue(executor.awaitTermination(1L, TimeUnit.SECONDS));
		}
	}

	@Test
	void shouldThrowInterruptedExceptionWhenWaitingInterruptibly() throws Exception {
		ReentrantSegmentedLock lock = new ReentrantSegmentedLock(4);
		Object key = "interruptible";
		CountDownLatch waiting = new CountDownLatch(1);
		AtomicBoolean interrupted = new AtomicBoolean(false);
		AtomicBoolean acquired = new AtomicBoolean(false);

		lock.lock(key);
		try {
			Thread thread = new Thread(() -> {
				waiting.countDown();
				try {
					lock.lockInterruptibly(key);
					acquired.set(true);
					lock.unlock(key);
				} catch (InterruptedException e) {
					interrupted.set(true);
				}
			}, "segment-lock-interruptibly");
			thread.start();

			assertTrue(waiting.await(1L, TimeUnit.SECONDS));
			thread.interrupt();
			thread.join(1_000L);

			assertFalse(thread.isAlive());
			assertTrue(interrupted.get());
			assertFalse(acquired.get());
		} finally {
			lock.unlock(key);
		}
	}

	@Test
	void shouldRetryAfterInterruptAndRestoreInterruptFlag() throws Exception {
		ReentrantSegmentedLock lock = new ReentrantSegmentedLock(4);
		Object key = "retry";
		CountDownLatch waiting = new CountDownLatch(1);
		CountDownLatch finished = new CountDownLatch(1);
		AtomicBoolean acquired = new AtomicBoolean(false);
		AtomicBoolean interruptedAfterAcquire = new AtomicBoolean(false);

		lock.lock(key);
		Thread thread = new Thread(() -> {
			waiting.countDown();
			lock.lockInterruptiblyRetryOnInterrupt(key);
			try {
				acquired.set(true);
				interruptedAfterAcquire.set(Thread.currentThread().isInterrupted());
			} finally {
				lock.unlock(key);
				finished.countDown();
			}
		}, "segment-lock-retry-on-interrupt");
		thread.start();

		assertTrue(waiting.await(1L, TimeUnit.SECONDS));
		thread.interrupt();
		lock.unlock(key);

		assertTrue(finished.await(1L, TimeUnit.SECONDS));
		thread.join(1_000L);

		assertFalse(thread.isAlive());
		assertTrue(acquired.get());
		assertTrue(interruptedAfterAcquire.get());
	}

	@Test
	void shouldSerializeDifferentKeysInSameSegment() throws Exception {
		InspectableReentrantSegmentedLock lock = new InspectableReentrantSegmentedLock(4);
		FixedHashKey firstKey = new FixedHashKey("first", 1);
		FixedHashKey secondKey = new FixedHashKey("second", 5);
		CountDownLatch firstAcquired = new CountDownLatch(1);
		CountDownLatch allowFirstRelease = new CountDownLatch(1);
		CountDownLatch secondAcquired = new CountDownLatch(1);
		ExecutorService executor = Executors.newFixedThreadPool(2);

		assertEquals(lock.segmentIndexOf(firstKey), lock.segmentIndexOf(secondKey));
		try {
			Future<Void> firstTask = executor.submit(() -> {
				lock.lock(firstKey);
				try {
					firstAcquired.countDown();
					assertTrue(allowFirstRelease.await(1L, TimeUnit.SECONDS));
				} finally {
					lock.unlock(firstKey);
				}
				return null;
			});
			Future<Void> secondTask = executor.submit(() -> {
				assertTrue(firstAcquired.await(1L, TimeUnit.SECONDS));
				lock.lock(secondKey);
				try {
					secondAcquired.countDown();
				} finally {
					lock.unlock(secondKey);
				}
				return null;
			});

			assertTrue(firstAcquired.await(1L, TimeUnit.SECONDS));
			assertFalse(secondAcquired.await(200L, TimeUnit.MILLISECONDS));
			allowFirstRelease.countDown();
			assertTrue(secondAcquired.await(1L, TimeUnit.SECONDS));
			firstTask.get(1L, TimeUnit.SECONDS);
			secondTask.get(1L, TimeUnit.SECONDS);
		} finally {
			executor.shutdownNow();
			assertTrue(executor.awaitTermination(1L, TimeUnit.SECONDS));
		}
	}

	@Test
	void shouldMapNullKeyToFirstSegment() {
		InspectableReentrantSegmentedLock lock = new InspectableReentrantSegmentedLock(4);

		assertEquals(0, lock.segmentIndexOf(null));
	}

	@Test
	void shouldRetryInterruptiblyByDefaultAndRestoreInterruptFlag() throws Exception {
		RetryingSegmentedLock lock = new RetryingSegmentedLock();
		Object key = "default-retry";
		AtomicBoolean interrupted = new AtomicBoolean(false);

		Thread thread = new Thread(() -> {
			lock.lockInterruptiblyRetryOnInterrupt(key);
			interrupted.set(Thread.currentThread().isInterrupted());
		}, "segmented-lock-default-retry");
		thread.start();
		thread.join(1_000L);

		assertFalse(thread.isAlive());
		assertEquals(2, lock.attempts.get());
		assertSame(key, lock.lastKey);
		assertTrue(interrupted.get());
	}

	@Test
	void shouldDelegateDurationOverloadToTimedTryLock() throws Exception {
		RecordingSegmentedLock lock = new RecordingSegmentedLock();
		Object key = "delegate";
		Duration timeout = Duration.ofMillis(250L);

		assertTrue(lock.tryLock(key, timeout));
		assertEquals("tryLockTimed", lock.lastOperation);
		assertSame(key, lock.lastKey);
		assertEquals(timeout.toNanos(), lock.lastTime);
		assertSame(TimeUnit.NANOSECONDS, lock.lastUnit);
	}

	private static class InspectableReentrantSegmentedLock extends ReentrantSegmentedLock {
		private InspectableReentrantSegmentedLock(int concurrency) {
			super(concurrency);
		}

		private int segmentIndexOf(Object key) {
			return this.resolveSegmentIndex(key);
		}
	}

	private static class FixedHashKey {
		private final String value;
		private final int hash;

		private FixedHashKey(String value, int hash) {
			this.value = value;
			this.hash = hash;
		}

		@Override
		public int hashCode() {
			return this.hash;
		}

		@Override
		public String toString() {
			return this.value;
		}
	}

	private static class RetryingSegmentedLock implements SegmentedLock {
		private final AtomicInteger attempts = new AtomicInteger();
		private Object lastKey;

		@Override
		public void lock(Object key) {
			this.lastKey = key;
		}

		@Override
		public void lockInterruptibly(Object key) throws InterruptedException {
			this.lastKey = key;
			if (this.attempts.incrementAndGet() == 1) {
				throw new InterruptedException("retry");
			}
		}

		@Override
		public boolean tryLock(Object key) {
			this.lastKey = key;
			return true;
		}

		@Override
		public boolean tryLock(Object key, long time, TimeUnit unit) {
			this.lastKey = key;
			return true;
		}

		@Override
		public void unlock(Object key) {
			this.lastKey = key;
		}
	}

	private static class RecordingSegmentedLock implements SegmentedLock {
		private String lastOperation;
		private Object lastKey;
		private long lastTime;
		private TimeUnit lastUnit;

		@Override
		public void lock(Object key) {
			this.lastOperation = "lock";
			this.lastKey = key;
		}

		@Override
		public void lockInterruptibly(Object key) {
			this.lastOperation = "lockInterruptibly";
			this.lastKey = key;
		}

		@Override
		public boolean tryLock(Object key) {
			this.lastOperation = "tryLock";
			this.lastKey = key;
			return true;
		}

		@Override
		public boolean tryLock(Object key, long time, TimeUnit unit) {
			this.lastOperation = "tryLockTimed";
			this.lastKey = key;
			this.lastTime = time;
			this.lastUnit = unit;
			return true;
		}

		@Override
		public void unlock(Object key) {
			this.lastOperation = "unlock";
			this.lastKey = key;
		}
	}
}
