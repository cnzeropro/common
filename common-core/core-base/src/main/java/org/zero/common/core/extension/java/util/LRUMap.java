package org.zero.common.core.extension.java.util;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.zero.common.core.util.java.util.MapUtil.INITIAL_CAPACITY;
import static org.zero.common.core.util.java.util.MapUtil.LOAD_FACTOR;

/**
 * 非线程安全的 LRU Map。
 * <p>
 * 该实现基于 {@link LinkedHashMap} 的 access-order 模式维护最近访问顺序。当写入后元素数量超过
 * {@code maxCapacity} 时，最久未访问的条目会被自动移除。
 * <p>
 * 本类不做任何同步控制，适合单线程场景或调用方自行保证外部同步的场景。在 access-order 模式下，
 * {@code get()}、{@code getOrDefault()} 等读取操作也会调整访问顺序，因此不能被视为无副作用读操作。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2024/10/15
 */
public class LRUMap<K, V> extends LinkedHashMap<K, V> {
	/**
	 * 默认最大容量：2^16 = 65536
	 */
	public static final int DEFAULT_MAX_CAPACITY = 1 << 16;
	private static final long serialVersionUID = 1L;
	/**
	 * 最大容量。
	 */
	private final int maxCapacity;

	/**
	 * 使用默认最大容量创建 LRU Map。
	 */
	public LRUMap() {
		this(DEFAULT_MAX_CAPACITY);
	}

	/**
	 * 使用指定最大容量创建 LRU Map。
	 *
	 * @param maxCapacity 最大容量，必须大于 0
	 */
	public LRUMap(int maxCapacity) {
		this(maxCapacity, INITIAL_CAPACITY);
	}

	/**
	 * 使用指定最大容量和初始容量创建 LRU Map。
	 *
	 * @param maxCapacity     最大容量，必须大于 0
	 * @param initialCapacity 初始容量
	 */
	public LRUMap(int maxCapacity, int initialCapacity) {
		this(maxCapacity, initialCapacity, LOAD_FACTOR);
	}

	/**
	 * 使用指定最大容量、初始容量和负载因子创建 LRU Map。
	 *
	 * @param maxCapacity     最大容量，必须大于 0
	 * @param initialCapacity 初始容量
	 * @param loadFactor      负载因子
	 */
	public LRUMap(int maxCapacity, int initialCapacity, float loadFactor) {
		// 默认 accessOrder 为 true，这是保证缓存 LRU 的关键。
		super(normalizeInitialCapacity(maxCapacity, initialCapacity), loadFactor, true);
		this.maxCapacity = maxCapacity;
	}

	private static int normalizeInitialCapacity(int maxCapacity, int initialCapacity) {
		if (maxCapacity <= 0) {
			throw new IllegalArgumentException("maxCapacity must be greater than 0");
		}
		// 初始容量不需要大于最大容量，避免为永远无法保留的桶预分配空间。
		return Math.min(maxCapacity, initialCapacity);
	}

	/**
	 * 返回最大容量。
	 *
	 * @return 最大容量
	 */
	public int maxCapacity() {
		return maxCapacity;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return this.size() > maxCapacity;
	}
}
