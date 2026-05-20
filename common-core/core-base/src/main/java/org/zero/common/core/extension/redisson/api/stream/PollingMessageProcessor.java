package org.zero.common.core.extension.redisson.api.stream;

/**
 * 标记当前处理器是轮询型处理器。
 * <p>
 * 轮询型处理器通常不会像阻塞读那样等待新消息，每轮可能很快返回；消费循环会据此在成功轮次后休眠退避，
 * 避免空转占用 CPU 或频繁访问 Redis。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/4/17
 */
public interface PollingMessageProcessor extends MessageProcessor {
}
