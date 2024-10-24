package org.zero.job.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.concurrent.Callable;

/**
 * @author Zero (cnzeropro@qq.com)
 * @since 2022/9/21
 */
@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class TriggeredCallableTaskHolder extends TriggeredTaskHolder {
    private Callable<?> task;
}
