package org.zero.common.core.extension.java.util.concurrent;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * BoundedOverflowThreadRejectedExecutionHandler 测试。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/12
 */
class BoundedOverflowThreadRejectedExecutionHandlerTest {
	private static final long TIMEOUT_SECONDS = 5L;

	private static ThreadPoolExecutor newExecutor() {
		return new ThreadPoolExecutor(1, 1, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
	}

	private static void await(CountDownLatch latch) {
		try {
			latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new AssertionError(e);
		}
	}

	@Test
	void shouldRunRejectedTaskInOverflowThread() throws InterruptedException {
		ThreadPoolExecutor executor = newExecutor();
		try {
			CountingThreadFactory threadFactory = new CountingThreadFactory("overflow");
			BoundedOverflowThreadRejectedExecutionHandler handler = new BoundedOverflowThreadRejectedExecutionHandler(
					"demo",
					1,
					threadFactory,
					new ThreadPoolExecutor.AbortPolicy()
			);
			CountDownLatch executed = new CountDownLatch(1);
			AtomicReference<String> threadName = new AtomicReference<>();

			handler.rejectedExecution(() -> {
				threadName.set(Thread.currentThread().getName());
				executed.countDown();
			}, executor);

			assertTrue(executed.await(TIMEOUT_SECONDS, TimeUnit.SECONDS));
			assertEquals(1, threadFactory.threadCount());
			assertTrue(threadName.get().startsWith("overflow-"));
		} finally {
			executor.shutdownNow();
		}
	}

	@Test
	void shouldUseFallbackHandlerWhenOverflowLimitReached() throws InterruptedException {
		ThreadPoolExecutor executor = newExecutor();
		try {
			CountDownLatch overflowStarted = new CountDownLatch(1);
			CountDownLatch releaseOverflow = new CountDownLatch(1);
			AtomicInteger fallbackCount = new AtomicInteger();
			RejectedExecutionHandler fallbackHandler = (task, threadPoolExecutor) -> {
				fallbackCount.incrementAndGet();
				task.run();
			};
			BoundedOverflowThreadRejectedExecutionHandler handler = new BoundedOverflowThreadRejectedExecutionHandler(
					"demo",
					1,
					new CountingThreadFactory("overflow"),
					fallbackHandler
			);
			AtomicBoolean fallbackTaskExecuted = new AtomicBoolean();

			handler.rejectedExecution(() -> {
				overflowStarted.countDown();
				await(releaseOverflow);
			}, executor);
			assertTrue(overflowStarted.await(TIMEOUT_SECONDS, TimeUnit.SECONDS));

			handler.rejectedExecution(() -> fallbackTaskExecuted.set(true), executor);

			assertEquals(1, fallbackCount.get());
			assertTrue(fallbackTaskExecuted.get());
			releaseOverflow.countDown();
		} finally {
			executor.shutdownNow();
		}
	}

	@Test
	void shouldReleaseOverflowPermitAfterTaskFinished() throws InterruptedException {
		ThreadPoolExecutor executor = newExecutor();
		try {
			CountDownLatch overflowThreadFinished = new CountDownLatch(1);
			CountingThreadFactory threadFactory = new CountingThreadFactory("overflow", overflowThreadFinished);
			AtomicInteger fallbackCount = new AtomicInteger();
			BoundedOverflowThreadRejectedExecutionHandler handler = new BoundedOverflowThreadRejectedExecutionHandler(
					"demo",
					1,
					threadFactory,
					(task, threadPoolExecutor) -> fallbackCount.incrementAndGet()
			);
			CountDownLatch firstTaskExecuted = new CountDownLatch(1);
			CountDownLatch secondTaskExecuted = new CountDownLatch(1);

			handler.rejectedExecution(firstTaskExecuted::countDown, executor);
			assertTrue(firstTaskExecuted.await(TIMEOUT_SECONDS, TimeUnit.SECONDS));
			assertTrue(overflowThreadFinished.await(TIMEOUT_SECONDS, TimeUnit.SECONDS));
			handler.rejectedExecution(secondTaskExecuted::countDown, executor);

			assertTrue(secondTaskExecuted.await(TIMEOUT_SECONDS, TimeUnit.SECONDS));
			assertEquals(2, threadFactory.threadCount());
			assertEquals(0, fallbackCount.get());
		} finally {
			executor.shutdownNow();
		}
	}

	@Test
	void shouldRejectExplicitlyWhenExecutorShutdown() {
		ThreadPoolExecutor executor = newExecutor();
		executor.shutdownNow();
		BoundedOverflowThreadRejectedExecutionHandler handler = new BoundedOverflowThreadRejectedExecutionHandler("demo");

		assertThrows(RejectedExecutionException.class, () -> handler.rejectedExecution(() -> {
		}, executor));
	}

	@Test
	void shouldValidateConstructorArguments() {
		RejectedExecutionHandler fallbackHandler = new ThreadPoolExecutor.AbortPolicy();
		ThreadFactory threadFactory = new CountingThreadFactory("overflow");

		assertThrows(IllegalArgumentException.class, () -> new BoundedOverflowThreadRejectedExecutionHandler(null));
		assertThrows(IllegalArgumentException.class, () -> new BoundedOverflowThreadRejectedExecutionHandler(" "));
		assertThrows(IllegalArgumentException.class, () -> new BoundedOverflowThreadRejectedExecutionHandler("demo", 0));
		assertThrows(NullPointerException.class,
				() -> new BoundedOverflowThreadRejectedExecutionHandler("demo", 1, null, fallbackHandler));
		assertThrows(NullPointerException.class,
				() -> new BoundedOverflowThreadRejectedExecutionHandler("demo", 1, threadFactory, null));
	}

	private static final class CountingThreadFactory implements ThreadFactory {
		private final String namePrefix;
		private final AtomicInteger threadNumber = new AtomicInteger();
		private final CountDownLatch threadFinished;

		private CountingThreadFactory(String namePrefix) {
			this(namePrefix, null);
		}

		private CountingThreadFactory(String namePrefix, CountDownLatch threadFinished) {
			this.namePrefix = namePrefix;
			this.threadFinished = threadFinished;
		}

		@Override
		public Thread newThread(Runnable r) {
			Runnable task = () -> {
				try {
					r.run();
				} finally {
					if (threadFinished != null) {
						threadFinished.countDown();
					}
				}
			};
			Thread thread = new Thread(task, namePrefix + "-" + threadNumber.incrementAndGet());
			thread.setDaemon(true);
			return thread;
		}

		private int threadCount() {
			return threadNumber.get();
		}
	}
}
