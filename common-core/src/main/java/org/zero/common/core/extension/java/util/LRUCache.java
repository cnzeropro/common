package org.zero.common.core.extension.java.util;

import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/15
 */
@EqualsAndHashCode(callSuper = true)
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    /**
     * 默认最大缓存数量，1 * 2^16 = 65536
     */
    public static final int DEFAULT_MAX_SIZE = 1 << 16;

    protected transient int maxCapacity;

    protected LRUCache() {
        this(DEFAULT_MAX_SIZE);
    }

    protected LRUCache(int maxCapacity) {
        super();
        this.maxCapacity = maxCapacity;
    }

    protected LRUCache(int maxCapacity, int initialCapacity) {
        super(initialCapacity);
        this.maxCapacity = maxCapacity;
    }

    protected LRUCache(int maxCapacity, int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        this.maxCapacity = maxCapacity;
    }

    protected LRUCache(int maxCapacity, int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
        this.maxCapacity = maxCapacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > maxCapacity;
    }

    public static <K, V> Map<K, V> create() {
        return new LRUCache<>();
    }

    public static <K, V> Map<K, V> createThreadSafe() {
        return Collections.synchronizedMap(create());
    }

    public static <K, V> Map<K, V> create(int maxCapacity) {
        return new LRUCache<>(maxCapacity);
    }

    public static <K, V> Map<K, V> createThreadSafe(int maxCapacity) {
        return Collections.synchronizedMap(create(maxCapacity));
    }

    public static <K, V> Map<K, V> create(int maxCapacity, int initialCapacity) {
        return new LRUCache<>(maxCapacity, initialCapacity);
    }

    public static <K, V> Map<K, V> createThreadSafe(int maxCapacity, int initialCapacity) {
        return Collections.synchronizedMap(create(maxCapacity, initialCapacity));
    }

    public static <K, V> Map<K, V> create(int maxCapacity, int initialCapacity, float loadFactor) {
        return new LRUCache<>(maxCapacity, initialCapacity, loadFactor);
    }

    public static <K, V> Map<K, V> createThreadSafe(int maxCapacity, int initialCapacity, float loadFactor) {
        return Collections.synchronizedMap(create(maxCapacity, initialCapacity, loadFactor));
    }

    public static <K, V> Map<K, V> create(int maxCapacity, int initialCapacity, float loadFactor, boolean accessOrder) {
        return new LRUCache<>(maxCapacity, initialCapacity, loadFactor, accessOrder);
    }

    public static <K, V> Map<K, V> createThreadSafe(int maxCapacity, int initialCapacity, float loadFactor, boolean accessOrder) {
        return Collections.synchronizedMap(create(maxCapacity, initialCapacity, loadFactor, accessOrder));
    }
}
