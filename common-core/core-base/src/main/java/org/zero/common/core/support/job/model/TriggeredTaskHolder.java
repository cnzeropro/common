package org.zero.common.core.support.job.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 触发任务注册信息的公共基类。
 *
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/9/21
 */
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TriggeredTaskHolder extends FutureHolder {
}
