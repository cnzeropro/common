package org.zero.common.core.extension.redisson.api.stream;

import org.junit.jupiter.api.Test;
import org.redisson.api.PendingEntry;
import org.redisson.api.StreamMessageId;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/20
 */
class ClaimMessageHandlerTest {
	@Test
	void shouldKeepPendingAfterClaimingMessage() {
		RecordingRStream<String, String> stream = RecordingRStream.create();
		StreamMessageId messageId = new StreamMessageId(1, 0);
		stream.fastClaimResult(Collections.singletonList(messageId));
		ClaimMessageHandler<String, String> handler = new ClaimMessageHandler<>(
				stream.proxy(),
				"group",
				"target-consumer"
		);
		PendingMessageEntry<String, String> entry = new PendingMessageEntry<>(
				new PendingEntry(messageId, "old-consumer", 123, 2),
				Collections.emptyMap()
		);

		MessageAction action = handler.handle(entry);

		assertEquals(MessageAction.KEEP_PENDING, action);
		assertEquals(Collections.singletonList(messageId), stream.getFastClaimedIds());
		assertEquals("group", stream.getLastFastClaimGroupName());
		assertEquals("target-consumer", stream.getLastFastClaimConsumerName());
		assertEquals(123L, stream.getLastFastClaimIdleTime());
		assertEquals(TimeUnit.MILLISECONDS, stream.getLastFastClaimIdleTimeUnit());
		assertTrue(stream.getAcknowledgedIds().isEmpty());
	}
}
