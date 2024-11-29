package org.zero.job.manager.spring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.zero.job.manager.BaseTaskManager;
import org.zero.job.model.FutureHolder;
import org.zero.job.model.ScheduledTaskHolder;
import org.zero.job.model.TriggeredCallableTaskHolder;
import org.zero.job.model.TriggeredRunnableTaskHolder;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

/**
 * 动态任务管理类
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/7/20
 */
@Slf4j
@RequiredArgsConstructor
public class TaskManager extends BaseTaskManager {
    protected final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    /**
     * 运行触发任务
     */
    @Override
    public void trigger(Serializable key, Runnable task) {
        if (this.isRunning(key)) {
            throw new IllegalStateException(String.format("The task[%s] is running", key));
        }
        Future<?> future = threadPoolTaskScheduler.submit(task);
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
        Future<T> future = threadPoolTaskScheduler.submit(task);
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
    public void schedule(Serializable key, Runnable task, String cron) {
        if (this.isRunning(key)) {
            throw new IllegalStateException(String.format("The task[%s] is running", key));
        }
        ScheduledFuture<?> scheduledFuture = threadPoolTaskScheduler.schedule(task, new CronTrigger(cron));
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
