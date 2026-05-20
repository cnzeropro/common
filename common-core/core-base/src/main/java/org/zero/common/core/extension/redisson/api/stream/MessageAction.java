package org.zero.common.core.extension.redisson.api.stream;

/**
 * 消息处理后的确认动作。
 * <p>
 * 该枚举只表达 Stream 消费记录的后续处理方式，不代表消息业务语义上的成功或失败。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/5/20
 */
public enum MessageAction {
	/**
	 * 确认消息。
	 * <p>
	 * 消息将从当前消费者组 pending 列表移除，但不会删除 Stream 中的消息实体。
	 */
	ACK,

	/**
	 * 确认并删除消息。
	 * <p>
	 * 先从当前消费者组 pending 列表移除，再删除 Stream 中的消息实体。删除后其他消费者组可能无法再读取该消息正文，
	 * 因此只适用于确认不再需要保留原始 Stream 消息的场景。
	 */
	ACK_AND_DELETE,

	/**
	 * 保留 pending 状态。
	 * <p>
	 * 适用于已经转移消息所有权，或希望后续恢复流程继续处理的场景。
	 */
	KEEP_PENDING
}
