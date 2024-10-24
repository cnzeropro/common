package org.zero.job.manager;

import lombok.extern.slf4j.Slf4j;
import org.zero.job.model.FutureBean;
import org.zero.job.model.ScheduledFutureBean;
import org.zero.job.model.TaskType;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/23
 */
@Slf4j
public abstract class BaseTaskManager {
    protected static final ConcurrentMap<String, FutureBean> taskMap = new ConcurrentHashMap<>();

    /**
     * 运行触发任务
     */
    public abstract boolean trigger(String key, Runnable task);

    /**
     * 运行调度任务
     */
    public abstract boolean schedule(String key, Runnable task, String cron);

    /**
     * 停止任务
     */
    public boolean stop(String key) {
        FutureBean futureBean = get(key);
        if (Objects.nonNull(futureBean)) {
            Future<?> future = futureBean.getFuture();
            if (Objects.nonNull(future)) {
                future.cancel(true);
            }
            log.info("The task[{}] stopped successfully", key);
        }
        return true;
    }

    /**
     * 任务是否在运行
     */
    public boolean isRunning(String key) {
        FutureBean futureBean = get(key);
        if (Objects.nonNull(futureBean)) {
            Future<?> future = futureBean.getFuture();
            if (Objects.nonNull(future)) {
                return !future.isDone() && !future.isCancelled();
            }
        }
        return false;
    }

    /**
     * 获取任务类型
     */
    public TaskType getTaskType(String key) {
        FutureBean futureBean = get(key);
        if (Objects.isNull(futureBean)) {
            return TaskType.NONE;
        }
        if (futureBean instanceof ScheduledFutureBean) {
            return TaskType.SCHEDULED_TASK;
        }
        return TaskType.TRIGGERED_TASK;
    }

    /**
     * 获取任务信息
     */
    public FutureBean get(String key) {
        return taskMap.get(key);
    }

    /**
     * 获取任务总数量
     */
    public int count() {
        return taskMap.size();
    }
}
