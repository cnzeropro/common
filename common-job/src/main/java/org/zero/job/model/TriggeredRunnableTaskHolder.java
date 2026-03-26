package org.zero.job.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 触发型 Runnable 任务注册信息。
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/9/21
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TriggeredRunnableTaskHolder extends TriggeredTaskHolder {
    /**
     * 供重新触发时复用的原始任务体。
     */
    private Runnable task;
}
