package org.zero.job.manager;

import lombok.extern.slf4j.Slf4j;
import org.zero.job.model.FutureHolder;
import org.zero.job.model.ScheduledTaskHolder;
import org.zero.job.model.TaskType;
import org.zero.job.model.TriggeredTaskHolder;

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
    protected static final ConcurrentMap<String, FutureHolder> taskMap = new ConcurrentHashMap<>();

    /**
     * 运行触发任务
     */
    public abstract boolean trigger(String key, Runnable task);

    /**
     * 运行触发任务
     */
    public <T> Future<T> trigger(String key, Runnable task, T result) {
        Callable<T> callable = Executors.callable(task, result);
        return trigger(key, callable);
    }

    /**
     * 运行触发任务
     */
    public abstract <T> Future<T> trigger(String key, Callable<T> task);

    /**
     * 运行调度任务
     */
    public abstract boolean schedule(String key, Runnable task, String cron);

    /**
     * 停止任务
     */
    public boolean stop(String key) {
        return stop(key, true);
    }

    /**
     * 停止任务
     */
    public boolean stop(String key, boolean mayInterruptIfRunning) {
        Future<?> future = getTaskFuture(key);
        if (Objects.nonNull(future)) {
            future.cancel(mayInterruptIfRunning);
        }
        log.info("The task[{}] stopped successfully", key);
        return true;
    }

    /**
     * 任务是否在运行
     */
    public boolean isRunning(String key) {
        Future<?> future = getTaskFuture(key);
        if (Objects.nonNull(future)) {
            return !future.isDone() && !future.isCancelled();
        }

        return false;
    }

    /**
     * 获取任务{@link Future}
     */
    public Future<?> getTaskFuture(String key) {
        FutureHolder futureHolder = get(key);
        if (Objects.nonNull(futureHolder)) {
            return futureHolder.getFuture();
        }
        return null;
    }

    /**
     * 获取任务类型
     */
    public TaskType getTaskType(String key) {
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
    public FutureHolder get(String key) {
        return taskMap.get(key);
    }

    /**
     * 获取任务总数量
     */
    public int count() {
        return taskMap.size();
    }
}
