package org.zero.common.core.extension.java.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.zero.common.core.util.java.util.MapUtil.INITIAL_CAPACITY;
import static org.zero.common.core.util.java.util.MapUtil.LOAD_FACTOR;

/**
 * 线程安全的 LRU Map。
 * <p>
 * 该实现通过组合 {@link LRUMap} 并在私有的 {@code delegate} 上加锁，保证每个公开操作在同一把锁下访问底层
 * access-order Map。由于 access-order 模式下 {@code get()} 也会调整访问顺序，读操作同样需要同步。
 * <p>
 * {@code entrySet()}、{@code keySet()} 与 {@code values()} 返回不可变快照，而不是底层 Map 的 live view。
 * 这可以避免调用方在遍历期间额外持有锁，也避免遍历过程中并发修改导致 {@link java.util.ConcurrentModificationException}。
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2026/05/14
 */
public class SynchronizedLRUMap<K, V> extends AbstractMap<K, V> implements Serializable {
	private static final long serialVersionUID = 1L;

	private final LRUMap<K, V> delegate;

	/**
	 * 使用默认最大容量创建线程安全 LRU Map。
	 */
	public SynchronizedLRUMap() {
		this(LRUMap.DEFAULT_MAX_CAPACITY);
	}

	/**
	 * 使用指定最大容量创建线程安全 LRU Map。
	 *
	 * @param maxCapacity 最大容量，必须大于 0
	 */
	public SynchronizedLRUMap(int maxCapacity) {
		this(maxCapacity, INITIAL_CAPACITY);
	}

	/**
	 * 使用指定最大容量和初始容量创建线程安全 LRU Map。
	 *
	 * @param maxCapacity     最大容量，必须大于 0
	 * @param initialCapacity 初始容量
	 */
	public SynchronizedLRUMap(int maxCapacity, int initialCapacity) {
		this(maxCapacity, initialCapacity, LOAD_FACTOR);
	}

	/**
	 * 使用指定最大容量、初始容量和负载因子创建线程安全 LRU Map。
	 *
	 * @param maxCapacity     最大容量，必须大于 0
	 * @param initialCapacity 初始容量
	 * @param loadFactor      负载因子
	 */
	public SynchronizedLRUMap(int maxCapacity, int initialCapacity, float loadFactor) {
		this.delegate = new LRUMap<>(maxCapacity, initialCapacity, loadFactor);
	}

	/**
	 * 返回最大容量。
	 *
	 * @return 最大容量
	 */
	public int maxCapacity() {
		synchronized (delegate) {
			return delegate.maxCapacity();
		}
	}

	@Override
	public int size() {
		synchronized (delegate) {
			return delegate.size();
		}
	}

	@Override
	public boolean isEmpty() {
		synchronized (delegate) {
			return delegate.isEmpty();
		}
	}

	@Override
	public boolean containsKey(Object key) {
		synchronized (delegate) {
			return delegate.containsKey(key);
		}
	}

	@Override
	public boolean containsValue(Object value) {
		synchronized (delegate) {
			return delegate.containsValue(value);
		}
	}

	@Override
	public V get(Object key) {
		synchronized (delegate) {
			return delegate.get(key);
		}
	}

	@Override
	public V getOrDefault(Object key, V defaultValue) {
		synchronized (delegate) {
			return delegate.getOrDefault(key, defaultValue);
		}
	}

	@Override
	public V put(K key, V value) {
		synchronized (delegate) {
			return delegate.put(key, value);
		}
	}

	@Override
	public V remove(Object key) {
		synchronized (delegate) {
			return delegate.remove(key);
		}
	}

	@Override
	public boolean remove(Object key, Object value) {
		synchronized (delegate) {
			return delegate.remove(key, value);
		}
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		synchronized (delegate) {
			delegate.putAll(map);
		}
	}

	@Override
	public void clear() {
		synchronized (delegate) {
			delegate.clear();
		}
	}

	@Override
	public V putIfAbsent(K key, V value) {
		synchronized (delegate) {
			return delegate.putIfAbsent(key, value);
		}
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		synchronized (delegate) {
			return delegate.replace(key, oldValue, newValue);
		}
	}

	@Override
	public V replace(K key, V value) {
		synchronized (delegate) {
			return delegate.replace(key, value);
		}
	}

	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		synchronized (delegate) {
			delegate.replaceAll(function);
		}
	}

	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		synchronized (delegate) {
			return delegate.computeIfAbsent(key, mappingFunction);
		}
	}

	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		synchronized (delegate) {
			return delegate.computeIfPresent(key, remappingFunction);
		}
	}

	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		synchronized (delegate) {
			return delegate.compute(key, remappingFunction);
		}
	}

	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		synchronized (delegate) {
			return delegate.merge(key, value, remappingFunction);
		}
	}

	@Override
	public Set<K> keySet() {
		synchronized (delegate) {
			// 返回快照而不是 live view，避免调用方遍历时依赖内部锁。
			return Collections.unmodifiableSet(new LinkedHashSet<>(delegate.keySet()));
		}
	}

	@Override
	public Collection<V> values() {
		synchronized (delegate) {
			// 保留当前访问顺序下的值快照。
			return Collections.unmodifiableList(new ArrayList<>(delegate.values()));
		}
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		synchronized (delegate) {
			return Collections.unmodifiableSet(snapshotEntrySet());
		}
	}

	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		Objects.requireNonNull(action, "action");
		Set<Entry<K, V>> snapshot;
		synchronized (delegate) {
			snapshot = snapshotEntrySet();
		}
		// 用户回调可能耗时或重入 Map 操作，放在锁外执行可减少锁占用和死锁风险。
		for (Entry<K, V> entry : snapshot) {
			action.accept(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public boolean equals(Object object) {
		synchronized (delegate) {
			return delegate.equals(object);
		}
	}

	@Override
	public int hashCode() {
		synchronized (delegate) {
			return delegate.hashCode();
		}
	}

	@Override
	public String toString() {
		synchronized (delegate) {
			return delegate.toString();
		}
	}

	private Set<Entry<K, V>> snapshotEntrySet() {
		Set<Entry<K, V>> snapshot = new LinkedHashSet<>(delegate.size());
		for (Entry<K, V> entry : delegate.entrySet()) {
			// 使用不可变 Entry，避免快照条目反向修改底层 Map。
			snapshot.add(new SimpleImmutableEntry<>(entry));
		}
		return snapshot;
	}

	private void writeObject(ObjectOutputStream outputStream) throws IOException {
		synchronized (delegate) {
			// 序列化期间也持有同一把锁，避免输出过程中底层访问顺序被并发修改。
			outputStream.defaultWriteObject();
		}
	}
}
