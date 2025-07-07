package org.zero.common.core.extension.java.util;

import lombok.extern.java.Log;

import java.util.logging.Level;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/4
 */
@Log
public class DefaultEvictionListener<K, V> implements EvictionListener<K, V> {
    @Override
    public void onEviction(K key, V value, EvictionReason reason) {
        if (log.isLoggable(Level.INFO)) {
            log.log(Level.INFO, "evict [{0} -> {1}] from map, reason: {2}", new Object[]{key, value, reason});
        }
    }
}
