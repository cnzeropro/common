package org.zero.common.core.extension.redisson.api.stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.PendingEntry;
import org.redisson.api.PendingResult;
import org.redisson.api.RStream;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamAckArgs;
import org.redisson.api.stream.StreamPendingRangeArgs;
import org.zero.common.core.util.java.util.concurrent.TimeUnitUtil;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/27
 */
@Slf4j
@RequiredArgsConstructor
public abstract class PendingMessageProcessor<K, V> implements MessageProcessor {
	public static final int DEFAULT_COUNT = 10;
	public static final long DEFAULT_TIMEOUT_NUMBER = 15;
	public static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.MINUTES;

	protected final RStream<K, V> stream;
	protected final String groupName;
	protected final String consumerName;
	protected final InvalidMessageHandler<K, V> invalidMessageHandler;
	protected final long idleTime;
	protected final TimeUnit idleTimeUnit;
	protected final int count;

	protected PendingMessageProcessor(RStream<K, V> stream, String groupName, String consumerName, InvalidMessageHandler<K, V> invalidMessageHandler) {
		this(stream, groupName, consumerName,
			invalidMessageHandler,
			DEFAULT_TIMEOUT_NUMBER, DEFAULT_TIMEOUT_UNIT, DEFAULT_COUNT);
	}

	@Override
	public void process() {
		PendingResult pendingResult = stream.getPendingInfo(groupName);
		long total = pendingResult.getTotal();
		if (total <= 0) {
			return;
		}
		StreamMessageId lowestId = pendingResult.getLowestId();
		StreamMessageId highestId = pendingResult.getHighestId();
		StreamPendingRangeArgs pendingRangeArgs = StreamPendingRangeArgs.groupName(groupName)
			.startId(lowestId)
			.endId(highestId)
			.count(count)
			.consumerName(consumerName)
			.idleTime(Duration.of(idleTime, TimeUnitUtil.toChronoUnit(idleTimeUnit)));
		List<PendingEntry> pendingEntries = stream.listPending(pendingRangeArgs);
		Map<StreamMessageId, Map<K, V>> messageMap = stream.pendingRange(groupName, consumerName, lowestId, highestId, idleTime, idleTimeUnit, count);
		for (PendingEntry pendingEntry : pendingEntries) {
			Map<K, V> message = messageMap.get(pendingEntry.getId());
			if (Objects.nonNull(message)) {
				PendingMessageEntry<K, V> pendingMessageEntry = new PendingMessageEntry<>(pendingEntry, message);
				try {
					this.process(pendingMessageEntry);
				} catch (Exception e) {
					log.debug(String.format("pending message processed error: %s", pendingMessageEntry), e);
				}
			}
		}
	}

	protected void process(PendingMessageEntry<K, V> pendingMessageEntry) {
		log.debug("pending message: {}", pendingMessageEntry);
		StreamMessageId id = pendingMessageEntry.getId();
		if (pendingMessageEntry.getIdleTime() >= invalidMessageHandler.getMaxIdleTime() || pendingMessageEntry.getLastTimeDelivered() >= invalidMessageHandler.getMaxDeliveredCount()) {
			log.debug("pending message is invalid: {}", pendingMessageEntry);
			invalidMessageHandler.handle(pendingMessageEntry);
			stream.ack(StreamAckArgs.group(groupName).ids(id));
		} else {
			Map<K, V> message = pendingMessageEntry.getMessage();
			if (this.process(message)) {
				stream.ack(StreamAckArgs.group(groupName).ids(id));
				log.debug("pending message processed successful: {}", pendingMessageEntry);
			} else {
				log.debug("pending message processed failed: {}", pendingMessageEntry);
			}
		}
	}

	protected abstract boolean process(Map<K, V> message);
}
