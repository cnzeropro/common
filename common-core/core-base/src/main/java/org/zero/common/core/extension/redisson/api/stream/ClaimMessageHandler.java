package org.zero.common.core.extension.redisson.api.stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RStream;
import org.redisson.api.StreamMessageId;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 将无效 pending 消息转移给指定消费者。
 * <p>
 * 转移后返回 {@link MessageAction#KEEP_PENDING}，让消息继续留在 pending 列表中，
 * 由目标消费者在后续轮次处理。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/28
 */
@Slf4j
@RequiredArgsConstructor
public class ClaimMessageHandler<K, V> implements InvalidMessageHandler<K, V> {
	protected final RStream<K, V> stream;
	protected final String groupName;
	protected final String consumerName;

	@Override
	public MessageAction handle(PendingMessageEntry<K, V> pendingMessageEntry) {
		this.claim(pendingMessageEntry);
		return MessageAction.KEEP_PENDING;
	}

	/**
	 * 使用 XCLAIM JUSTID 转移消息所有权。
	 */
	protected List<StreamMessageId> claim(PendingMessageEntry<K, V> pendingMessageEntry) {
		List<StreamMessageId> messageIds = stream.fastClaim(
				groupName,
				consumerName,
				pendingMessageEntry.getIdleTime(),
				TimeUnit.MILLISECONDS,
				pendingMessageEntry.getId()
		);
		if (messageIds.isEmpty()) {
			log.warn(
					"pending stream message claim returned empty result, "
							+ "groupName: {}, sourceConsumerName: {}, targetConsumerName: {}, messageId: {}",
					groupName,
					pendingMessageEntry.getConsumerName(),
					consumerName,
					pendingMessageEntry.getId()
			);
		} else {
			log.info(
					"pending stream message claimed, groupName: {}, sourceConsumerName: {}, targetConsumerName: {}, messageIds: {}",
					groupName,
					pendingMessageEntry.getConsumerName(),
					consumerName,
					messageIds
			);
		}
		return messageIds;
	}
}
