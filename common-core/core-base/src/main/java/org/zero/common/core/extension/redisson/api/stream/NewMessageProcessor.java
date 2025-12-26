package org.zero.common.core.extension.redisson.api.stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RStream;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamAckArgs;
import org.redisson.api.stream.StreamReadGroupArgs;
import org.zero.common.core.util.java.util.concurrent.TimeUnitUtil;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/27
 */
@Slf4j
@RequiredArgsConstructor
public abstract class NewMessageProcessor<K, V> implements MessageProcessor {
	public static final int DEFAULT_COUNT = 10;
	public static final long DEFAULT_TIMEOUT_NUMBER = 10;
	public static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.MINUTES;
	public static final Duration DEFAULT_TIMEOUT = Duration.of(DEFAULT_TIMEOUT_NUMBER, TimeUnitUtil.toChronoUnit(DEFAULT_TIMEOUT_UNIT));
	public static final StreamReadGroupArgs DEFAULT_READ_GROUP_ARGS = StreamReadGroupArgs.neverDelivered().count(DEFAULT_COUNT).timeout(DEFAULT_TIMEOUT);

	protected final RStream<K, V> stream;
	protected final String groupName;
	protected final String consumerName;
	protected final StreamReadGroupArgs readGroupArgs;

	protected NewMessageProcessor(RStream<K, V> stream, String groupName, String consumerName) {
		this(stream, groupName, consumerName, DEFAULT_READ_GROUP_ARGS);
	}

	@Override
	public void process() {
		Map<StreamMessageId, Map<K, V>> messageMap = stream.readGroup(groupName, consumerName, readGroupArgs);
		messageMap.forEach((messageId, message) -> {
			log.debug("messageId: {}, message: {}", messageId, message);
			try {
				if (this.process(message)) {
					stream.ack(StreamAckArgs.group(groupName).ids(messageId));
					log.debug("message processed successful, messageId: {}, message: {}", messageId, message);
				} else {
					log.debug("message processed failed, messageId: {}, message: {}", messageId, message);
				}
			} catch (Exception e) {
				// 捕获异常，不会导致线程退出
				log.debug(String.format("message processed error, messageId: %s, message: %s", messageId, message), e);
			}
		});
	}

	protected abstract boolean process(Map<K, V> message);
}
