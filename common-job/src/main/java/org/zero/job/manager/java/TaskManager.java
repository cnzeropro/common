package org.zero.job.manager.java;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.zero.job.manager.java.cron.CronExpression;
import org.zero.job.model.FutureBean;
import org.zero.job.model.ScheduledFutureBean;
import org.zero.job.model.TaskType;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 动态任务管理类（java原生api实现）
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/7/20
 */
@Slf4j
@RequiredArgsConstructor
public class TaskManager {
    private static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private static final ConcurrentMap<String, FutureBean> triggeredTaskMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, ScheduledFutureBean> scheduledTaskMap = new ConcurrentHashMap<>();

    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    /**
     * 启动任务
     */
    public synchronized boolean startTask(String taskId, String cron, Runnable task) {
        // 如果存在该任务先停止
        if (Objects.nonNull(getTask(taskId))) {
            stopTask(taskId);
        }

        if (Objects.nonNull(cron)) {
            if (!CronExpression.isValidExpression(cron)) {
                log.warn("Task[{}] cron[{}] is incorrect, skipped", taskId, cron);
                return false;
            }
            Callable<ScheduledFuture<?>> callable = wrap(cron, task);
            ScheduledFuture<ScheduledFuture<?>> scheduledFuture = scheduledExecutor.schedule(callable, 0, TimeUnit.MILLISECONDS);
            ScheduledFutureBean scheduledTaskHolder = ScheduledFutureBean.builder()
                    .future(scheduledFuture)
                    .clazz(task.getClass())
                    .corn(cron)
                    .build();
            scheduledTaskMap.put(taskId, scheduledTaskHolder);
            log.info("The scheduled task[{}] starts successfully using [{}]", taskId, cron);
        } else {
            Future<?> future = scheduledThreadPoolExecutor.submit(task);
            FutureBean triggeredTaskHolder = FutureBean.builder()
                    .future(future)
                    .clazz(task.getClass())
                    .build();
            triggeredTaskMap.put(taskId, triggeredTaskHolder);
            log.info("The triggered task[{}] started successfully", taskId);
        }
        return true;
    }

    /**
     * 停止任务
     */
    public synchronized boolean stopTask(String taskId) {
        if (triggeredTaskMap.containsKey(taskId)) {
            log.info("The triggered task exists, try to stop...");
            Future<?> future = triggeredTaskMap.get(taskId).getFuture();
            if (Objects.nonNull(future)) {
                future.cancel(true);
            }
            triggeredTaskMap.remove(taskId);
        }
        if (scheduledTaskMap.containsKey(taskId)) {
            log.info("The scheduled task exists, try to stop...");
            Future<?> future = scheduledTaskMap.get(taskId).getFuture();
            if (Objects.nonNull(future)) {
                future.cancel(true);
            }
            scheduledTaskMap.remove(taskId);
        }
        log.info("The task[{}] stopped successfully", taskId);
        return true;
    }

    /**
     * 任务是否在运行
     */
    public synchronized boolean isRunning(String taskId) {
        if (triggeredTaskMap.containsKey(taskId)) {
            Future<?> future = triggeredTaskMap.get(taskId).getFuture();
            if (Objects.nonNull(future)) {
                return !future.isDone() && !future.isCancelled();
            }
        }
        if (scheduledTaskMap.containsKey(taskId)) {
            Future<?> future = scheduledTaskMap.get(taskId).getFuture();
            if (Objects.nonNull(future)) {
                return !future.isDone() && !future.isCancelled();
            }
        }
        return false;
    }

    /**
     * 获取任务类型
     */
    public synchronized TaskType getTaskType(String taskId) {
        if (triggeredTaskMap.containsKey(taskId)) {
            return TaskType.TRIGGERED_TASK;
        }
        if (scheduledTaskMap.containsKey(taskId)) {
            return TaskType.SCHEDULED_TASK;
        }
        return TaskType.NONE;
    }

    /**
     * 获取任务信息
     */
    public synchronized FutureBean getTask(String taskId) {
        TaskType taskType = getTaskType(taskId);
        if (taskType == TaskType.TRIGGERED_TASK) {
            return triggeredTaskMap.get(taskId);
        }
        if (taskType == TaskType.SCHEDULED_TASK) {
            return scheduledTaskMap.get(taskId);
        }
        return null;
    }

    /**
     * 查询指定的触发任务
     */
    public synchronized FutureBean getTriggeredTask(String taskId) {
        return triggeredTaskMap.get(taskId);
    }

    /**
     * 查询指定的定时任务
     */
    public synchronized ScheduledFutureBean getScheduledTask(String taskId) {
        return scheduledTaskMap.get(taskId);
    }

    /**
     * 获取当前触发任务总数量
     */
    public synchronized int countTriggeredTask() {
        return triggeredTaskMap.size();
    }

    /**
     * 获取当前定时任务总数量
     */
    public synchronized int countScheduledTask() {
        return scheduledTaskMap.size();
    }

    /**
     * 查询所有的触发任务
     */
    public synchronized Map<String, FutureBean> listTriggeredTask() {
        return triggeredTaskMap;
    }

    /**
     * 查询所有的定时任务
     */
    public synchronized Map<String, ScheduledFutureBean> listScheduledTask() {
        return scheduledTaskMap;
    }

    /**
     * 构建定时任务的守护任务
     */
    private Callable<ScheduledFuture<?>> wrap(String cron, Runnable task) {
        return () -> {
            CronExpression cronExpression = new CronExpression(cron);
            Date lastTime = new Date();
            ScheduledFuture<?> resultFuture = null;
            // 当前线程中断时，尝试退出循环
            while (!Thread.currentThread().isInterrupted()) {
                Date nextTime = cronExpression.getNextValidTimeAfter(lastTime);
                long milliseconds = nextTime.getTime() - lastTime.getTime();
                lastTime = nextTime;
                ScheduledFuture<?> scheduledFuture = scheduledThreadPoolExecutor.schedule(task, milliseconds, TimeUnit.MILLISECONDS);
                resultFuture = scheduledFuture;
                // 阻塞
                try {
                    // Thread.sleep(milliseconds);
                    scheduledFuture.get(milliseconds, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    log.warn("Task is interrupt", e);
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.warn("Task run failed", e);
                }
            }
            return resultFuture;
        };
    }
}
