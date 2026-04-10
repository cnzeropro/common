package org.zero.common.core.support.job.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 定时任务注册信息。
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/9/21
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ScheduledTaskHolder extends FutureHolder {
	/**
	 * 原始任务体。
	 */
	private Runnable task;

	/**
	 * cron 表达式。
	 */
	private String cron;

	/**
	 * 单次执行抛异常后，是否继续后续调度。
	 */
	private boolean continueAfterException;
}
