package org.zero.job.manager.spring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.zero.job.manager.BaseTaskManager;
import org.zero.job.model.ScheduledTaskHolder;
import org.zero.job.model.TriggeredCallableTaskHolder;
import org.zero.job.model.TriggeredRunnableTaskHolder;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 基于 Spring 调度器的任务管理器。
 * <p>
 * 依赖 Spring {@link ThreadPoolTaskScheduler} 提供触发任务和 cron 调度能力。
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/7/20
 */
@Slf4j
@RequiredArgsConstructor
public class TaskManager extends BaseTaskManager {
    protected final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    /**
     * 注册并执行一次性 Runnable 任务。
     */
    @Override
    public void trigger(Serializable key, Runnable task) {
        this.registerTask(key, () -> {
            Future<?> future = threadPoolTaskScheduler.submit(task);
            return TriggeredRunnableTaskHolder.builder()
                    .future(future)
                    .task(task)
                    .build();
        });
        if (log.isDebugEnabled()) {
            log.debug("Registered triggered task[{}]", key);
        }
    }

    @Override
    public <T> Future<T> trigger(Serializable key, Callable<T> task) {
        AtomicReference<Future<T>> futureReference = new AtomicReference<Future<T>>();
        this.registerTask(key, () -> {
            Future<T> future = threadPoolTaskScheduler.submit(task);
            futureReference.set(future);
            return TriggeredCallableTaskHolder.builder()
                    .future(future)
                    .task(task)
                    .build();
        });
        if (log.isDebugEnabled()) {
            log.debug("Registered triggered task[{}]", key);
        }
        return futureReference.get();
    }

    /**
     * 注册 cron 定时任务。
     */
    @Override
    public void schedule(Serializable key, Runnable task, String cron, boolean continueAfterException) {
        this.registerTask(key, () -> {
            AtomicReference<ScheduledFuture<?>> futureReference = new AtomicReference<ScheduledFuture<?>>();
            Runnable wrappedTask = () -> {
                try {
                    task.run();
                } catch (Throwable throwable) {
                    log.error(
                            "Scheduled task execution failed, key[{}], cron[{}], continueAfterException={}",
                            key,
                            cron,
                            continueAfterException,
                            throwable
                    );
                    if (!continueAfterException) {
                        log.warn(
                                "Scheduled task chain stopped after failure, key[{}], cron[{}], continueAfterException={}",
                                key,
                                cron,
                                continueAfterException
                        );
                        ScheduledFuture<?> future = futureReference.get();
                        if (future != null) {
                            // Spring 会继续维持 cron 调度，这里显式取消当前 future 才能终止后续触发。
                            future.cancel(false);
                        }
                    }
                }
            };
            // 包装原始任务，统一异常日志和“异常后是否继续调度”的策略。
            ScheduledFuture<?> scheduledFuture = threadPoolTaskScheduler.schedule(wrappedTask, new CronTrigger(cron));
            futureReference.set(scheduledFuture);
            return ScheduledTaskHolder.builder()
                    .future(scheduledFuture)
                    .task(task)
                    .cron(cron)
                    .continueAfterException(continueAfterException)
                    .build();
        });
        if (log.isDebugEnabled()) {
            log.debug(
                    "Registered scheduled task[{}] with cron[{}], continueAfterException={}",
                    key,
                    cron,
                    continueAfterException
            );
        }
    }
}
