package org.zero.common.core.extension.java.lang;

import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/20
 */
@Setter
@Accessors(chain = true, fluent = true)
public class ThreadBuilder implements Builder<Thread, ThreadBuilder> {
	protected static final AtomicLong THREAD_NUMBER = new AtomicLong(1L);

	protected Runnable task;
	protected CharSequence taskName;
	protected ThreadGroup group;
	protected long stackSize;
	protected int priority = Thread.NORM_PRIORITY;
	protected Boolean daemon;
	protected Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
	protected boolean start;

	public ThreadBuilder start() {
		return this.start(true);
	}

	@Override
	public Thread build() {
		String name = Objects.nonNull(taskName) ? taskName.toString() : generateName();
		Thread thread = new Thread(group, task, name, stackSize);
		if (Objects.nonNull(daemon)) {
			thread.setDaemon(daemon);
		}
		thread.setPriority(priority);
		thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
		if (start) {
			thread.start();
		}
		return thread;
	}

	protected static String generateName() {
		return "Thread-" + THREAD_NUMBER.getAndIncrement();
	}
}
