package org.zero.common.core.extension.redisson.api.stream;

import java.util.Map;

/**
 * 有效 Stream 消息处理器。
 * <p>
 * 用于处理新投递消息，以及尚未超过失效阈值的 pending 消息。处理器通过返回 {@link MessageAction}
 * 明确当前消息的后续动作；抛出异常时消息保持 pending。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/20
 */
@FunctionalInterface
public interface ValidMessageHandler<K, V> {
	/**
	 * 处理有效 Stream 消息正文。
	 *
	 * @param message 消息正文
	 * @return 消息后续动作
	 */
	MessageAction handle(Map<K, V> message);
}
