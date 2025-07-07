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
public interface EvictionListener<K, V> {
    void onEviction(K key, V value, EvictionReason reason);
}
