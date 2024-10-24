package org.zero.job.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author zero
 * @since 2023/3/29
 */
@Getter
@RequiredArgsConstructor
public enum TaskType {
    /**
     * 触发任务
     */
    TRIGGERED_TASK,
    /**
     * 定时任务
     */
    SCHEDULED_TASK,
    /**
     * 无任务
     */
    NONE,
    /**
     * 未知
     */
    UNKNOWN,
    ;
}
