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

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 动态任务管理类（java原生api实现）
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
    public boolean trigger(String key, Runnable task) {
        // 如果存在该任务先停止
        stop(key);
        Future<?> future = scheduledExecutorService.submit(task);
        FutureHolder futureHolder = TriggeredRunnableTaskHolder.builder()
                .future(future)
                .taskClass(task.getClass())
                .task(task)
                .build();
        taskMap.put(key, futureHolder);
        log.info("The triggered task[{}] started successfully", key);
        return true;
    }

    @Override
    public <T> Future<T> trigger(String key, Callable<T> task) {
        // 如果存在该任务先停止
        stop(key);
        Future<T> future = scheduledExecutorService.submit(task);
        FutureHolder futureHolder = TriggeredCallableTaskHolder.builder()
                .future(future)
                .taskClass(task.getClass())
                .task(task)
                .build();
        taskMap.put(key, futureHolder);
        log.info("The triggered task[{}] started successfully", key);
        return future;
    }

    /**
     * 运行调度任务
     */
    @Override
    @SneakyThrows
    public boolean schedule(String key, Runnable task, String cron) {
        // 如果存在该任务先停止
        stop(key);
        ReschedulingRunnable scheduledFuture = new ReschedulingRunnable(scheduledExecutorService, task, new CronExpression(cron));
        scheduledFuture.schedule();
        FutureHolder futureHolder = ScheduledTaskHolder.builder()
                .future(scheduledFuture)
                .taskClass(task.getClass())
                .task(task)
                .corn(cron)
                .build();
        taskMap.put(key, futureHolder);
        log.info("The scheduled task[{}] starts successfully using [{}]", key, cron);
        return true;
    }
}
