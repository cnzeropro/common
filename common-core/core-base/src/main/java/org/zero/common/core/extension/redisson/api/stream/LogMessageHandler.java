package org.zero.common.core.extension.redisson.api.stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.logging.Level;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/28
 */
@Log
@RequiredArgsConstructor
public class LogMessageHandler<K, V> implements InvalidMessageHandler<K, V> {
	protected final Level level;

	@Override
	public void handle(PendingMessageEntry<K, V> pendingMessageEntry) {
		log.log(level, "pending message entry is invalid: %s", pendingMessageEntry);
	}
}
