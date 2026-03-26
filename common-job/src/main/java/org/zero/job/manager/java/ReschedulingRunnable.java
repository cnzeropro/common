package org.zero.job.manager.java;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.zero.job.manager.java.cron.CronExpression;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 支持 cron 续调的 {@link RunnableScheduledFuture} 包装器。
 * <p>
 * 每次执行完成后都会重新计算下一次触发时间，并根据异常策略决定是否继续后续调度。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/23
 */
@Slf4j
public class ReschedulingRunnable implements RunnableScheduledFuture<Object> {
	protected final Object locker = new Object();

	protected final Runnable delegate;
	protected final ScheduledExecutorService executor;
	protected final CronExpression cron;
	protected final boolean continueAfterException;

	@Setter
	protected Date startTime;
	@Setter
	protected Date endDate;
	private ScheduledFuture<?> currentFuture;
	/**
	 * 标记是否由外部显式取消。
	 */
	private volatile boolean cancelled;
	/**
	 * 标记调度链是否因异常策略、自然结束等原因终止。
	 */
	private volatile boolean terminated;

	public ReschedulingRunnable(ScheduledExecutorService executor, Runnable runnable, CronExpression cron) {
		this(executor, runnable, cron, true);
	}

	public ReschedulingRunnable(ScheduledExecutorService executor, Runnable runnable, CronExpression cron, boolean continueAfterException) {
		this(executor, runnable, cron, continueAfterException, new Date(), null);
	}

	public ReschedulingRunnable(ScheduledExecutorService executor, Runnable runnable, CronExpression cron, Date startTime) {
		this(executor, runnable, cron, true, startTime, null);
	}

	public ReschedulingRunnable(ScheduledExecutorService executor, Runnable runnable, CronExpression cron, boolean continueAfterException, Date startTime) {
		this(executor, runnable, cron, continueAfterException, startTime, null);
	}

	public ReschedulingRunnable(ScheduledExecutorService executor, Runnable runnable, CronExpression cron, Date startTime, Date endDate) {
		this(executor, runnable, cron, true, startTime, endDate);
	}

	private ReschedulingRunnable(
		ScheduledExecutorService executor,
		Runnable runnable,
		CronExpression cron,
		boolean continueAfterException,
		Date startTime,
		Date endDate
	) {
		this.delegate = runnable;
		this.executor = executor;
		this.cron = cron;
		this.continueAfterException = continueAfterException;
		this.startTime = startTime;
		this.endDate = endDate;
	}

	/**
	 * 计算下一次触发时间，并重新向线程池注册当前包装器。
	 */
	public ReschedulingRunnable schedule() {
		synchronized (this.locker) {
			if (this.cancelled || this.terminated) {
				return this;
			}
			// 每次都重新按 cron 计算下一次触发时间，而不是固定频率递推。
			Date nextDate = this.cron.getNextValidTimeAfter(this.startTime);
			if (Objects.isNull(nextDate)) {
				this.terminated = true;
				log.debug("Scheduled task chain finished, reason=no-next-fire-time");
				return this;
			}
			if (Objects.nonNull(this.endDate) && this.endDate.before(nextDate)) {
				this.terminated = true;
				log.debug("Scheduled task chain finished, reason=end-date-reached");
				return this;
			}
			long delay = nextDate.getTime() - this.startTime.getTime();
			this.currentFuture = this.executor.schedule(this, delay, TimeUnit.MILLISECONDS);
			this.startTime = nextDate;
			return this;
		}
	}

	protected ScheduledFuture<?> obtainCurrentFuture() {
		Objects.requireNonNull(this.currentFuture, "No current future");
		return this.currentFuture;
	}

	@Override
	public void run() {
		boolean shouldScheduleNext = true;
		try {
			this.delegate.run();
		} catch (Throwable throwable) {
			// 是否记录任务上下文由外层调度器决定，这里只关心续调策略。
			shouldScheduleNext = this.continueAfterException;
			if (!shouldScheduleNext) {
				this.terminated = true;
			}
		}
		synchronized (this.locker) {
			if (!this.cancelled && !this.terminated && shouldScheduleNext) {
				this.schedule();
			}
		}
	}

	@Override
	public Object get() throws InterruptedException, ExecutionException {
		ScheduledFuture<?> curr;
		synchronized (this.locker) {
			curr = this.obtainCurrentFuture();
		}
		return curr.get();
	}

	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		ScheduledFuture<?> curr;
		synchronized (this.locker) {
			curr = this.obtainCurrentFuture();
		}
		return curr.get(timeout, unit);
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		synchronized (this.locker) {
			this.cancelled = true;
			if (this.currentFuture == null) {
				return false;
			}
			return this.currentFuture.cancel(mayInterruptIfRunning);
		}
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public boolean isDone() {
		synchronized (this.locker) {
			// 对外暴露“已结束”，既包括显式取消，也包括按策略终止或自然结束。
			if (this.cancelled || this.terminated) {
				return true;
			}
			if (this.currentFuture == null) {
				return false;
			}
			return this.currentFuture.isDone();
		}
	}

	@Override
	public boolean isPeriodic() {
		return true;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		ScheduledFuture<?> curr;
		synchronized (this.locker) {
			curr = this.obtainCurrentFuture();
		}
		return curr.getDelay(unit);
	}

	@Override
	public int compareTo(Delayed other) {
		if (this == other) {
			return 0;
		}
		long diff = this.getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
		if (diff != 0) {
			return diff < 0 ? -1 : 1;
		}
		return 0;
	}
}
