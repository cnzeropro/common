package org.zero.common.core.extension.redisson.api.stream;

import org.junit.jupiter.api.Test;
import org.redisson.api.PendingEntry;
import org.redisson.api.StreamMessageId;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/20
 */
class DeadLetterMessageHandlerTest {
	@Test
	void shouldAppendMappedMessageAndAckOriginalPending() {
		RecordingRStream<String, String> deadLetterStream = RecordingRStream.create();
		StreamMessageId messageId = new StreamMessageId(1, 0);
		PendingMessageEntry<String, String> pendingMessageEntry = this.pendingMessageEntry(messageId);
		AtomicReference<Map<String, String>> mappedMessage = new AtomicReference<>();
		DeadLetterMessageHandler<String, String> handler = new DeadLetterMessageHandler<>(
				deadLetterStream.proxy(),
				entry -> {
					Map<String, String> message = new LinkedHashMap<>(entry.getMessage());
					message.put("originalMessageId", entry.getId().toString());
					mappedMessage.set(message);
					return message;
				}
		);

		MessageAction action = handler.handle(pendingMessageEntry);

		assertEquals(MessageAction.ACK, action);
		assertEquals(1, deadLetterStream.getAddCount());
		assertNotNull(deadLetterStream.getLastAddArgs());
		assertEquals("body", mappedMessage.get().get("payload"));
		assertEquals(messageId.toString(), mappedMessage.get().get("originalMessageId"));
	}

	@Test
	void shouldCopyOriginalMessageByDefault() {
		RecordingRStream<String, String> deadLetterStream = RecordingRStream.create();
		DeadLetterMessageHandler<String, String> handler = new DeadLetterMessageHandler<>(deadLetterStream.proxy());

		MessageAction action = handler.handle(this.pendingMessageEntry(new StreamMessageId(2, 0)));

		assertEquals(MessageAction.ACK, action);
		assertEquals(1, deadLetterStream.getAddCount());
		assertNotNull(deadLetterStream.getLastAddArgs());
	}

	@Test
	void shouldReturnConfiguredAckAndDeleteAction() {
		RecordingRStream<String, String> deadLetterStream = RecordingRStream.create();
		DeadLetterMessageHandler<String, String> handler = new DeadLetterMessageHandler<>(
				deadLetterStream.proxy(),
				PendingMessageEntry::getMessage,
				MessageAction.ACK_AND_DELETE
		);

		MessageAction action = handler.handle(this.pendingMessageEntry(new StreamMessageId(3, 0)));

		assertEquals(MessageAction.ACK_AND_DELETE, action);
		assertEquals(1, deadLetterStream.getAddCount());
	}

	@Test
	void shouldKeepOriginalPendingWhenMapperFails() {
		RecordingRStream<String, String> deadLetterStream = RecordingRStream.create();
		DeadLetterMessageHandler<String, String> handler = new DeadLetterMessageHandler<>(
				deadLetterStream.proxy(),
				entry -> {
					throw new IllegalStateException("mapper failed");
				}
		);

		assertThrows(
				IllegalStateException.class,
				() -> handler.handle(this.pendingMessageEntry(new StreamMessageId(4, 0)))
		);
		assertEquals(0, deadLetterStream.getAddCount());
	}

	@Test
	void shouldRejectEmptyDeadLetterMessage() {
		RecordingRStream<String, String> deadLetterStream = RecordingRStream.create();
		DeadLetterMessageHandler<String, String> handler = new DeadLetterMessageHandler<>(
				deadLetterStream.proxy(),
				entry -> Collections.emptyMap()
		);

		assertThrows(
				IllegalArgumentException.class,
				() -> handler.handle(this.pendingMessageEntry(new StreamMessageId(5, 0)))
		);
		assertEquals(0, deadLetterStream.getAddCount());
	}

	private PendingMessageEntry<String, String> pendingMessageEntry(StreamMessageId messageId) {
		Map<String, String> message = new LinkedHashMap<>();
		message.put("payload", "body");
		return new PendingMessageEntry<>(
				new PendingEntry(messageId, "consumer", 60_000, 4),
				message
		);
	}
}
