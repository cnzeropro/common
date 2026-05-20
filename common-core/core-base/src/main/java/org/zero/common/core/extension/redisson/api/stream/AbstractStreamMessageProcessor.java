package org.zero.common.core.extension.redisson.api.stream;

import org.redisson.api.RStream;
import org.redisson.api.StreamMessageId;

import java.util.Objects;

/**
 * Stream 消息处理器基类。
 * <p>
 * 统一保存 Stream、消费者组和消费者名称，并封装 XACK、XDEL 等底层动作。具体消息流程和日志语义由子类负责。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/20
 */
public abstract class AbstractStreamMessageProcessor<K, V> implements MessageProcessor {
	protected final RStream<K, V> stream;
	protected final String groupName;
	protected final String consumerName;

	protected AbstractStreamMessageProcessor(RStream<K, V> stream, String groupName, String consumerName) {
		this.stream = Objects.requireNonNull(stream, "stream must not be null");
		this.groupName = Objects.requireNonNull(groupName, "groupName must not be null");
		this.consumerName = Objects.requireNonNull(consumerName, "consumerName must not be null");
	}

	/**
	 * 使用普通 XACK 确认消息。
	 * <p>
	 * 这里只移除消费者组 pending 记录，不删除 Stream 中的消息实体。
	 */
	protected long ack(StreamMessageId messageId) {
		return stream.ack(groupName, messageId);
	}

	/**
	 * 确认并删除消息。
	 * <p>
	 * 先使用普通 XACK 移除当前消费者组 pending 记录，再使用 XDEL 删除 Stream 消息实体。
	 */
	protected long ackAndDelete(StreamMessageId messageId) {
		this.ack(messageId);
		return stream.remove(messageId);
	}
}
