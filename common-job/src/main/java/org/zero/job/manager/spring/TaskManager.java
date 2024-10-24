package org.zero.job.manager.spring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.zero.job.manager.BaseTaskManager;
import org.zero.job.model.FutureBean;
import org.zero.job.model.ScheduledFutureBean;

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
    public boolean trigger(String key, Runnable task) {
        // 如果存在该任务先停止
        stop(key);
        Future<?> future = threadPoolTaskScheduler.submit(task);
        FutureBean triggeredTaskHolder = FutureBean.builder()
                .future(future)
                .clazz(task.getClass())
                .build();
        taskMap.put(key, triggeredTaskHolder);
        log.info("The triggered task[{}] started successfully", key);
        return true;
    }

    /**
     * 运行调度任务
     */
    @Override
    public boolean schedule(String key, Runnable task, String cron) {
        // 如果存在该任务先停止
        stop(key);
        ScheduledFuture<?> scheduledFuture = threadPoolTaskScheduler.schedule(task, new CronTrigger(cron));
        ScheduledFutureBean scheduledTaskHolder = ScheduledFutureBean.builder()
                .future(scheduledFuture)
                .clazz(task.getClass())
                .corn(cron)
                .build();
        taskMap.put(key, scheduledTaskHolder);
        log.info("The scheduled task[{}] starts successfully using [{}]", key, cron);
        return true;
    }
}
