package org.zero.job.manager.java;

import lombok.Setter;
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
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/23
 */
public class ReschedulingRunnable implements RunnableScheduledFuture<Object> {
    protected final Object locker = new Object();

    protected final Runnable delegate;
    protected final ScheduledExecutorService executor;
    protected final CronExpression cron;

    @Setter
    protected Date startTime;
    @Setter
    protected Date endDate;
    private ScheduledFuture<?> currentFuture;


    public ReschedulingRunnable(ScheduledExecutorService executor, Runnable runnable, CronExpression cron) {
        this(executor, runnable, cron, new Date());
    }

    public ReschedulingRunnable(ScheduledExecutorService executor, Runnable runnable, CronExpression cron, Date startTime) {
        this(executor, runnable, cron, startTime, null);
    }

    public ReschedulingRunnable(ScheduledExecutorService executor, Runnable runnable, CronExpression cron, Date startTime, Date endDate) {
        this.delegate = runnable;
        this.executor = executor;
        this.cron = cron;
        this.startTime = startTime;
        this.endDate = endDate;
    }

    public ReschedulingRunnable schedule() {
        synchronized (this.locker) {
            Date nextDate = this.cron.getNextValidTimeAfter(this.startTime);
            if (Objects.nonNull(endDate) && endDate.before(nextDate)) {
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
        delegate.run();
        synchronized (this.locker) {
            if (!isCancelled()) {
                schedule();
            }
        }
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        ScheduledFuture<?> curr;
        synchronized (this.locker) {
            curr = obtainCurrentFuture();
        }
        return curr.get();
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        ScheduledFuture<?> curr;
        synchronized (this.locker) {
            curr = obtainCurrentFuture();
        }
        return curr.get(timeout, unit);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        synchronized (this.locker) {
            return obtainCurrentFuture().cancel(mayInterruptIfRunning);
        }
    }

    @Override
    public boolean isCancelled() {
        synchronized (this.locker) {
            return obtainCurrentFuture().isDone();
        }
    }

    @Override
    public boolean isDone() {
        synchronized (this.locker) {
            return obtainCurrentFuture().isDone();
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
            curr = obtainCurrentFuture();
        }
        return curr.getDelay(unit);
    }

    @Override
    public int compareTo(Delayed other) {
        if (this == other) {
            return 0;
        }
        long diff = getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS);
        if (diff != 0) {
            return diff < 0 ? -1 : 1;
        }
        return 0;
    }
}
