package org.zero.common.core.extension.redisson.api.stream;

import org.junit.jupiter.api.Test;
import org.redisson.api.PendingEntry;
import org.redisson.api.StreamMessageId;

import javax.mail.MessageContext;
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
		MessageContext<String, String> context = this.pendingMessageContext(messageId);
		AtomicReference<Map<String, String>> mappedMessage = new AtomicReference<>();
		DeadLetterMessageHandler<String, String, String, String> handler = new DeadLetterMessageHandler<>(
				deadLetterStream.proxy(),
				entry -> {
					Map<String, String> message = new LinkedHashMap<>(entry.getMessage());
					message.put("originalMessageId", entry.getMessageId().toString());
					mappedMessage.set(message);
					return message;
				}
		);

		MessageAction action = handler.handle(context);

		assertEquals(MessageAction.ACK, action);
		assertEquals(1, deadLetterStream.getAddCount());
		assertNotNull(deadLetterStream.getLastAddArgs());
		assertEquals("body", mappedMessage.get().get("payload"));
		assertEquals(messageId.toString(), mappedMessage.get().get("originalMessageId"));
	}

	@Test
	void shouldCopyOriginalMessageByDefault() {
		RecordingRStream<String, String> deadLetterStream = RecordingRStream.create();
		DeadLetterMessageHandler<String, String, String, String> handler = new DeadLetterMessageHandler<>(
				deadLetterStream.proxy(),
				DeadLetterMessageHandler.copyMessage()
		);

		MessageAction action = handler.handle(this.pendingMessageContext(new StreamMessageId(2, 0)));

		assertEquals(MessageAction.ACK, action);
		assertEquals(1, deadLetterStream.getAddCount());
		assertNotNull(deadLetterStream.getLastAddArgs());
	}

	@Test
	void shouldMapMessageContextTypeToDifferentDeadLetterType() {
		RecordingRStream<String, String> deadLetterStream = RecordingRStream.create();
		StreamMessageId messageId = new StreamMessageId(13, 0);
		MessageContext<String, Integer> context = this.pendingIntegerMessageContext(messageId);
		AtomicReference<Map<String, String>> mappedMessage = new AtomicReference<>();
		DeadLetterMessageHandler<String, Integer, String, String> handler = new DeadLetterMessageHandler<>(
				deadLetterStream.proxy(),
				entry -> {
					Map<String, String> message = new LinkedHashMap<>();
					message.put("payload", String.valueOf(entry.getMessage().get("payload")));
					message.put("originalMessageId", entry.getMessageId().toString());
					mappedMessage.set(message);
					return message;
				}
		);

		MessageAction action = handler.handle(context);

		assertEquals(MessageAction.ACK, action);
		assertEquals(1, deadLetterStream.getAddCount());
		assertEquals("100", mappedMessage.get().get("payload"));
		assertEquals(messageId.toString(), mappedMessage.get().get("originalMessageId"));
	}

	@Test
	void shouldReturnConfiguredAckAndDeleteAction() {
		RecordingRStream<String, String> deadLetterStream = RecordingRStream.create();
		DeadLetterMessageHandler<String, String, String, String> handler = new DeadLetterMessageHandler<>(
				deadLetterStream.proxy(),
				MessageContext::getMessage,
				MessageAction.ACK_AND_DELETE
		);

		MessageAction action = handler.handle(this.pendingMessageContext(new StreamMessageId(3, 0)));

		assertEquals(MessageAction.ACK_AND_DELETE, action);
		assertEquals(1, deadLetterStream.getAddCount());
	}

	@Test
	void shouldKeepOriginalPendingWhenMapperFails() {
		RecordingRStream<String, String> deadLetterStream = RecordingRStream.create();
		DeadLetterMessageHandler<String, String, String, String> handler = new DeadLetterMessageHandler<>(
				deadLetterStream.proxy(),
				entry -> {
					throw new IllegalStateException("mapper failed");
				}
		);

		assertThrows(
				IllegalStateException.class,
				() -> handler.handle(this.pendingMessageContext(new StreamMessageId(4, 0)))
		);
		assertEquals(0, deadLetterStream.getAddCount());
	}

	@Test
	void shouldRejectEmptyDeadLetterMessage() {
		RecordingRStream<String, String> deadLetterStream = RecordingRStream.create();
		DeadLetterMessageHandler<String, String, String, String> handler = new DeadLetterMessageHandler<>(
				deadLetterStream.proxy(),
				entry -> Collections.emptyMap()
		);

		assertThrows(
				IllegalArgumentException.class,
				() -> handler.handle(this.pendingMessageContext(new StreamMessageId(5, 0)))
		);
		assertEquals(0, deadLetterStream.getAddCount());
	}

	private MessageContext<String, String> pendingMessageContext(StreamMessageId messageId) {
		Map<String, String> message = new LinkedHashMap<>();
		message.put("payload", "body");
		return MessageContext.pendingMessage(
				"group",
				"current-consumer",
				new PendingEntry(messageId, "consumer", 60_000, 4),
				message
		);
	}

	private MessageContext<String, Integer> pendingIntegerMessageContext(StreamMessageId messageId) {
		Map<String, Integer> message = new LinkedHashMap<>();
		message.put("payload", 100);
		return MessageContext.pendingMessage(
				"group",
				"current-consumer",
				new PendingEntry(messageId, "consumer", 60_000, 4),
				message
		);
	}
}
