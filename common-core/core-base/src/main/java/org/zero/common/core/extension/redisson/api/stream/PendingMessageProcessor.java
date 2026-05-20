package org.zero.common.core.extension.redisson.api.stream;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.AutoClaimResult;
import org.redisson.api.PendingEntry;
import org.redisson.api.PendingResult;
import org.redisson.api.RStream;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamPendingRangeArgs;
import org.zero.common.core.util.java.util.concurrent.TimeUnitUtil;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 处理消费者组中的 pending 消息。
 * <p>
 * 每轮先认领超过空闲阈值的消息，再处理当前消费者名下的 pending 消息。
 * 这样既能恢复旧消费者遗留消息，也能避免多个消费者重复处理同一条 pending 消息。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/27
 */
@Slf4j
public class PendingMessageProcessor<K, V> extends AbstractStreamMessageProcessor<K, V>
		implements PollingMessageProcessor {
	public static final int DEFAULT_COUNT = 10;
	public static final long DEFAULT_TIMEOUT_NUMBER = 15;
	public static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.MINUTES;

	protected final ValidMessageHandler<K, V> validMessageHandler;
	protected final InvalidMessageHandler<K, V> invalidMessageHandler;
	protected final long idleTime;
	protected final TimeUnit idleTimeUnit;
	protected final int count;

	public PendingMessageProcessor(
			RStream<K, V> stream,
			String groupName,
			String consumerName,
			ValidMessageHandler<K, V> validMessageHandler,
			InvalidMessageHandler<K, V> invalidMessageHandler
	) {
		this(stream, groupName, consumerName,
				validMessageHandler, invalidMessageHandler,
			DEFAULT_TIMEOUT_NUMBER, DEFAULT_TIMEOUT_UNIT, DEFAULT_COUNT);
	}

	public PendingMessageProcessor(
			RStream<K, V> stream,
			String groupName,
			String consumerName,
			ValidMessageHandler<K, V> validMessageHandler,
			InvalidMessageHandler<K, V> invalidMessageHandler,
			long idleTime,
			TimeUnit idleTimeUnit,
			int count
	) {
		super(stream, groupName, consumerName);
		this.validMessageHandler = Objects.requireNonNull(
				validMessageHandler,
				"validMessageHandler must not be null"
		);
		this.invalidMessageHandler = Objects.requireNonNull(
				invalidMessageHandler,
				"invalidMessageHandler must not be null"
		);
		this.idleTime = idleTime;
		this.idleTimeUnit = Objects.requireNonNull(idleTimeUnit, "idleTimeUnit must not be null");
		this.count = count;
	}

	@Override
	public void process() {
		PendingResult pendingResult = stream.getPendingInfo(groupName);
		long total = pendingResult.getTotal();
		if (total <= 0) {
			log.debug("no pending stream messages, groupName: {}, consumerName: {}", groupName, consumerName);
			return;
		}
		log.debug(
				"pending stream messages found, groupName: {}, consumerName: {}, total: {}, lowestId: {}, highestId: {}",
				groupName,
				consumerName,
				total,
				pendingResult.getLowestId(),
				pendingResult.getHighestId()
		);
		this.claimIdleMessages();
		// 认领后重新按当前消费者过滤 pending，确保只处理当前消费者已经拥有的消息。
		StreamMessageId lowestId = pendingResult.getLowestId();
		StreamMessageId highestId = pendingResult.getHighestId();
		StreamPendingRangeArgs pendingRangeArgs = StreamPendingRangeArgs.groupName(groupName)
			.startId(lowestId)
			.endId(highestId)
			.count(count)
			.consumerName(consumerName)
			.idleTime(Duration.of(idleTime, TimeUnitUtil.toChronoUnit(idleTimeUnit)));
		List<PendingEntry> pendingEntries = stream.listPending(pendingRangeArgs);
		Map<StreamMessageId, Map<K, V>> messageMap = stream.pendingRange(
				groupName,
				consumerName,
				lowestId,
				highestId,
				idleTime,
				idleTimeUnit,
				count
		);
		for (PendingEntry pendingEntry : pendingEntries) {
			Map<K, V> message = messageMap.get(pendingEntry.getId());
			if (Objects.nonNull(message)) {
				PendingMessageEntry<K, V> pendingMessageEntry = new PendingMessageEntry<>(pendingEntry, message);
				try {
					this.process(pendingMessageEntry);
				} catch (Exception e) {
					log.warn(
							"pending stream message processing failed, groupName: {}, consumerName: {}, messageId: {}",
							groupName,
							consumerName,
							pendingEntry.getId(),
							e
					);
				}
			} else {
				log.debug(
						"pending stream message body not found, groupName: {}, consumerName: {}, messageId: {}",
						groupName,
						consumerName,
						pendingEntry.getId()
				);
			}
		}
	}

	protected void process(PendingMessageEntry<K, V> pendingMessageEntry) {
		log.debug(
				"pending stream message received, groupName: {}, consumerName: {}, entry: {}",
				groupName,
				consumerName,
				pendingMessageEntry
		);
		StreamMessageId id = pendingMessageEntry.getId();
		if (pendingMessageEntry.getIdleTime() >= invalidMessageHandler.getMaxIdleTime()
				|| pendingMessageEntry.getLastTimeDelivered() >= invalidMessageHandler.getMaxDeliveredCount()) {
			log.warn(
					"pending stream message exceeded invalid threshold, "
							+ "groupName: {}, consumerName: {}, messageId: {}, idleTime: {}, deliveredCount: {}",
					groupName,
					consumerName,
					id,
					pendingMessageEntry.getIdleTime(),
					pendingMessageEntry.getLastTimeDelivered()
			);
			MessageAction action = Objects.requireNonNull(
					invalidMessageHandler.handle(pendingMessageEntry),
					"invalid message action must not be null"
			);
			this.handleInvalidMessageAction(id, action);
		} else {
			Map<K, V> message = pendingMessageEntry.getMessage();
			MessageAction action = Objects.requireNonNull(
					validMessageHandler.handle(message),
					"valid message action must not be null"
			);
			this.handleValidMessageAction(id, action);
		}
	}

	protected void handleValidMessageAction(StreamMessageId messageId, MessageAction action) {
		switch (action) {
			case ACK:
				this.ack(messageId);
				log.debug(
						"pending stream message acknowledged, groupName: {}, consumerName: {}, messageId: {}",
						groupName,
						consumerName,
						messageId
				);
				break;
			case ACK_AND_DELETE:
				long removedCount = this.ackAndDelete(messageId);
				log.info(
						"pending stream message acknowledged and deleted, "
								+ "groupName: {}, consumerName: {}, messageId: {}, removedCount: {}",
						groupName,
						consumerName,
						messageId,
						removedCount
				);
				break;
			case KEEP_PENDING:
			default:
				log.warn(
						"pending stream message kept pending, groupName: {}, consumerName: {}, messageId: {}, action: {}",
						groupName,
						consumerName,
						messageId,
						action
				);
				break;
		}
	}

	protected void handleInvalidMessageAction(StreamMessageId messageId, MessageAction action) {
		switch (action) {
			case ACK:
				this.ack(messageId);
				log.info(
						"invalid pending stream message acknowledged, groupName: {}, consumerName: {}, messageId: {}",
						groupName,
						consumerName,
						messageId
				);
				break;
			case ACK_AND_DELETE:
				long removedCount = this.ackAndDelete(messageId);
				log.info(
						"invalid pending stream message acknowledged and deleted, "
								+ "groupName: {}, consumerName: {}, messageId: {}, removedCount: {}",
						groupName,
						consumerName,
						messageId,
						removedCount
				);
				break;
			case KEEP_PENDING:
			default:
				log.info(
						"invalid pending stream message kept, groupName: {}, consumerName: {}, messageId: {}, action: {}",
						groupName,
						consumerName,
						messageId,
						action
				);
				break;
		}
	}

	/**
	 * 认领超过空闲阈值的 pending 消息。
	 * <p>
	 * 这里只转移所有权，不直接处理消息正文；后续 {@code listPending/pendingRange} 会按当前消费者读取并处理。
	 */
	protected void claimIdleMessages() {
		AutoClaimResult<K, V> autoClaimResult = stream.autoClaim(
				groupName,
				consumerName,
				idleTime,
				idleTimeUnit,
				this.autoClaimStartId(),
				count
		);
		Map<StreamMessageId, Map<K, V>> messages = autoClaimResult.getMessages();
		List<StreamMessageId> deletedIds = autoClaimResult.getDeletedIds();
		if (messages == null) {
			messages = Collections.emptyMap();
		}
		if (deletedIds == null) {
			deletedIds = Collections.emptyList();
		}
		if (!messages.isEmpty() || !deletedIds.isEmpty()) {
			log.info(
					"pending stream messages auto claimed, "
							+ "groupName: {}, consumerName: {}, claimedCount: {}, deletedIds: {}, nextId: {}",
					groupName,
					consumerName,
					messages.size(),
					deletedIds,
					autoClaimResult.getNextId()
			);
		}
	}

	/**
	 * XAUTOCLAIM 起始 ID。
	 * <p>
	 * 使用 {@code 0-0} 从 pending 列表头部开始扫描，而不是 range 查询中的 {@code -} 语义。
	 */
	protected StreamMessageId autoClaimStartId() {
		return new StreamMessageId(0, 0);
	}
}
