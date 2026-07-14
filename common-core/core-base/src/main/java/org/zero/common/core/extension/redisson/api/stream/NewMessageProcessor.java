package org.zero.common.core.extension.redisson.api.stream;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RStream;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamReadGroupArgs;
import org.zero.common.core.util.java.util.concurrent.TimeUnitUtil;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 读取并处理从未投递过的新 Stream 消息。
 * <p>
 * 新消息处理器返回 {@link MessageAction} 来决定确认、确认并删除或继续保留 pending；
 * 抛出异常时消息也会保留 pending，由 {@link PendingMessageProcessor} 一类的恢复流程再次处理。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/27
 */
@Slf4j
public class NewMessageProcessor<K, V> extends AbstractMessageProcessor<K, V> {
	public static final int DEFAULT_COUNT = 10;
	public static final long DEFAULT_TIMEOUT_NUMBER = 10;
	public static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.MINUTES;
	public static final Duration DEFAULT_TIMEOUT = Duration.of(
			DEFAULT_TIMEOUT_NUMBER,
			TimeUnitUtil.toChronoUnit(DEFAULT_TIMEOUT_UNIT)
	);

	protected final MessageHandler<K, V> messageHandler;
	protected final StreamReadGroupArgs readGroupArgs;

	public NewMessageProcessor(
			RStream<K, V> stream,
			String groupName,
			String consumerName,
			MessageHandler<K, V> messageHandler
	) {
		this(stream, groupName, consumerName, messageHandler, defaultReadGroupArgs());
	}

	public NewMessageProcessor(
			RStream<K, V> stream,
			String groupName,
			String consumerName,
			MessageHandler<K, V> messageHandler,
			StreamReadGroupArgs readGroupArgs
	) {
		super(stream, groupName, consumerName);
		this.messageHandler = Objects.requireNonNull(messageHandler, "messageHandler must not be null");
		this.readGroupArgs = Objects.requireNonNull(readGroupArgs, "readGroupArgs must not be null");
	}

	/**
	 * 创建默认读参数。
	 * <p>
	 * Redisson 读参数对象是可变对象，因此每次创建新实例，避免不同处理器之间共享状态。
	 */
	protected static StreamReadGroupArgs defaultReadGroupArgs() {
		return StreamReadGroupArgs.neverDelivered()
				.count(DEFAULT_COUNT)
				.timeout(DEFAULT_TIMEOUT);
	}

	@Override
	public void process() {
		Map<StreamMessageId, Map<K, V>> messageMap = stream.readGroup(groupName, consumerName, readGroupArgs);
		if (messageMap == null || messageMap.isEmpty()) {
			log.debug("no new stream messages, groupName: {}, consumerName: {}", groupName, consumerName);
			return;
		}
		messageMap.forEach((messageId, message) -> {
			log.debug("stream message received, groupName: {}, consumerName: {}, messageId: {}, message: {}", groupName, consumerName, messageId, message);
			try {
				// 创建新投递消息上下文
				MessageContext<K, V> context = MessageContext.newMessage(groupName, consumerName, messageId, message);
				MessageAction action = this.handleMessage(messageHandler, context);
				this.handleMessageAction(messageId, action);
			} catch (Exception e) {
				// 保留消息 pending 状态，避免单条消息异常导致消费线程退出。
				log.warn(
						"stream message processing failed, message remains pending, groupName: {}, consumerName: {}, messageId: {}",
						groupName,
						consumerName,
						messageId,
						e
				);
			}
		});
	}

	protected void handleMessageAction(StreamMessageId messageId, MessageAction action) {
		switch (action) {
			case ACK:
				this.executeAction(messageId, action);
				log.debug(
						"stream message acknowledged, groupName: {}, consumerName: {}, messageId: {}",
						groupName,
						consumerName,
						messageId
				);
				break;
			case ACK_AND_DELETE:
				long removedCount = this.executeAction(messageId, action);
				log.info(
						"stream message acknowledged and deleted, groupName: {}, consumerName: {}, messageId: {}, removedCount: {}",
						groupName,
						consumerName,
						messageId,
						removedCount
				);
				break;
			case KEEP_PENDING:
			default:
				log.warn(
						"stream message kept pending, groupName: {}, consumerName: {}, messageId: {}, action: {}",
						groupName,
						consumerName,
						messageId,
						action
				);
				break;
		}
	}
}
