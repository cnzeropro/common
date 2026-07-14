package org.zero.common.core.extension.redisson.api.stream;

/**
 * 无效 pending 消息处理器。
 * <p>
 * 当消息空闲时间或投递次数超过阈值时，处理器需要明确返回 pending 列表的后续处理动作。
 * 该接口只负责失效消息的归宿，不承担业务重试判断。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/28
 */
@FunctionalInterface
public interface InvalidMessageHandler<K, V> extends MessageHandler<K, V> {
	/**
	 * 最大空闲时间，单位为毫秒。
	 * <p>
	 * Redisson 的 {@link org.redisson.api.PendingEntry#getIdleTime()} 返回值也是毫秒。
	 */
	default long getMaxIdleTime() {
		return 30 * 60 * 1000L;
	}

	/**
	 * 最大投递次数，达到该阈值后视为无效 pending 消息。
	 */
	default long getMaxDeliveredCount() {
		return 3;
	}

}
