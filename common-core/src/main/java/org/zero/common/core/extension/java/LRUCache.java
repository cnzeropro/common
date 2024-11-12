package org.zero.common.core.extension.java;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/15
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    public static final int DEFAULT_MAX_SIZE = 1 << 16;

    private final int maxSize;

    public LRUCache() {
        this(DEFAULT_MAX_SIZE);
    }

    public LRUCache(int maxSize) {
        this.maxSize = maxSize;
    }

    public LRUCache(int maxSize, int initialCapacity) {
        super(initialCapacity);
        this.maxSize = maxSize;
    }

    public LRUCache(int maxSize, int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        this.maxSize = maxSize;
    }

    public LRUCache(int maxSize, int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > maxSize;
    }
}
