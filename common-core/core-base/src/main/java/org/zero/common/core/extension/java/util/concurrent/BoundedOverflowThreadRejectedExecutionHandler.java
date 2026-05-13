package org.zero.common.core.extension.java.util.concurrent;

import lombok.extern.java.Log;
import org.zero.common.core.util.java.lang.CharSequenceUtil;
import org.zero.common.core.util.java.lang.StringUtil;

import java.util.Objects;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;

/**
 * 有界溢出线程拒绝策略。
 * <p>
 * 当线程池达到容量上限并触发拒绝策略时，本策略允许少量任务临时使用线程池外部的新线程执行。
 * 溢出线程数量由 {@code maxOverflowThreads} 限制，避免在持续过载时绕过线程池上限无限创建线程。
 * <p>
 * 如果溢出额度耗尽，任务会交给 {@code fallbackHandler} 处理；默认使用
 * {@link ThreadPoolExecutor.CallerRunsPolicy}，通过提交线程执行任务形成背压。
 * 如果线程池已经关闭，则直接抛出 {@link RejectedExecutionException}，避免静默丢弃任务。
 * <p>
 * 注意：溢出线程不属于原始 {@link ThreadPoolExecutor} 管理范围，因此不会参与线程池的生命周期、
 * 统计指标、{@code beforeExecute}/{@code afterExecute} 回调和统一关闭流程。该策略适合短时突发流量兜底，
 * 不应作为长期过载的容量扩展手段。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/12
 */
@Log
public class BoundedOverflowThreadRejectedExecutionHandler implements RejectedExecutionHandler {
	/**
	 * 默认溢出线程数量。
	 * <p>
	 * 使用可用处理器数量作为保守上限，既允许短时兜底，也避免默认配置下无限放大线程数。
	 */
	public static final int DEFAULT_MAX_OVERFLOW_THREADS = Math.max(1, Runtime.getRuntime().availableProcessors());

	private final String jobName;
	private final int maxOverflowThreads;
	private final Semaphore overflowPermits;
	private final ThreadFactory threadFactory;
	private final RejectedExecutionHandler fallbackHandler;

	/**
	 * 创建有界溢出线程拒绝策略。
	 *
	 * @param jobName 任务名称，用于线程命名与日志定位
	 */
	public BoundedOverflowThreadRejectedExecutionHandler(String jobName) {
		this(jobName, DEFAULT_MAX_OVERFLOW_THREADS);
	}

	/**
	 * 创建有界溢出线程拒绝策略。
	 *
	 * @param jobName            任务名称，用于线程命名与日志定位
	 * @param maxOverflowThreads 最大溢出线程数量
	 */
	public BoundedOverflowThreadRejectedExecutionHandler(String jobName, int maxOverflowThreads) {
		this(jobName, maxOverflowThreads, defaultThreadFactory(jobName), new ThreadPoolExecutor.CallerRunsPolicy());
	}

	/**
	 * 创建有界溢出线程拒绝策略。
	 *
	 * @param jobName            任务名称，用于线程命名与日志定位
	 * @param maxOverflowThreads 最大溢出线程数量
	 * @param fallbackHandler    溢出额度耗尽时使用的兜底拒绝策略
	 */
	public BoundedOverflowThreadRejectedExecutionHandler(
			String jobName,
			int maxOverflowThreads,
			RejectedExecutionHandler fallbackHandler
	) {
		this(jobName, maxOverflowThreads, defaultThreadFactory(jobName), fallbackHandler);
	}

	/**
	 * 创建有界溢出线程拒绝策略。
	 *
	 * @param jobName            任务名称，用于线程命名与日志定位
	 * @param maxOverflowThreads 最大溢出线程数量
	 * @param threadFactory      溢出线程工厂
	 * @param fallbackHandler    溢出额度耗尽时使用的兜底拒绝策略
	 */
	public BoundedOverflowThreadRejectedExecutionHandler(
			String jobName,
			int maxOverflowThreads,
			ThreadFactory threadFactory,
			RejectedExecutionHandler fallbackHandler
	) {
		this.jobName = requireJobName(jobName);
		if (maxOverflowThreads <= 0) {
			throw new IllegalArgumentException("maxOverflowThreads must be positive");
		}
		this.maxOverflowThreads = maxOverflowThreads;
		this.overflowPermits = new Semaphore(maxOverflowThreads);
		this.threadFactory = Objects.requireNonNull(threadFactory, "threadFactory");
		this.fallbackHandler = Objects.requireNonNull(fallbackHandler, "fallbackHandler");
	}

	private static ThreadFactory defaultThreadFactory(String jobName) {
		return DefaultThreadFactory.builder(requireJobName(jobName) + "-Overflow").build();
	}

	private static String requireJobName(String jobName) {
		if (CharSequenceUtil.isBlank(jobName)) {
			throw new IllegalArgumentException("jobName must not be blank");
		}
		return StringUtil.trim(jobName);
	}

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		Objects.requireNonNull(r, "r");
		Objects.requireNonNull(executor, "executor");
		if (executor.isShutdown()) {
			throw new RejectedExecutionException("Executor has been shut down");
		}
		if (!overflowPermits.tryAcquire()) {
			if (log.isLoggable(Level.WARNING)) {
				log.log(Level.WARNING,
						"ThreadPool[{0}] overload, job[{1}] has used all overflow threads[{2}], fallback handler will process task[{3}].",
						new Object[]{executor, jobName, maxOverflowThreads, r});
			}
			fallbackHandler.rejectedExecution(r, executor);
			return;
		}

		this.startOverflowThread(r, executor);
	}

	/**
	 * 启动溢出线程。
	 * <p>
	 * permit 必须在任务执行结束或启动失败时释放，避免短时失败导致后续拒绝任务永久进入兜底策略。
	 */
	private void startOverflowThread(Runnable task, ThreadPoolExecutor executor) {
		Runnable overflowTask = () -> {
			try {
				task.run();
			} finally {
				overflowPermits.release();
			}
		};
		Thread thread;
		try {
			thread = threadFactory.newThread(overflowTask);
		} catch (Exception e) {
			overflowPermits.release();
			throw new RejectedExecutionException("Failed to create overflow thread", e);
		}
		if (Objects.isNull(thread)) {
			overflowPermits.release();
			fallbackHandler.rejectedExecution(task, executor);
			return;
		}

		if (log.isLoggable(Level.FINE)) {
			log.log(Level.FINE, "Create overflow thread[{0}] for job[{1}].", new Object[]{thread.getName(), jobName});
		}
		boolean started = false;
		try {
			thread.start();
			started = true;
		} finally {
			if (!started) {
				overflowPermits.release();
			}
		}
	}
}
