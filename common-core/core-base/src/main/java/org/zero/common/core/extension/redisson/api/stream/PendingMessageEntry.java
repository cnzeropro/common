package org.zero.common.core.extension.redisson.api.stream;

import lombok.Getter;
import lombok.ToString;
import org.redisson.api.PendingEntry;

import java.util.Map;

/**
 * 带消息正文的 pending 条目。
 * <p>
 * Redisson 的 {@link PendingEntry} 只包含 pending 元数据，本类补充对应的 Stream 消息正文。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/28
 */
@ToString(callSuper = true)
public class PendingMessageEntry<K, V> extends PendingEntry {
	@Getter
	private final Map<K, V> message;

	public PendingMessageEntry(PendingEntry pendingEntry, Map<K, V> message) {
		super(
				pendingEntry.getId(),
				pendingEntry.getConsumerName(),
				pendingEntry.getIdleTime(),
				pendingEntry.getLastTimeDelivered()
		);
		this.message = message;
	}
}
