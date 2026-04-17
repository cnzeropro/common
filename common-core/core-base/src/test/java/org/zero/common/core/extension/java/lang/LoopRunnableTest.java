package org.zero.common.core.extension.java.lang;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
class LoopRunnableTest {
	@Test
	void shouldRestoreInterruptedFlagAndStopByDefault() throws InterruptedException {
		AtomicInteger invocationCount = new AtomicInteger();
		AtomicBoolean interrupted = new AtomicBoolean();
		LoopRunnable loopRunnable = new LoopRunnable(() -> {
			invocationCount.incrementAndGet();
			throw new InterruptedException("stop");
		});

		Thread thread = this.start(loopRunnable, interrupted);
		thread.join(1000L);

		assertFalse(thread.isAlive());
		assertEquals(1, invocationCount.get());
		assertTrue(interrupted.get());
	}

	@Test
	void shouldRouteAfterRunSuccessInterruptThroughSameFlow() throws InterruptedException {
		AtomicInteger invocationCount = new AtomicInteger();
		AtomicInteger afterRunSuccessCount = new AtomicInteger();
		AtomicBoolean interrupted = new AtomicBoolean();
		LoopRunnable loopRunnable = new LoopRunnable(invocationCount::incrementAndGet) {
			@Override
			protected void afterRunSuccess() throws InterruptedException {
				afterRunSuccessCount.incrementAndGet();
				throw new InterruptedException("stop after success");
			}
		};

		Thread thread = this.start(loopRunnable, interrupted);
		thread.join(1000L);

		assertFalse(thread.isAlive());
		assertEquals(1, invocationCount.get());
		assertEquals(1, afterRunSuccessCount.get());
		assertTrue(interrupted.get());
	}

	protected Thread start(Runnable runnable, AtomicBoolean interrupted) {
		Thread thread = new Thread(() -> interrupted.set(this.isInterruptedAfter(runnable)), "loop-runnable-test");
		thread.start();
		return thread;
	}

	private boolean isInterruptedAfter(Runnable runnable) {
		runnable.run();
		return Thread.currentThread().isInterrupted();
	}
}
