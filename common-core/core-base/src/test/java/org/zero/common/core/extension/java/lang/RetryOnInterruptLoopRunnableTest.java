package org.zero.common.core.extension.java.lang;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
class RetryOnInterruptLoopRunnableTest {
	@Test
	void shouldRetryAfterInterruptedException() throws InterruptedException {
		AtomicInteger invocationCount = new AtomicInteger();
		AtomicInteger retryCount = new AtomicInteger();
		AtomicBoolean interrupted = new AtomicBoolean();
		RetryOnInterruptLoopRunnable loopRunnable = new RetryOnInterruptLoopRunnable(() -> {
			if (invocationCount.incrementAndGet() == 1) {
				throw new InterruptedException("retry");
			}
			Thread.currentThread().interrupt();
		}) {
			@Override
			protected void beforeRetryAfterInterrupt(InterruptedException interruptedException) {
				retryCount.incrementAndGet();
			}
		};

		Thread thread = this.start(loopRunnable, interrupted);
		thread.join(1000L);

		assertFalse(thread.isAlive());
		assertEquals(2, invocationCount.get());
		assertEquals(1, retryCount.get());
		assertTrue(interrupted.get());
	}

	@Test
	void shouldStopWhenRetryRejected() throws InterruptedException {
		AtomicInteger invocationCount = new AtomicInteger();
		AtomicInteger retryCount = new AtomicInteger();
		AtomicBoolean interrupted = new AtomicBoolean();
		RetryOnInterruptLoopRunnable loopRunnable = new RetryOnInterruptLoopRunnable(() -> {
			invocationCount.incrementAndGet();
			throw new InterruptedException("stop");
		}) {
			@Override
			protected boolean shouldRetryAfterInterrupt(InterruptedException interruptedException) {
				return false;
			}

			@Override
			protected void beforeRetryAfterInterrupt(InterruptedException interruptedException) {
				retryCount.incrementAndGet();
			}
		};

		Thread thread = this.start(loopRunnable, interrupted);
		thread.join(1000L);

		assertFalse(thread.isAlive());
		assertEquals(1, invocationCount.get());
		assertEquals(0, retryCount.get());
		assertTrue(interrupted.get());
	}

	@Test
	void shouldStopWhenRetryHookInterrupted() throws InterruptedException {
		AtomicInteger invocationCount = new AtomicInteger();
		AtomicInteger retryCount = new AtomicInteger();
		AtomicBoolean interrupted = new AtomicBoolean();
		RetryOnInterruptLoopRunnable loopRunnable = new RetryOnInterruptLoopRunnable(() -> {
			invocationCount.incrementAndGet();
			throw new InterruptedException("retry");
		}) {
			@Override
			protected void beforeRetryAfterInterrupt(InterruptedException interruptedException) throws InterruptedException {
				retryCount.incrementAndGet();
				throw new InterruptedException("stop");
			}
		};

		Thread thread = this.start(loopRunnable, interrupted);
		thread.join(1000L);

		assertFalse(thread.isAlive());
		assertEquals(1, invocationCount.get());
		assertEquals(1, retryCount.get());
		assertTrue(interrupted.get());
	}

	@Test
	void shouldFailFastOnThrowable() {
		RetryOnInterruptLoopRunnable loopRunnable = new RetryOnInterruptLoopRunnable(() -> {
			throw new IllegalStateException("boom");
		}) {
			@Override
			protected void beforeRetryAfterInterrupt(InterruptedException interruptedException) {
			}
		};

		IllegalStateException exception = assertThrows(IllegalStateException.class, loopRunnable::run);
		assertEquals("boom", exception.getMessage());
	}

	@Test
	void shouldWaitForRetryHookBeforeNextIteration() throws InterruptedException {
		AtomicInteger invocationCount = new AtomicInteger();
		CountDownLatch retryEntered = new CountDownLatch(1);
		CountDownLatch continueRetry = new CountDownLatch(1);
		RetryOnInterruptLoopRunnable loopRunnable = new RetryOnInterruptLoopRunnable(() -> {
			if (invocationCount.incrementAndGet() == 1) {
				throw new InterruptedException("retry");
			}
			Thread.currentThread().interrupt();
		}) {
			@Override
			protected void beforeRetryAfterInterrupt(InterruptedException interruptedException) throws InterruptedException {
				retryEntered.countDown();
				assertTrue(continueRetry.await(1, TimeUnit.SECONDS));
			}
		};

		Thread thread = new Thread(loopRunnable, "retry-on-interrupt-wait");
		thread.start();

		assertTrue(retryEntered.await(1, TimeUnit.SECONDS));
		assertEquals(1, invocationCount.get());
		continueRetry.countDown();
		thread.join(1000L);

		assertFalse(thread.isAlive());
		assertEquals(2, invocationCount.get());
	}

	protected Thread start(Runnable runnable, AtomicBoolean interrupted) {
		Thread thread = new Thread(() -> interrupted.set(this.isInterruptedAfter(runnable)), "retry-on-interrupt-test");
		thread.start();
		return thread;
	}

	private boolean isInterruptedAfter(Runnable runnable) {
		runnable.run();
		return Thread.currentThread().isInterrupted();
	}
}
