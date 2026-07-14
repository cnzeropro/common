package org.zero.common.core.extension.redisson.api.stream;

import org.redisson.api.RStream;
import org.redisson.api.StreamMessageId;

import java.util.Objects;

/**
 * 消息处理器基类。
 * <p>
 * 统一保存 Stream、消费者组和消费者名称，并封装 XACK、XDEL 等底层动作。具体读取策略、消息分类和日志语义由子类负责。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/20
 */
public abstract class AbstractMessageProcessor<K, V> implements MessageProcessor {
	protected final RStream<K, V> stream;
	protected final String groupName;
	protected final String consumerName;

	protected AbstractMessageProcessor(RStream<K, V> stream, String groupName, String consumerName) {
		this.stream = Objects.requireNonNull(stream, "stream must not be null");
		this.groupName = Objects.requireNonNull(groupName, "groupName must not be null");
		this.consumerName = Objects.requireNonNull(consumerName, "consumerName must not be null");
	}

	/**
	 * 调用消息处理器，并校验处理结果。
	 *
	 * @param messageHandler 消息处理器
	 * @param context        消息上下文
	 * @return 消息后续动作
	 */
	protected MessageAction handleMessage(
			MessageHandler<K, V> messageHandler,
			MessageContext<K, V> context
	) {
		Objects.requireNonNull(messageHandler, "message handler must not be null");
		Objects.requireNonNull(context, "context must not be null");
		return Objects.requireNonNull(messageHandler.handle(context), "message action must not be null");
	}

	/**
	 * 执行消息后续动作。
	 * <p>
	 * 该方法只封装底层确认或删除操作，日志语义仍由子类按消息类型决定。
	 *
	 * @param messageId 消息 ID
	 * @param action    消息后续动作
	 * @return 底层动作影响的消息数量；保留 pending 时返回 {@code 0}
	 */
	protected long executeAction(StreamMessageId messageId, MessageAction action) {
		Objects.requireNonNull(messageId, "messageId must not be null");
		Objects.requireNonNull(action, "action must not be null");
		switch (action) {
			case ACK:
				return this.ack(messageId);
			case ACK_AND_DELETE:
				return this.ackAndDelete(messageId);
			case KEEP_PENDING:
			default:
				return 0;
		}
	}

	/**
	 * 使用 XACK 确认消息。
	 * <p>
	 * 这里只移除消费者组 pending 记录，不删除 Stream 中的消息实体。
	 *
	 * @param messageId 消息 ID
	 * @return 确认成功的消息数量
	 */
	protected long ack(StreamMessageId messageId) {
		return stream.ack(groupName, messageId);
	}

	/**
	 * 确认并删除消息。
	 * <p>
	 * 先使用普通 XACK 移除当前消费者组 pending 记录，再使用 XDEL 删除 Stream 消息实体。
	 *
	 * @param messageId 消息 ID
	 * @return 删除成功的消息数量
	 */
	protected long ackAndDelete(StreamMessageId messageId) {
		this.ack(messageId);
		return stream.remove(messageId);
	}
}
