package org.zero.common.core.extension.java.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU Map
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/15
 */
public class LRUMap<K, V> extends LinkedHashMap<K, V> {
    /**
     * 默认最大容量：2^16 = 65536
     */
    public static final int DEFAULT_MAX_CAPACITY = 1 << 16;
    /**
     * 默认初始化容量：2^4 = 16
     */
    public static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    /**
     * 默认负载因子：0.75
     */
    public static final float DEFAULT_LOAD_FACTOR = 0.75F;

    /**
     * 最大容量
     */
    protected int maxCapacity;

    protected LRUMap() {
        this(DEFAULT_MAX_CAPACITY);
    }

    protected LRUMap(int maxCapacity) {
        this(maxCapacity, DEFAULT_INITIAL_CAPACITY);
    }

    protected LRUMap(int maxCapacity, int initialCapacity) {
        this(maxCapacity, initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    protected LRUMap(int maxCapacity, int initialCapacity, float loadFactor) {
        // 默认 accessOrder 为 true，这是保证缓存 LRU 的关键
        super(initialCapacity, loadFactor, true);
        this.maxCapacity = maxCapacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > maxCapacity;
    }

    public static <K, V> Map<K, V> create(boolean threadSafe) {
        return wrapper(threadSafe, new LRUMap<>());
    }

    public static <K, V> Map<K, V> create(boolean threadSafe, int maxCapacity) {
        return wrapper(threadSafe, new LRUMap<>(maxCapacity));
    }

    public static <K, V> Map<K, V> create(boolean threadSafe, int maxCapacity, int initialCapacity) {
        return wrapper(threadSafe, new LRUMap<>(maxCapacity, initialCapacity));
    }

    public static <K, V> Map<K, V> create(boolean threadSafe, int maxCapacity, int initialCapacity, float loadFactor) {
        return wrapper(threadSafe, new LRUMap<>(maxCapacity, initialCapacity, loadFactor));
    }

    public static <K, V> Map<K, V> wrapper(boolean threadSafe, LRUMap<K, V> map) {
        return threadSafe ? Collections.synchronizedMap(map) : map;
    }
}
