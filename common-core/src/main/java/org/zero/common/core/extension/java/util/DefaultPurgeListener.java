package org.zero.common.core.extension.java.util;

import lombok.extern.java.Log;

import java.util.logging.Level;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/4
 */
@Log
public class DefaultPurgeListener<K, V> implements PurgeListener<K, V> {
    @Override
    public void onPurge(K key, V value, PurgeReason reason) {
        if (log.isLoggable(Level.FINE)) {
            log.log(Level.FINE, "Purge [{0} -> {1}] from map, reason: {2}", new Object[]{key, value, reason});
        }
    }
}
