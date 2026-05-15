package org.zero.common.core.extension.java.util;

/**
 * 键值对移除监听器。
 * <p>
 * 当 entry 因容量淘汰、TTL 过期、引用回收、显式删除、覆盖替换或清空操作离开当前映射时触发。
 * 回调通常在触发清理的线程内执行，耗时逻辑可能拖慢当前 Map 操作或后台清理线程。
 * 如需执行 I/O、远程调用、批量统计等复杂处理，应在实现中转交给独立异步流程。
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/4
 */
public interface PurgeListener<K, V> {
	/**
	 * 处理一次 entry 移除事件。
	 * <p>
	 * {@code value} 表示移除时可观察到的旧值；当移除原因为 {@link PurgeReason#COLLECTED} 时，
	 * {@code value} 始终为 {@code null}，因为值对象已经被回收。除引用回收外，支持 {@code null}
	 * value 的 Map 也可能把用户写入的 {@code null} 传给监听器。
	 * <p>
	 * 当移除原因为 {@link PurgeReason#REPLACED} 时，{@code value} 表示被覆盖替换的旧值，而不是替换后的新值。
	 *
	 * @param key    被移除的键
	 * @param value  被移除的值，可能为 {@code null}
	 * @param reason 移除原因
	 */
	void onPurge(K key, V value, PurgeReason reason);
}
