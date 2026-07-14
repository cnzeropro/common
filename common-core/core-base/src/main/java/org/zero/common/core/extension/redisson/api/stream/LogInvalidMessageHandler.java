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
	protected static final String INVALID_MESSAGE_LOG_TEMPLATE = "invalid pending stream message, context: {}";

	protected final Level level;

	public LogInvalidMessageHandler(Level level) {
		this.level = Objects.requireNonNull(level, "level must not be null");
	}

	@Override
	public MessageAction handle(MessageContext<K, V> context) {
		Objects.requireNonNull(context, "context must not be null");
		this.logInvalidMessage(context);
		return MessageAction.ACK;
	}

	protected void logInvalidMessage(MessageContext<K, V> context) {
		switch (this.level) {
			case ERROR:
				log.error(INVALID_MESSAGE_LOG_TEMPLATE, context);
				return;
			case WARN:
				log.warn(INVALID_MESSAGE_LOG_TEMPLATE, context);
				return;
			case INFO:
				log.info(INVALID_MESSAGE_LOG_TEMPLATE, context);
				return;
			case TRACE:
				log.trace(INVALID_MESSAGE_LOG_TEMPLATE, context);
				return;
			case DEBUG:
			default:
				log.debug(INVALID_MESSAGE_LOG_TEMPLATE, context);
		}
	}
}
