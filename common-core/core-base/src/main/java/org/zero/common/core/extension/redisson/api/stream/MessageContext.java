package org.zero.common.core.extension.redisson.api.stream;

import lombok.Getter;
import lombok.ToString;
import org.redisson.api.PendingEntry;
import org.redisson.api.StreamMessageId;

import java.util.Map;
import java.util.Objects;

/**
 * 消息处理上下文。
 * <p>
 * 统一承载新投递消息和 pending 消息的正文、消息 ID 与投递状态。业务处理器可以直接基于该上下文完成
 * 幂等、审计、死信转存或诊断记录，无需再额外组合 Redisson 的消息正文与 pending 元数据。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/21
 */
@Getter
@ToString
public class MessageContext<K, V> {
	private final String groupName;
	private final String consumerName;
	private final StreamMessageId messageId;
	private final Map<K, V> message;
	private final PendingEntry pendingEntry;

	private MessageContext(
			String groupName,
			String consumerName,
			StreamMessageId messageId,
			Map<K, V> message,
			PendingEntry pendingEntry
	) {
		this.groupName = Objects.requireNonNull(groupName, "groupName must not be null");
		this.consumerName = Objects.requireNonNull(consumerName, "consumerName must not be null");
		this.messageId = Objects.requireNonNull(messageId, "messageId must not be null");
		this.message = Objects.requireNonNull(message, "message must not be null");
		this.pendingEntry = pendingEntry;
	}

	/**
	 * 创建新投递消息上下文。
	 *
	 * @param groupName 消费者组名称
	 * @param consumerName 当前消费者名称，即本次读取消息的消费者
	 * @param messageId 消息 ID
	 * @param message 消息正文
	 * @param <K> 消息字段类型
	 * @param <V> 消息值类型
	 * @return 新消息上下文
	 */
	public static <K, V> MessageContext<K, V> newMessage(
			String groupName,
			String consumerName,
			StreamMessageId messageId,
			Map<K, V> message
	) {
		return new MessageContext<>(groupName, consumerName, messageId, message, null);
	}

	/**
	 * 创建 pending 消息上下文。
	 *
	 * @param groupName 消费者组名称
	 * @param consumerName 当前消费者名称，即本轮恢复处理消息的消费者
	 * @param pendingEntry pending 元数据
	 * @param message 消息正文
	 * @param <K> 消息字段类型
	 * @param <V> 消息值类型
	 * @return pending 消息上下文
	 */
	public static <K, V> MessageContext<K, V> pendingMessage(
			String groupName,
			String consumerName,
			PendingEntry pendingEntry,
			Map<K, V> message
	) {
		Objects.requireNonNull(pendingEntry, "pendingEntry must not be null");
		return new MessageContext<>(
				groupName,
				consumerName,
				pendingEntry.getId(),
				message,
				pendingEntry
		);
	}

	/**
	 * 当前上下文是否来自 pending 列表。
	 *
	 * @return 来自 pending 列表时返回 {@code true}
	 */
	public boolean isPending() {
		return pendingEntry != null;
	}

	/**
	 * 返回消息当前所属消费者。
	 * <p>
	 * 新投递消息尚未发生所有权迁移，返回当前消费者；pending 消息返回 Redisson pending 记录中的消费者。
	 *
	 * @return 消息当前所有者消费者名称
	 */
	public String getOwnerConsumerName() {
		return pendingEntry == null ? consumerName : pendingEntry.getConsumerName();
	}

	/**
	 * 返回 pending 空闲时间。
	 * <p>
	 * 新投递消息尚未进入 pending 恢复流程，固定返回 {@code 0}。
	 *
	 * @return 空闲毫秒数
	 */
	public long getIdleTime() {
		return pendingEntry == null ? 0L : pendingEntry.getIdleTime();
	}

	/**
	 * 返回投递次数。
	 * <p>
	 * 新投递消息按首次投递处理，固定返回 {@code 1}。
	 *
	 * @return 投递次数
	 */
	public long getDeliveredCount() {
		return pendingEntry == null ? 1L : pendingEntry.getLastTimeDelivered();
	}
}
