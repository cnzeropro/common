package org.zero.common.core.extension.redisson.api.stream;

import org.junit.jupiter.api.Test;
import org.redisson.api.PendingEntry;
import org.redisson.api.PendingResult;
import org.redisson.api.StreamMessageId;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/20
 */
class PendingMessageProcessorTest {
	private static final String GROUP_NAME = "group";
	private static final String CONSUMER_NAME = "consumer";

	@Test
	void shouldAckWithCompatibleCommandAfterPendingMessageProcessed() {
		RecordingRStream<String, String> stream = RecordingRStream.create();
		StreamMessageId messageId = new StreamMessageId(1, 0);
		this.preparePendingMessage(stream, messageId, new PendingEntry(messageId, CONSUMER_NAME, 1, 1));
		AtomicInteger processedCount = new AtomicInteger();
		PendingMessageProcessor<String, String> processor = new PendingMessageProcessor<>(
				stream.proxy(),
				GROUP_NAME,
				CONSUMER_NAME,
				message -> {
					processedCount.incrementAndGet();
					return MessageAction.ACK;
				},
				new NoopInvalidMessageHandler()
		);

		processor.process();

		assertEquals(1, processedCount.get());
		assertEquals(Collections.singletonList(messageId), stream.getAcknowledgedIds());
		assertEquals(GROUP_NAME, stream.getLastAckGroupName());
		assertEquals(0, stream.getAckWithArgsCount());
	}

	@Test
	void shouldAckAndDeleteAfterPendingMessageProcessed() {
		RecordingRStream<String, String> stream = RecordingRStream.create();
		StreamMessageId messageId = new StreamMessageId(2, 0);
		this.preparePendingMessage(stream, messageId, new PendingEntry(messageId, CONSUMER_NAME, 1, 1));
		PendingMessageProcessor<String, String> processor = new PendingMessageProcessor<>(
				stream.proxy(),
				GROUP_NAME,
				CONSUMER_NAME,
				message -> MessageAction.ACK_AND_DELETE,
				new NoopInvalidMessageHandler()
		);

		processor.process();

		assertEquals(Collections.singletonList(messageId), stream.getAcknowledgedIds());
		assertEquals(Collections.singletonList(messageId), stream.getRemovedIds());
	}

	@Test
	void shouldKeepPendingAfterPendingMessageProcessed() {
		RecordingRStream<String, String> stream = RecordingRStream.create();
		StreamMessageId messageId = new StreamMessageId(3, 0);
		this.preparePendingMessage(stream, messageId, new PendingEntry(messageId, CONSUMER_NAME, 1, 1));
		PendingMessageProcessor<String, String> processor = new PendingMessageProcessor<>(
				stream.proxy(),
				GROUP_NAME,
				CONSUMER_NAME,
				message -> MessageAction.KEEP_PENDING,
				new NoopInvalidMessageHandler()
		);

		processor.process();

		assertTrue(stream.getAcknowledgedIds().isEmpty());
		assertTrue(stream.getRemovedIds().isEmpty());
	}

	@Test
	void shouldKeepPendingWhenInvalidHandlerRequestsKeepPending() {
		RecordingRStream<String, String> stream = RecordingRStream.create();
		StreamMessageId messageId = new StreamMessageId(4, 0);
		this.preparePendingMessage(stream, messageId, new PendingEntry(messageId, CONSUMER_NAME, 100, 5));
		AtomicInteger invalidCount = new AtomicInteger();
		InvalidMessageHandler<String, String> handler = new InvalidMessageHandler<String, String>() {
			@Override
			public long getMaxIdleTime() {
				return 1;
			}

			@Override
			public long getMaxDeliveredCount() {
				return 1;
			}

			@Override
			public MessageAction handle(PendingMessageEntry<String, String> pendingMessageEntry) {
				invalidCount.incrementAndGet();
				return MessageAction.KEEP_PENDING;
			}
		};
		PendingMessageProcessor<String, String> processor = new PendingMessageProcessor<>(
				stream.proxy(),
				GROUP_NAME,
				CONSUMER_NAME,
				message -> MessageAction.ACK,
				handler
		);

		processor.process();

		assertEquals(1, invalidCount.get());
		assertTrue(stream.getAcknowledgedIds().isEmpty());
	}

	@Test
	void shouldAckAndDeleteWhenInvalidHandlerRequestsAckAndDelete() {
		RecordingRStream<String, String> stream = RecordingRStream.create();
		StreamMessageId messageId = new StreamMessageId(5, 0);
		this.preparePendingMessage(stream, messageId, new PendingEntry(messageId, CONSUMER_NAME, 100, 5));
		AtomicInteger invalidCount = new AtomicInteger();
		InvalidMessageHandler<String, String> handler = new InvalidMessageHandler<String, String>() {
			@Override
			public long getMaxIdleTime() {
				return 1;
			}

			@Override
			public long getMaxDeliveredCount() {
				return 1;
			}

			@Override
			public MessageAction handle(PendingMessageEntry<String, String> pendingMessageEntry) {
				invalidCount.incrementAndGet();
				return MessageAction.ACK_AND_DELETE;
			}
		};
		PendingMessageProcessor<String, String> processor = new PendingMessageProcessor<>(
				stream.proxy(),
				GROUP_NAME,
				CONSUMER_NAME,
				message -> MessageAction.ACK,
				handler
		);

		processor.process();

		assertEquals(1, invalidCount.get());
		assertEquals(Collections.singletonList(messageId), stream.getAcknowledgedIds());
		assertEquals(Collections.singletonList(messageId), stream.getRemovedIds());
	}

	@Test
	void shouldAutoClaimIdleMessagesBeforeListingCurrentPending() {
		RecordingRStream<String, String> stream = RecordingRStream.create();
		StreamMessageId messageId = new StreamMessageId(6, 0);
		stream.pendingInfo(new PendingResult(
				1,
				messageId,
				messageId,
				Collections.singletonMap("old-consumer", 1L)
		));
		PendingMessageProcessor<String, String> processor = new PendingMessageProcessor<>(
				stream.proxy(),
				GROUP_NAME,
				CONSUMER_NAME,
				message -> MessageAction.ACK,
				new NoopInvalidMessageHandler()
		);

		processor.process();

		assertEquals(1, stream.getInvocationCount("autoClaim"));
		assertEquals(GROUP_NAME, stream.getLastAutoClaimGroupName());
		assertEquals(CONSUMER_NAME, stream.getLastAutoClaimConsumerName());
		assertEquals(15L, stream.getLastAutoClaimIdleTime());
		assertEquals(TimeUnit.MINUTES, stream.getLastAutoClaimIdleTimeUnit());
		assertEquals(new StreamMessageId(0, 0), stream.getLastAutoClaimStartId());
		assertEquals(10, stream.getLastAutoClaimCount());
	}

	private void preparePendingMessage(
			RecordingRStream<String, String> stream,
			StreamMessageId messageId,
			PendingEntry pendingEntry
	) {
		Map<String, String> message = new LinkedHashMap<>();
		message.put("payload", "ok");
		Map<StreamMessageId, Map<String, String>> messages = new LinkedHashMap<>();
		messages.put(messageId, message);
		stream.pendingInfo(new PendingResult(1, messageId, messageId, Collections.singletonMap(CONSUMER_NAME, 1L)))
				.pendingEntries(Collections.singletonList(pendingEntry))
				.pendingRangeResult(messages);
	}

	private static class NoopInvalidMessageHandler implements InvalidMessageHandler<String, String> {
		@Override
		public MessageAction handle(PendingMessageEntry<String, String> pendingMessageEntry) {
			return MessageAction.ACK;
		}
	}
}
