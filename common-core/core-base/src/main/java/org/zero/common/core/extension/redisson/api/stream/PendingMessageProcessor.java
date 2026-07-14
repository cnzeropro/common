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
public class PendingMessageProcessor<K, V> extends AbstractMessageProcessor<K, V>
		implements PollingMessageProcessor {
	public static final int DEFAULT_COUNT = 10;
	public static final long DEFAULT_TIMEOUT_NUMBER = 15;
	public static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.MINUTES;
	protected static final StreamMessageId AUTO_CLAIM_INITIAL_ID = new StreamMessageId(0, 0);

	protected final MessageHandler<K, V> messageHandler;
	protected final InvalidMessageHandler<K, V> invalidMessageHandler;
	protected final long idleTime;
	protected final TimeUnit idleTimeUnit;
	protected final int count;
	protected StreamMessageId autoClaimStartId = AUTO_CLAIM_INITIAL_ID;
	protected StreamMessageId pendingScanStartId = StreamMessageId.MIN;

	public PendingMessageProcessor(
			RStream<K, V> stream,
			String groupName,
			String consumerName,
			MessageHandler<K, V> messageHandler,
			InvalidMessageHandler<K, V> invalidMessageHandler
	) {
		this(stream, groupName, consumerName,
				messageHandler, invalidMessageHandler,
				DEFAULT_TIMEOUT_NUMBER, DEFAULT_TIMEOUT_UNIT, DEFAULT_COUNT);
	}

	public PendingMessageProcessor(
			RStream<K, V> stream,
			String groupName,
			String consumerName,
			MessageHandler<K, V> messageHandler,
			InvalidMessageHandler<K, V> invalidMessageHandler,
			long idleTime,
			TimeUnit idleTimeUnit,
			int count
	) {
		super(stream, groupName, consumerName);
		this.messageHandler = Objects.requireNonNull(
				messageHandler,
				"messageHandler must not be null"
		);
		this.invalidMessageHandler = Objects.requireNonNull(
				invalidMessageHandler,
				"invalidMessageHandler must not be null"
		);
		if (idleTime < 0) {
			throw new IllegalArgumentException("idleTime must not be negative");
		}
		if (count <= 0) {
			throw new IllegalArgumentException("count must be greater than 0");
		}
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
			this.autoClaimStartId = AUTO_CLAIM_INITIAL_ID;
			this.pendingScanStartId = StreamMessageId.MIN;
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
		List<PendingEntry> pendingEntries = this.listOwnedPendingEntries();
		if (pendingEntries.isEmpty()) {
			return;
		}
		Map<StreamMessageId, Map<K, V>> messageMap = this.readPendingMessages(pendingEntries);
		this.processPendingEntries(pendingEntries, messageMap);
		this.advancePendingScanStartId(pendingEntries);
	}

	protected void process(MessageContext<K, V> context) {
		log.debug(
				"pending stream message received, groupName: {}, consumerName: {}, entry: {}",
				groupName,
				consumerName,
				context
		);
		StreamMessageId id = context.getMessageId();
		if (context.getIdleTime() < invalidMessageHandler.getMaxIdleTime()
				&& context.getDeliveredCount() < invalidMessageHandler.getMaxDeliveredCount()) {
			MessageAction action = this.handleMessage(messageHandler, context);
			this.handleValidMessageAction(id, action);
			return;
		}
		log.warn(
				"pending stream message exceeded invalid threshold, "
						+ "groupName: {}, consumerName: {}, messageId: {}, idleTime: {}, deliveredCount: {}",
				groupName,
				consumerName,
				id,
				context.getIdleTime(),
				context.getDeliveredCount()
		);
		MessageAction action = this.handleMessage(invalidMessageHandler, context);
		this.handleInvalidMessageAction(id, action);
	}

	/**
	 * 列出当前消费者已经拥有且达到空闲阈值的 pending 记录。
	 * <p>
	 * 认领完成后仍需按当前消费者重新扫描 pending 列表，避免处理仍属于其它消费者的消息。
	 *
	 * @return 当前消费者可处理的 pending 记录
	 */
	protected List<PendingEntry> listOwnedPendingEntries() {
		StreamMessageId scanStartId = pendingScanStartId;
		List<PendingEntry> pendingEntries = stream.listPending(this.createPendingRangeArgs());
		if (pendingEntries == null || pendingEntries.isEmpty()) {
			this.pendingScanStartId = StreamMessageId.MIN;
			log.debug(
					"no owned idle pending stream messages, groupName: {}, consumerName: {}, scanStartId: {}",
					groupName,
					consumerName,
					scanStartId
			);
			return Collections.emptyList();
		}
		return pendingEntries;
	}

	/**
	 * 根据 pending 记录 ID 范围读取消息正文。
	 * <p>
	 * Redisson 的 {@code listPending} 只返回 pending 元数据，真正的消息正文仍需通过 {@code pendingRange}
	 * 单独读取。
	 *
	 * @param pendingEntries pending 元数据列表
	 * @return 消息 ID 到消息正文的映射
	 */
	protected Map<StreamMessageId, Map<K, V>> readPendingMessages(List<PendingEntry> pendingEntries) {
		StreamMessageId lowestId = pendingEntries.get(0).getId();
		StreamMessageId highestId = pendingEntries.get(pendingEntries.size() - 1).getId();
		Map<StreamMessageId, Map<K, V>> messageMap = stream.pendingRange(
				groupName,
				consumerName,
				lowestId,
				highestId,
				idleTime,
				idleTimeUnit,
				pendingEntries.size()
		);
		return messageMap == null ? Collections.emptyMap() : messageMap;
	}

	/**
	 * 逐条处理 pending 记录。
	 * <p>
	 * 单条消息处理失败时保留 pending 状态，并继续处理同批次中的其它消息。
	 *
	 * @param pendingEntries pending 元数据列表
	 * @param messageMap 消息 ID 到消息正文的映射
	 */
	protected void processPendingEntries(
			List<PendingEntry> pendingEntries,
			Map<StreamMessageId, Map<K, V>> messageMap
	) {
		for (PendingEntry pendingEntry : pendingEntries) {
			Map<K, V> message = messageMap.get(pendingEntry.getId());
			if (Objects.nonNull(message)) {
				MessageContext<K, V> context = MessageContext.pendingMessage(
						groupName,
						consumerName,
						pendingEntry,
						message
				);
				try {
					this.process(context);
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

	protected void handleValidMessageAction(StreamMessageId messageId, MessageAction action) {
		switch (action) {
			case ACK:
				this.executeAction(messageId, action);
				log.debug(
						"pending stream message acknowledged, groupName: {}, consumerName: {}, messageId: {}",
						groupName,
						consumerName,
						messageId
				);
				break;
			case ACK_AND_DELETE:
				long removedCount = this.executeAction(messageId, action);
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
				this.executeAction(messageId, action);
				log.info(
						"invalid pending stream message acknowledged, groupName: {}, consumerName: {}, messageId: {}",
						groupName,
						consumerName,
						messageId
				);
				break;
			case ACK_AND_DELETE:
				long removedCount = this.executeAction(messageId, action);
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
		StreamMessageId startId = autoClaimStartId;
		AutoClaimResult<K, V> autoClaimResult = stream.autoClaim(
				groupName,
				consumerName,
				idleTime,
				idleTimeUnit,
				startId,
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
		StreamMessageId nextId = autoClaimResult.getNextId();
		this.autoClaimStartId = nextId == null || AUTO_CLAIM_INITIAL_ID.equals(nextId)
				? AUTO_CLAIM_INITIAL_ID
				: nextId;
	}

	/**
	 * 创建 pending 列表扫描参数。
	 * <p>
	 * {@code StreamMessageId.MIN} 用于从列表头开始扫描；后续批次使用 exclusive start id 避免重复读取上一批
	 * 最后一条记录。
	 *
	 * @return pending 列表扫描参数
	 */
	protected StreamPendingRangeArgs createPendingRangeArgs() {
		StreamMessageId startId = pendingScanStartId;
		StreamPendingRangeArgs pendingRangeArgs;
		if (StreamMessageId.MIN.equals(startId)) {
			pendingRangeArgs = StreamPendingRangeArgs.groupName(groupName)
					.startId(StreamMessageId.MIN)
					.endId(StreamMessageId.MAX)
					.count(count);
		} else {
			pendingRangeArgs = StreamPendingRangeArgs.groupName(groupName)
					.startIdExclusive(startId)
					.endId(StreamMessageId.MAX)
					.count(count);
		}
		return pendingRangeArgs
				.consumerName(consumerName)
				.idleTime(Duration.of(idleTime, TimeUnitUtil.toChronoUnit(idleTimeUnit)));
	}

	/**
	 * 根据本批扫描结果推进下一轮 pending 扫描起点。
	 * <p>
	 * 本批不足 {@link #count} 条时，说明当前扫描窗口已经到达尾部，下一轮从列表头重新开始。
	 *
	 * @param pendingEntries 本批 pending 元数据列表
	 */
	protected void advancePendingScanStartId(List<PendingEntry> pendingEntries) {
		if (pendingEntries.size() < count) {
			this.pendingScanStartId = StreamMessageId.MIN;
			return;
		}
		this.pendingScanStartId = pendingEntries.get(pendingEntries.size() - 1).getId();
	}
}
