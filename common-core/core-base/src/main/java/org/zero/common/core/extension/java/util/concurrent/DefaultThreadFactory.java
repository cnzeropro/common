package org.zero.common.core.extension.java.util.concurrent;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.zero.common.core.extension.java.lang.InnerBuilder;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 可配置的默认线程工厂实现。
 * <p>
 * 线程名称格式为 {@code baseName[poolX-threadY]}。相同 {@code baseName} 的工厂共享池编号，
 * 单个工厂实例内部的线程编号从 1 开始递增。
 * <p>
 * 若未显式指定 {@code daemon}，则沿用 {@link Thread} 构造线程时的默认语义，即继承创建线程的
 * daemon 状态；这与 {@link java.util.concurrent.Executors#defaultThreadFactory()} 固定创建
 * 非 daemon 线程的行为不同。
 *
 * @author Zero (cnzeropro@163.com)
 * @see java.util.concurrent.Executors#defaultThreadFactory()
 * @since 2025/7/14
 */
public class DefaultThreadFactory implements ThreadFactory {
	protected static final ConcurrentMap<String, Long> POOL_NUMBER_MAP = new ConcurrentHashMap<>();

	protected final AtomicLong threadNumber = new AtomicLong(1L);

	protected final String baseName;
	protected final ThreadGroup group;
	protected final long stackSize;
	protected final int priority;
	protected final Boolean daemon;
	protected final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
	protected final String namePattern;

	/**
	 * 创建线程工厂。
	 *
	 * @param baseName                 线程名称前缀
	 * @param group                    线程组
	 * @param stackSize                线程栈大小
	 * @param priority                 线程优先级
	 * @param daemon                   守护线程标记；为 {@code null} 时继承创建线程语义
	 * @param uncaughtExceptionHandler 未捕获异常处理器
	 */
	protected DefaultThreadFactory(String baseName, ThreadGroup group, long stackSize, int priority, Boolean daemon, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
		this.baseName = Objects.requireNonNull(baseName, "baseName");
		this.group = group;
		this.stackSize = stackSize;
		this.daemon = daemon;
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;
		this.priority = priority;
		long poolNumber = POOL_NUMBER_MAP.compute(this.baseName, (k, v) -> Objects.isNull(v) ? 1L : v + 1L);
		this.namePattern = "%s[pool" + poolNumber + "-thread%d]";
	}

	/**
	 * 创建线程工厂构建器。
	 *
	 * @param baseName 线程名称前缀
	 * @return 构建器
	 */
	public static Builder builder(String baseName) {
		return new Builder(baseName);
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r, String.format(namePattern, baseName, threadNumber.getAndIncrement()), stackSize);
		if (Objects.nonNull(daemon)) {
			t.setDaemon(daemon);
		}
		t.setPriority(priority);
		t.setUncaughtExceptionHandler(uncaughtExceptionHandler);
		return t;
	}

	@Setter
	@Accessors(chain = true, fluent = true)
	public static class Builder extends InnerBuilder<ThreadFactory, Builder> {
		/**
		 * 线程名称前缀。
		 */
		protected String baseName;

		protected ThreadGroup group;
		protected long stackSize;
		protected int priority = Thread.NORM_PRIORITY;
		protected Boolean daemon;
		protected Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

		/**
		 * 创建构建器。
		 *
		 * @param baseName 线程名称前缀
		 */
		protected Builder(String baseName) {
			this.baseName = baseName;
		}
	}
}
