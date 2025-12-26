package org.zero.common.core.extension.redisson.api.stream;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/28
 */
@FunctionalInterface
public interface InvalidMessageHandler<K, V> {
	default long getMaxIdleTime() {
		return 30 * 60 * 1000L;
	}

	default long getMaxDeliveredCount() {
		return 3;
	}

	void handle(PendingMessageEntry<K,V> pendingMessageEntry);
}
