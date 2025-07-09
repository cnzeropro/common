package org.zero.common.core.extension.java.util;

/**
 * 键值对移除监听器
 * <p>
 * 注意：请不要实现太复杂逻辑，否则可能会导致性能问题。
 * 如果实在需要，请采用异步方式处理。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/4
 */
public interface PurgeListener<K, V> {
    /**
     * 键值对被移除时的处理
     * <p>
     * 注意：当移除原因为 {@linkplain  PurgeReason#RECLAMATION RECLAMATION} 时，{@code value} 始终为 {@code null}，因为此时值已经被回收了。
     *
     * @param key    键
     * @param value  值
     * @param reason 移除原因
     */
    void onPurge(K key, V value, PurgeReason reason);
}
