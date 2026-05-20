package org.zero.common.core.extension.redisson.api.stream;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;

import java.util.Objects;

/**
 * 记录无效 pending 消息并确认。
 * <p>
 * 适用于只需要丢弃并留痕的无效消息处理场景。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/28
 */
@Slf4j
public class LogInvalidMessageHandler<K, V> implements InvalidMessageHandler<K, V> {
	protected final Level level;

	public LogInvalidMessageHandler(Level level) {
		this.level = Objects.requireNonNull(level, "level must not be null");
	}

	@Override
	public MessageAction handle(PendingMessageEntry<K, V> pendingMessageEntry) {
		this.logInvalidMessage(level, pendingMessageEntry);
		return MessageAction.ACK;
	}

	protected void logInvalidMessage(Level level, PendingMessageEntry<K, V> pendingMessageEntry) {
		switch (level) {
			case ERROR:
				log.error("invalid pending stream message, entry: {}", pendingMessageEntry);
				break;
			case WARN:
				log.warn("invalid pending stream message, entry: {}", pendingMessageEntry);
				break;
			case INFO:
				log.info("invalid pending stream message, entry: {}", pendingMessageEntry);
				break;
			case TRACE:
				log.trace("invalid pending stream message, entry: {}", pendingMessageEntry);
				break;
			case DEBUG:
			default:
				log.debug("invalid pending stream message, entry: {}", pendingMessageEntry);
				break;
		}
	}
}
