package org.zero.common.core.extension.redisson.api.stream;

/**
 * 标记当前处理器每轮可能快速返回，需要消费循环在轮次之间自行退避。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
public interface PollingMessageProcessor extends MessageProcessor {
}
