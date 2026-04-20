package org.zero.common.core.extension.java.util.concurrent;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * DefaultThreadFactory 测试。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
class DefaultThreadFactoryTest {
	@Test
	void shouldBuildConfiguredThreadFactory() {
		Thread.UncaughtExceptionHandler handler = (thread, throwable) -> {
		};
		ThreadGroup group = new ThreadGroup("demo-group");
		ThreadFactory threadFactory = DefaultThreadFactory.builder("demo")
				.group(group)
				.stackSize(1024L)
				.priority(Thread.MAX_PRIORITY)
				.daemon(Boolean.TRUE)
				.uncaughtExceptionHandler(handler)
				.build();

		Thread thread = threadFactory.newThread(() -> {
		});
		assertEquals(group, thread.getThreadGroup());
		assertEquals(Thread.MAX_PRIORITY, thread.getPriority());
		assertTrue(thread.isDaemon());
		assertSame(handler, thread.getUncaughtExceptionHandler());
		assertTrue(thread.getName().startsWith("demo[pool"));
		assertTrue(thread.getName().endsWith("-thread1]"));
	}

	@Test
	void shouldIncrementPoolNumberForFactoriesWithSameBaseName() {
		String baseName = "same-base-name";
		ThreadFactory firstFactory = DefaultThreadFactory.builder(baseName).build();
		ThreadFactory secondFactory = DefaultThreadFactory.builder(baseName).build();

		Thread firstThread = firstFactory.newThread(() -> {
		});
		Thread secondThread = secondFactory.newThread(() -> {
		});

		long firstPoolNumber = this.poolNumberOf(firstThread);
		long secondPoolNumber = this.poolNumberOf(secondThread);
		assertEquals(firstPoolNumber + 1L, secondPoolNumber);
		assertNotEquals(firstThread.getName(), secondThread.getName());
	}

	private long poolNumberOf(Thread thread) {
		String threadName = thread.getName();
		int poolStart = threadName.indexOf("[pool") + "[pool".length();
		int poolEnd = threadName.indexOf("-thread");
		return Long.parseLong(threadName.substring(poolStart, poolEnd));
	}
}
