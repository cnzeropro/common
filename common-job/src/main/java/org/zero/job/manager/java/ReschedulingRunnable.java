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
    private final Object locker = new Object();

    private final Runnable runnable;
    private final ScheduledExecutorService executor;
    private final CronExpression cronExpression;

    @Setter
    private Date startTime;
    @Setter
    private Date endDate;
    private ScheduledFuture<?> currentFuture;


    public ReschedulingRunnable(ScheduledExecutorService executor, Runnable runnable, CronExpression cronExpression) {
        this(executor, runnable, cronExpression, new Date());
    }

    public ReschedulingRunnable(ScheduledExecutorService executor, Runnable runnable, CronExpression cronExpression, Date startTime) {
        this(executor, runnable, cronExpression, startTime, null);
    }

    public ReschedulingRunnable(ScheduledExecutorService executor, Runnable runnable, CronExpression cronExpression, Date startTime, Date endDate) {
        this.runnable = runnable;
        this.executor = executor;
        this.cronExpression = cronExpression;
        this.startTime = startTime;
        this.endDate = endDate;
    }

    protected ScheduledFuture<?> obtainCurrentFuture() {
        return this.currentFuture;
    }

    @Override
    public void run() {
        runnable.run();
        synchronized (this.locker) {
            if (!isCancelled()) {
                schedule();
            }
        }
    }

    public void schedule() {
        synchronized (this.locker) {
            Date nextDate = this.cronExpression.getNextValidTimeAfter(this.startTime);
            if (Objects.nonNull(endDate) && endDate.before(nextDate)) {
                return;
            }
            long delay = nextDate.getTime() - this.startTime.getTime();
            this.currentFuture = this.executor.schedule(this, delay, TimeUnit.MILLISECONDS);
            this.startTime = nextDate;
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
