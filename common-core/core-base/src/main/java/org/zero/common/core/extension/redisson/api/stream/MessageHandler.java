package org.zero.common.core.extension.redisson.api.stream;

/**
 * 消息处理器。
 * <p>
 * 处理器通过返回 {@link MessageAction} 明确当前消息的后续动作；抛出异常时消息保持 pending。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/20
 */
@FunctionalInterface
public interface MessageHandler<K, V> {
	/**
	 * 处理消息上下文。
	 *
	 * @param context 消息上下文
	 * @return 消息后续动作
	 */
	MessageAction handle(MessageContext<K, V> context);
}
