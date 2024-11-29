package org.zero.job.manager;

import lombok.extern.slf4j.Slf4j;
import org.zero.job.enumeration.TaskType;
import org.zero.job.model.FutureHolder;
import org.zero.job.model.ScheduledTaskHolder;
import org.zero.job.model.TriggeredCallableTaskHolder;
import org.zero.job.model.TriggeredRunnableTaskHolder;
import org.zero.job.model.TriggeredTaskHolder;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/23
 */
@Slf4j
public abstract class BaseTaskManager {
    protected static final ConcurrentMap<Serializable, FutureHolder> taskMap = new ConcurrentHashMap<>();

    /**
     * 运行触发任务
     */
    public abstract void trigger(Serializable key, Runnable task);

    /**
     * 运行触发任务
     */
    public <T> Future<T> trigger(Serializable key, Runnable task, T result) {
        Callable<T> callable = Executors.callable(task, result);
        return trigger(key, callable);
    }

    /**
     * 运行触发任务
     */
    public abstract <T> Future<T> trigger(Serializable key, Callable<T> task);

    /**
     * 重新触发任务
     * <p>
     * 注意：如果当前任务正在运行中，则会先停止当前任务，再重新启动
     */
    public void reTrigger(Serializable key) {
        stop(key);
        reTrigger(key, key);
    }

    /**
     * 重新触发任务
     * <p>
     * 保证旧任务的状态下，运行一个新的任务
     */
    public void reTrigger(Serializable key, Serializable newKey) {
        FutureHolder futureHolder = this.get(key);
        if (Objects.isNull(futureHolder)) {
            throw new IllegalStateException(String.format("The task[%s] is not exist", key));
        }
        if (!(futureHolder instanceof TriggeredTaskHolder)) {
            throw new IllegalStateException(String.format("The task[%s] is not triggered task", key));
        }
        if (futureHolder instanceof TriggeredRunnableTaskHolder) {
            TriggeredRunnableTaskHolder triggeredRunnableTaskHolder = (TriggeredRunnableTaskHolder) futureHolder;
            trigger(newKey, triggeredRunnableTaskHolder.getTask());
        } else if (futureHolder instanceof TriggeredCallableTaskHolder) {
            TriggeredCallableTaskHolder triggeredCallableTaskHolder = (TriggeredCallableTaskHolder) futureHolder;
            trigger(newKey, triggeredCallableTaskHolder.getTask());
        } else {
            throw new IllegalStateException(String.format("The task[%s] is not supported", key));
        }
    }

    /**
     * 运行调度任务
     */
    public abstract void schedule(Serializable key, Runnable task, String cron);

    /**
     * 重新调度任务
     * <p>
     * 注意：如果当前任务正在运行中，则会先停止当前任务，再重新启动
     */
    public void reSchedule(Serializable key) {
        stop(key);
        reSchedule(key, key);
    }

    /**
     * 重新调度任务
     * <p>
     * 保证旧任务的状态下，运行一个新的任务
     */
    public void reSchedule(Serializable key, Serializable newKey) {
        FutureHolder futureHolder = this.get(key);
        if (Objects.isNull(futureHolder)) {
            throw new IllegalStateException(String.format("The task[%s] is not exist", key));
        }
        if (!(futureHolder instanceof ScheduledTaskHolder)) {
            throw new IllegalStateException(String.format("The task[%s] is not scheduled task", key));
        }
        ScheduledTaskHolder scheduledTaskHolder = (ScheduledTaskHolder) futureHolder;
        schedule(newKey, scheduledTaskHolder.getTask(), scheduledTaskHolder.getCorn());
    }

    /**
     * 重新调度任务
     * <p>
     * 注意：如果当前任务正在运行中，则会先停止当前任务，再重新启动
     */
    public void reSchedule(Serializable key, String cron) {
        stop(key);
        reSchedule(key, key, cron);
    }

    /**
     * 重新调度任务
     * <p>
     * 保证旧任务的状态下，使用指定的 cron 表达式运行一个新的任务
     */
    public void reSchedule(Serializable key, Serializable newKey, String cron) {
        FutureHolder futureHolder = this.get(key);
        if (Objects.isNull(futureHolder)) {
            throw new IllegalStateException(String.format("The task[%s] is not exist", key));
        }
        if (!(futureHolder instanceof ScheduledTaskHolder)) {
            throw new IllegalStateException(String.format("The task[%s] is not scheduled task", key));
        }
        ScheduledTaskHolder scheduledTaskHolder = (ScheduledTaskHolder) futureHolder;
        schedule(newKey, scheduledTaskHolder.getTask(), cron);
    }

    /**
     * 重新运行任务
     * <p>
     * 注意：如果当前任务正在运行中，则会先停止当前任务，再重新启动
     */
    public void reRun(Serializable key) {
        reRun(key, key);
    }

    /**
     * 重新运行任务
     * <p>
     * 保证旧任务的状态下，运行一个新的任务
     */
    public void reRun(Serializable key, Serializable newKey) {
        TaskType taskType = this.getTaskType(key);
        if (taskType == TaskType.TRIGGERED_TASK) {
            reTrigger(key, newKey);
        } else if (taskType == TaskType.SCHEDULED_TASK) {
            reSchedule(key, newKey);
        } else {
            throw new IllegalStateException(String.format("The task[%s] is not supported, task type: %s", key, taskType));
        }
    }

    /**
     * 停止任务
     * <p>
     * 注意：此方法只停止任务，不会删除任务。如需删除，请使用{@link #remove(Serializable)}
     */
    public void stop(Serializable key) {
        stop(key, true);
    }

    /**
     * 停止任务
     * <p>
     * 注意：此方法只停止任务，不会删除任务。如需删除，请使用{@link #remove(Serializable)}
     */
    public void stop(Serializable key, boolean mayInterruptIfRunning) {
        Future<?> future = getTaskFuture(key);
        if (Objects.nonNull(future)) {
            future.cancel(mayInterruptIfRunning);
        }
        if (log.isDebugEnabled()) {
            log.debug("The task[{}] stopped successfully", key);
        }
    }

    /**
     * 删除任务
     */
    public void remove(Serializable key) {
        taskMap.remove(key);
    }

    /**
     * 检查任务是否在运行中
     */
    public boolean isRunning(Serializable key) {
        Future<?> future = getTaskFuture(key);
        if (Objects.nonNull(future)) {
            return !future.isDone() && !future.isCancelled();
        }
        return false;
    }

    /**
     * 获取任务{@link Future}
     */
    public Future<?> getTaskFuture(Serializable key) {
        FutureHolder futureHolder = get(key);
        if (Objects.nonNull(futureHolder)) {
            return futureHolder.getFuture();
        }
        return null;
    }

    /**
     * 获取任务类型
     *
     * @see TaskType
     */
    public TaskType getTaskType(Serializable key) {
        FutureHolder futureHolder = get(key);
        if (Objects.isNull(futureHolder)) {
            return TaskType.NONE;
        }
        if (futureHolder instanceof ScheduledTaskHolder) {
            return TaskType.SCHEDULED_TASK;
        }
        if (futureHolder instanceof TriggeredTaskHolder) {
            return TaskType.TRIGGERED_TASK;
        }
        return TaskType.UNKNOWN;
    }

    /**
     * 获取任务信息
     */
    public FutureHolder get(Serializable key) {
        return taskMap.get(key);
    }

    /**
     * 获取任务总数量。已注册的所有任务，包括运行中、完成、取消等状态的触发和定时任务
     */
    public int count() {
        return taskMap.size();
    }
}
