package org.zero.common.core.extension.redisson.api.stream;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RStream;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamAddArgs;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * 将无效 pending 消息转存到死信 Stream 后确认原消息。
 * <p>
 * 写入死信 Stream 成功后返回 {@link MessageAction#ACK}，由 pending 处理器确认原消费者组消息；
 * 写入失败或消息映射失败时异常会继续向外抛出，原消息保持 pending，便于后续恢复或再次转存。
 * <p>
 * 死信消息的字段结构由 {@code messageMapper} 决定。泛型键值类型不固定时，建议业务侧自行约定元数据字段
 * 或使用独立的死信消息类型，避免强行在原消息结构中追加不兼容字段。
 * <p>
 * 如需在转存后同时删除原 Stream 消息，可将 {@code successAction} 配置为
 * {@link MessageAction#ACK_AND_DELETE}。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/20
 */
@Slf4j
public class DeadLetterMessageHandler<K, V> implements InvalidMessageHandler<K, V> {
	protected final RStream<K, V> deadLetterStream;
	protected final Function<PendingMessageEntry<K, V>, Map<K, V>> messageMapper;
	protected final MessageAction successAction;

	public DeadLetterMessageHandler(RStream<K, V> deadLetterStream) {
		this(deadLetterStream, DeadLetterMessageHandler::copyMessage);
	}

	public DeadLetterMessageHandler(
			RStream<K, V> deadLetterStream,
			Function<PendingMessageEntry<K, V>, Map<K, V>> messageMapper
	) {
		this(deadLetterStream, messageMapper, MessageAction.ACK);
	}

	public DeadLetterMessageHandler(
			RStream<K, V> deadLetterStream,
			Function<PendingMessageEntry<K, V>, Map<K, V>> messageMapper,
			MessageAction successAction
	) {
		this.deadLetterStream = Objects.requireNonNull(deadLetterStream, "deadLetterStream must not be null");
		this.messageMapper = Objects.requireNonNull(messageMapper, "messageMapper must not be null");
		this.successAction = this.requireSuccessAction(successAction);
	}

	protected static <K, V> Map<K, V> copyMessage(PendingMessageEntry<K, V> pendingMessageEntry) {
		return new LinkedHashMap<>(Objects.requireNonNull(
				pendingMessageEntry.getMessage(),
				"pending message must not be null"
		));
	}

	@Override
	public MessageAction handle(PendingMessageEntry<K, V> pendingMessageEntry) {
		Objects.requireNonNull(pendingMessageEntry, "pendingMessageEntry must not be null");
		Map<K, V> message = this.mapMessage(pendingMessageEntry);
		StreamMessageId deadLetterMessageId = deadLetterStream.add(StreamAddArgs.entries(message));
		log.info(
				"dead letter stream message appended, "
						+ "originalMessageId: {}, originalConsumerName: {}, idleTime: {}, deliveredCount: {}, "
						+ "deadLetterMessageId: {}",
				pendingMessageEntry.getId(),
				pendingMessageEntry.getConsumerName(),
				pendingMessageEntry.getIdleTime(),
				pendingMessageEntry.getLastTimeDelivered(),
				deadLetterMessageId
		);
		return successAction;
	}

	protected Map<K, V> mapMessage(PendingMessageEntry<K, V> pendingMessageEntry) {
		Map<K, V> message = Objects.requireNonNull(
				messageMapper.apply(pendingMessageEntry),
				"dead letter message must not be null"
		);
		if (message.isEmpty()) {
			throw new IllegalArgumentException("dead letter message must not be empty");
		}
		return message;
	}

	protected MessageAction requireSuccessAction(MessageAction successAction) {
		MessageAction action = Objects.requireNonNull(successAction, "successAction must not be null");
		if (action == MessageAction.KEEP_PENDING) {
			throw new IllegalArgumentException("successAction must acknowledge original message");
		}
		return action;
	}
}
