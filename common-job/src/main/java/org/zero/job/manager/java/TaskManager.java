package org.zero.job.manager.java;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.zero.job.manager.BaseTaskManager;
import org.zero.job.manager.java.cron.CronExpression;
import org.zero.job.model.FutureHolder;
import org.zero.job.model.ScheduledTaskHolder;
import org.zero.job.model.TriggeredCallableTaskHolder;
import org.zero.job.model.TriggeredRunnableTaskHolder;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 动态任务管理类（java 原生 api 实现）
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/7/20
 */
@Slf4j
@RequiredArgsConstructor
public class TaskManager extends BaseTaskManager {
    protected final ScheduledExecutorService scheduledExecutorService;

    /**
     * 运行触发任务
     */
    @Override
    public void trigger(Serializable key, Runnable task) {
        if (this.isRunning(key)) {
            throw new IllegalStateException(String.format("The task[%s] is running", key));
        }
        Future<?> future = scheduledExecutorService.submit(task);
        FutureHolder futureHolder = TriggeredRunnableTaskHolder.builder()
                .future(future)
                .task(task)
                .build();
        taskMap.put(key, futureHolder);
        if (log.isDebugEnabled()) {
            log.debug("The triggered task[{}] started successfully", key);
        }
    }

    @Override
    public <T> Future<T> trigger(Serializable key, Callable<T> task) {
        if (this.isRunning(key)) {
            throw new IllegalStateException(String.format("The task[%s] is running", key));
        }
        Future<T> future = scheduledExecutorService.submit(task);
        FutureHolder futureHolder = TriggeredCallableTaskHolder.builder()
                .future(future)
                .task(task)
                .build();
        taskMap.put(key, futureHolder);
        if (log.isDebugEnabled()) {
            log.debug("The triggered task[{}] started successfully", key);
        }
        return future;
    }

    /**
     * 运行调度任务
     */
    @Override
    @SneakyThrows
    public void schedule(Serializable key, Runnable task, String cron) {
        if (this.isRunning(key)) {
            throw new IllegalStateException(String.format("The task[%s] is running", key));
        }
        ReschedulingRunnable scheduledFuture = new ReschedulingRunnable(scheduledExecutorService, task, new CronExpression(cron)).schedule();
        FutureHolder futureHolder = ScheduledTaskHolder.builder()
                .future(scheduledFuture)
                .task(task)
                .corn(cron)
                .build();
        taskMap.put(key, futureHolder);
        if (log.isDebugEnabled()) {
            log.debug("The scheduled task[{}] started successfully using [{}]", key, cron);
        }
    }
}
