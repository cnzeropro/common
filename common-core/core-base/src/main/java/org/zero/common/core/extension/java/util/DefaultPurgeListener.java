package org.zero.common.core.extension.java.util;

import lombok.extern.java.Log;

import java.util.logging.Level;

/**
 * 默认键值对移除监听器。
 * <p>
 * 该实现只在 {@link Level#FINE} 级别记录被移除 entry 的 key、value 和 {@link PurgeReason}，
 * 不做业务补偿、资源释放或异步调度等额外处理。需要更复杂清理逻辑时，应自行实现 {@link PurgeListener}，
 * 并避免在回调线程内执行耗时操作。
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/4
 */
@Log
public class DefaultPurgeListener<K, V> implements PurgeListener<K, V> {
	/**
	 * 记录一次 entry 移除事件。
	 * <p>
	 * 日志级别低于 {@link Level#FINE} 时直接跳过参数格式化；当 {@code reason} 为
	 * {@link PurgeReason#COLLECTED} 时，{@code value} 可能已经被回收并表现为 {@code null}。
	 *
	 * @param key    被移除的键
	 * @param value  被移除的值，引用回收场景下可能为 {@code null}
	 * @param reason 移除原因
	 */
	@Override
	public void onPurge(K key, V value, PurgeReason reason) {
		if (log.isLoggable(Level.FINE)) {
			log.log(Level.FINE, "Purge [{0} -> {1}] from map, reason: {2}", new Object[]{key, value, reason});
		}
	}
}
