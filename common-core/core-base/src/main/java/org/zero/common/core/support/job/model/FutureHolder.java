package org.zero.common.core.support.job.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.concurrent.Future;

/**
 * 任务注册信息的公共基类。
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/9/21
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FutureHolder {
	/**
	 * 底层执行句柄，用于查询运行状态和取消任务。
	 */
	private Future<?> future;
}
