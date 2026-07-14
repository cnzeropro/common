package org.zero.common.core.extension.redisson.api.stream;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RStream;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamAddArgs;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 将无效 pending 消息转存到死信 Stream 后确认原消息。
 * <p>
 * 写入死信 Stream 成功后返回 {@link MessageAction#ACK}，由 pending 处理器确认原消费者组消息；
 * 写入失败或消息映射失败时异常会继续向外抛出，原消息保持 pending，便于后续恢复或再次转存。
 * <p>
 * 死信消息的字段结构由 {@link MessageMapper} 决定。映射策略可以把源消息上下文
 * {@link MessageContext} 的键值类型映射为另一组死信 Stream 键值类型；同类型复制可使用
 * {@link #copyMessage()}。
 * <p>
 * 如需在转存后同时删除原 Stream 消息，可将 {@code successAction} 配置为
 * {@link MessageAction#ACK_AND_DELETE}。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/20
 */
@Slf4j
public class DeadLetterMessageHandler<SK, SV, DK, DV> implements InvalidMessageHandler<SK, SV> {
	protected final RStream<DK, DV> deadLetterStream;
	protected final MessageMapper<SK, SV, DK, DV> messageMapper;
	protected final MessageAction successAction;

	public DeadLetterMessageHandler(
			RStream<DK, DV> deadLetterStream,
			MessageMapper<SK, SV, DK, DV> messageMapper
	) {
		this(deadLetterStream, messageMapper, MessageAction.ACK);
	}

	public DeadLetterMessageHandler(
			RStream<DK, DV> deadLetterStream,
			MessageMapper<SK, SV, DK, DV> messageMapper,
			MessageAction successAction
	) {
		this.deadLetterStream = Objects.requireNonNull(deadLetterStream, "deadLetterStream must not be null");
		this.messageMapper = Objects.requireNonNull(messageMapper, "messageMapper must not be null");
		this.successAction = this.requireSuccessAction(successAction);
	}

	/**
	 * 创建复制原消息正文的映射策略。
	 *
	 * @param <K> 消息字段类型，同时用于源消息上下文和死信消息正文
	 * @param <V> 消息值类型，同时用于源消息上下文和死信消息正文
	 * @return 死信消息映射策略
	 */
	public static <K, V> MessageMapper<K, V, K, V> copyMessage() {
		return DeadLetterMessageHandler::copyMessageBody;
	}

	protected static <K, V> Map<K, V> copyMessageBody(MessageContext<K, V> context) {
		Objects.requireNonNull(context, "context must not be null");
		return new LinkedHashMap<>(Objects.requireNonNull(
				context.getMessage(),
				"pending message must not be null"
		));
	}

	@Override
	public MessageAction handle(MessageContext<SK, SV> context) {
		Objects.requireNonNull(context, "context must not be null");
		Map<DK, DV> message = this.mapMessage(context);
		StreamMessageId deadLetterMessageId = deadLetterStream.add(StreamAddArgs.entries(message));
		log.info("dead letter stream message appended, "
						+ "originalMessageId: {}, originalConsumerName: {}, idleTime: {}, deliveredCount: {}, "
						+ "deadLetterMessageId: {}",
				context.getMessageId(),
				context.getOwnerConsumerName(),
				context.getIdleTime(),
				context.getDeliveredCount(),
				deadLetterMessageId);
		return successAction;
	}

	protected Map<DK, DV> mapMessage(MessageContext<SK, SV> context) {
		Map<DK, DV> message = Objects.requireNonNull(
				messageMapper.map(context),
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

	/**
	 * 死信消息映射策略。
	 * <p>
	 * 实现类只负责生成要写入死信 Stream 的消息正文，不负责确认、删除或重试原消息。
	 *
	 * @param <SK> 源消息字段类型
	 * @param <SV> 源消息值类型
	 * @param <DK> 死信消息字段类型
	 * @param <DV> 死信消息值类型
	 */
	@FunctionalInterface
	public interface MessageMapper<SK, SV, DK, DV> {
		/**
		 * 将消息上下文映射为死信消息正文。
		 *
		 * @param context 消息上下文
		 * @return 死信消息正文
		 */
		Map<DK, DV> map(MessageContext<SK, SV> context);
	}
}
