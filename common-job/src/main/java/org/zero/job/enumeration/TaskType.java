package org.zero.job.enumeration;

/**
 * 任务类型枚举。
 *
 * @author zero
 * @since 2023/3/29
 */
public enum TaskType {
    /**
     * 触发任务。
     */
    TRIGGERED_TASK,
    /**
     * 定时任务。
     */
    SCHEDULED_TASK,
    /**
     * 不存在任务。
     */
    NONE,
    /**
     * 未知任务。
     */
    UNKNOWN,
    ;
}
