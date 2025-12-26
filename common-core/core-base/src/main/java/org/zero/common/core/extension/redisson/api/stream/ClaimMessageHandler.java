package org.zero.common.core.extension.redisson.api.stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RStream;
import org.redisson.api.StreamMessageId;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
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
	public void handle(PendingMessageEntry<K, V> pendingMessageEntry) {
		List<StreamMessageId> messageIds = stream.fastClaim(groupName, consumerName, pendingMessageEntry.getIdleTime(), TimeUnit.MILLISECONDS, pendingMessageEntry.getId());
		log.debug("claim successful: {}", messageIds);
	}
}
