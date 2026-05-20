package org.zero.common.core.extension.redisson.api.stream;

import org.junit.jupiter.api.Test;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamReadGroupArgs;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/20
 */
class NewMessageProcessorTest {
	private static final String GROUP_NAME = "group";
	private static final String CONSUMER_NAME = "consumer";

	@Test
	void shouldAckWithCompatibleCommandAfterMessageProcessed() {
		RecordingRStream<String, String> stream = RecordingRStream.create();
		StreamMessageId messageId = this.prepareReadMessage(stream, 1);
		NewMessageProcessor<String, String> processor = new NewMessageProcessor<String, String>(
				stream.proxy(),
				GROUP_NAME,
				CONSUMER_NAME,
				message -> MessageAction.ACK
		);

		processor.process();

		assertEquals(Collections.singletonList(messageId), stream.getAcknowledgedIds());
		assertEquals(GROUP_NAME, stream.getLastAckGroupName());
		assertEquals(0, stream.getAckWithArgsCount());
	}

	@Test
	void shouldAckAndDeleteWhenHandlerRequestsAckAndDelete() {
		RecordingRStream<String, String> stream = RecordingRStream.create();
		StreamMessageId messageId = this.prepareReadMessage(stream, 2);
		NewMessageProcessor<String, String> processor = new NewMessageProcessor<String, String>(
				stream.proxy(),
				GROUP_NAME,
				CONSUMER_NAME,
				message -> MessageAction.ACK_AND_DELETE
		);

		processor.process();

		assertEquals(Collections.singletonList(messageId), stream.getAcknowledgedIds());
		assertEquals(Collections.singletonList(messageId), stream.getRemovedIds());
	}

	@Test
	void shouldKeepPendingWhenHandlerRequestsKeepPending() {
		RecordingRStream<String, String> stream = RecordingRStream.create();
		this.prepareReadMessage(stream, 3);
		NewMessageProcessor<String, String> processor = new NewMessageProcessor<String, String>(
				stream.proxy(),
				GROUP_NAME,
				CONSUMER_NAME,
				message -> MessageAction.KEEP_PENDING
		);

		processor.process();

		assertTrue(stream.getAcknowledgedIds().isEmpty());
		assertTrue(stream.getRemovedIds().isEmpty());
	}

	@Test
	void shouldCreateIndependentDefaultReadGroupArgs() {
		StreamReadGroupArgs first = NewMessageProcessor.defaultReadGroupArgs();
		StreamReadGroupArgs second = NewMessageProcessor.defaultReadGroupArgs();

		assertNotSame(first, second);
	}

	private StreamMessageId prepareReadMessage(RecordingRStream<String, String> stream, long id) {
		StreamMessageId messageId = new StreamMessageId(id, 0);
		Map<String, String> message = new LinkedHashMap<>();
		message.put("payload", "ok");
		Map<StreamMessageId, Map<String, String>> messages = new LinkedHashMap<>();
		messages.put(messageId, message);
		stream.readGroupResult(messages);
		return messageId;
	}
}
