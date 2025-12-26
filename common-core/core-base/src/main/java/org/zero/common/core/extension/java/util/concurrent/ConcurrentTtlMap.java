package org.zero.common.core.extension.java.util.concurrent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.zero.common.core.extension.java.lang.LoopRunnable;
import org.zero.common.core.extension.java.lang.ThreadBuilder;
import org.zero.common.core.extension.java.util.PurgeListener;
import org.zero.common.core.extension.java.util.PurgeReason;
import org.zero.common.core.extension.java.util.function.ToBoolFunction;

import java.time.Duration;
import java.time.Instant;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.zero.common.core.util.java.util.MapUtil.DEFAULT_INITIAL_CAPACITY;
import static org.zero.common.core.util.java.util.MapUtil.DEFAULT_LOAD_FACTOR;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/17
 */
public class ConcurrentTtlMap<K, V> implements ConcurrentMap<K, V> {
	/**
	 * 核心存储
	 */
	protected final Map<K, Pair<K, V>> storage;
	/**
	 * 延迟队列
	 */
	protected final DelayQueue<Pair<K, V>> delayQueue;
	/**
	 * 缓存 TTL
	 */
	protected final Duration ttl;
	/**
	 * 缓存失效监听器
	 */
	protected final PurgeListener<K, V> listener;
	/**
	 * 清理线程
	 */
	protected final Thread cleanupThread;
	/**
	 * 读写锁
	 */
	protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	protected final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
	protected final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

	protected ConcurrentTtlMap(Map<K, Pair<K, V>> storage, DelayQueue<Pair<K, V>> delayQueue, Duration ttl, PurgeListener<K, V> listener, ThreadBuilder cleanupThreadBuilder) {
		this.storage = storage;
		this.delayQueue = delayQueue;
		this.ttl = ttl;
		this.listener = listener;
		this.cleanupThread = this.buildCleanupThread(cleanupThreadBuilder);
	}

	@Override
	public V get(Object key) {
		return this.getOrDefault(key, null);
	}

	@Override
	public V getOrDefault(Object key, V defaultValue) {
		Pair<K, V> pair;
		readLock.lock();
		try {
			pair = storage.get(key);
		} finally {
			readLock.unlock();
		}
		if (Objects.isNull(pair)) {
			return defaultValue;
		}
		if (pair.isExpired()) {
			this.cleanup(pair,
				p -> {
					writeLock.lock();
					try {
						delayQueue.remove(p);
						return storage.remove(p.getKey(), p);
					} finally {
						writeLock.unlock();
					}
				},
				PurgeReason.EXPIRY);
			return defaultValue;
		}
		V value = pair.getValue();
		return Objects.nonNull(value) ? value : defaultValue;
	}

	@Override
	public V put(K key, V value) {
		return this.put(key, value, ttl, listener);
	}

	public V put(K key, V value, Duration ttl) {
		return this.put(key, value, ttl, listener);
	}

	public V put(K key, V value, PurgeListener<K, V> listener) {
		return this.put(key, value, ttl, listener);
	}

	public V put(K key, V value, Duration ttl, PurgeListener<K, V> listener) {
		Pair<K, V> newPair = this.createPair(key, value, ttl, listener);
		Pair<K, V> oldPair;
		writeLock.lock();
		try {
			oldPair = storage.put(key, newPair);
			if (Objects.nonNull(ttl)) {
				delayQueue.add(newPair);
			}
		} finally {
			writeLock.unlock();
		}
		if (Objects.isNull(oldPair)) {
			return null;
		}
		if (delayQueue.remove(oldPair)) {
			this.notifyListener(oldPair, PurgeReason.EXPLICIT);
		}
		return oldPair.isExpired() ? null : oldPair.getValue();
	}

	protected Pair<K, V> createPair(K key, V value, Duration ttl, PurgeListener<K, V> listener) {
		Instant expireTime = Objects.isNull(ttl) ? null : Instant.now().plus(ttl);
		return new Pair<>(key, value, expireTime, listener);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		this.putAll(map, ttl, listener);
	}

	public void putAll(Map<? extends K, ? extends V> map, Duration ttl) {
		this.putAll(map, ttl, listener);
	}

	public void putAll(Map<? extends K, ? extends V> map, PurgeListener<K, V> listener) {
		this.putAll(map, ttl, listener);
	}

	public void putAll(Map<? extends K, ? extends V> map, Duration ttl, PurgeListener<K, V> listener) {
		Collection<Pair<K, V>> pairs = new ArrayList<>(map.size());
		writeLock.lock();
		try {
			map.forEach((key, value) -> {
				Pair<K, V> newPair = this.createPair(key, value, ttl, listener);
				Pair<K, V> oldPair = storage.put(key, newPair);
				if (Objects.nonNull(ttl)) {
					delayQueue.add(newPair);
				}
				if (Objects.nonNull(oldPair)) {
					pairs.add(oldPair);
				}
			});
		} finally {
			writeLock.unlock();
		}
		pairs.forEach(pair -> {
			if (delayQueue.remove(pair)) {
				this.notifyListener(pair, PurgeReason.EXPLICIT);
			}
		});
	}

	@Override
	public V putIfAbsent(K key, V value) {
		return this.putIfAbsent(key, value, ttl, listener);
	}

	public V putIfAbsent(K key, V value, Duration ttl) {
		return this.putIfAbsent(key, value, ttl, listener);
	}

	public V putIfAbsent(K key, V value, PurgeListener<K, V> listener) {
		return this.putIfAbsent(key, value, ttl, listener);
	}

	public V putIfAbsent(K key, V value, Duration ttl, PurgeListener<K, V> listener) {
		Pair<K, V> oldPair;
		boolean absent;
		writeLock.lock();
		try {
			oldPair = storage.get(key);
			absent = this.isAbsent(oldPair);
			if (absent) {
				Pair<K, V> newPair = this.createPair(key, value, ttl, listener);
				oldPair = storage.put(key, newPair);
				if (Objects.nonNull(ttl)) {
					delayQueue.add(newPair);
				}
			}
		} finally {
			writeLock.unlock();
		}
		if (Objects.isNull(oldPair)) {
			return null;
		}
		// is putted
		if (absent) {
			if (delayQueue.remove(oldPair)) {
				this.notifyListener(oldPair, PurgeReason.EXPLICIT);
			}
			return null;
		}
		if (oldPair.isExpired()) {
			this.cleanup(oldPair,
				p -> {
					writeLock.lock();
					try {
						delayQueue.remove(p);
						return storage.remove(p.getKey(), p);
					} finally {
						writeLock.unlock();
					}
				},
				PurgeReason.EXPIRY);
			return null;
		}
		return oldPair.getValue();
	}

	protected boolean isAbsent(Pair<K, V> pair) {
		return Objects.isNull(pair) || pair.isExpired() || Objects.isNull(pair.getValue());
	}

	@Override
	public V remove(Object key) {
		Pair<K, V> pair;
		writeLock.lock();
		try {
			pair = storage.remove(key);
		} finally {
			writeLock.unlock();
		}
		if (Objects.isNull(pair)) {
			return null;
		}
		if (delayQueue.remove(pair)) {
			this.notifyListener(pair, PurgeReason.EXPLICIT);
		}
		return pair.getValue();
	}

	@Override
	public boolean remove(Object key, Object value) {
		Pair<K, V> pair;
		writeLock.lock();
		try {
			pair = storage.get(key);
			if (Objects.isNull(pair) || pair.isExpired() || !Objects.equals(pair.getValue(), value)) {
				return false;
			}
			pair = storage.remove(key);
		} finally {
			writeLock.unlock();
		}
		if (delayQueue.remove(pair)) {
			this.notifyListener(pair, PurgeReason.EXPLICIT);
		}
		return true;
	}

	@Override
	public void clear() {
		Collection<Pair<K, V>> pairs;
		writeLock.lock();
		try {
			pairs = storage.values();
			storage.clear();
			delayQueue.clear();
		} finally {
			writeLock.unlock();
		}
		pairs.forEach(pair -> this.notifyListener(pair, PurgeReason.EXPLICIT));
	}

	@Override
	public V replace(K key, V value) {
		Pair<K, V> pair;
		writeLock.lock();
		try {
			pair = storage.get(key);
			if (Objects.isNull(pair)) {
				return null;
			}
			if (!pair.isExpired()) {
				return pair.setValue(value);
			}
		} finally {
			writeLock.unlock();
		}
		this.cleanup(pair,
			p -> {
				writeLock.lock();
				try {
					delayQueue.remove(p);
					return storage.remove(p.getKey(), p);
				} finally {
					writeLock.unlock();
				}
			},
			PurgeReason.EXPIRY);
		return null;
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		Pair<K, V> pair;
		writeLock.lock();
		try {
			pair = storage.get(key);
			if (Objects.isNull(pair)) {
				return false;
			}
			if (!pair.isExpired()) {
				if (Objects.equals(pair.getValue(), oldValue)) {
					pair.setValue(newValue);
					return true;
				} else {
					return false;
				}
			}
		} finally {
			writeLock.unlock();
		}
		this.cleanup(pair,
			p -> {
				writeLock.lock();
				try {
					delayQueue.remove(p);
					return storage.remove(p.getKey(), p);
				} finally {
					writeLock.unlock();
				}
			},
			PurgeReason.EXPIRY);
		return false;
	}

	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		Collection<Pair<K, V>> pairs = new ArrayList<>(storage.size());
		writeLock.lock();
		try {
			storage.forEach((key, pair) -> {
				if (pair.isExpired()) {
					pairs.add(pair);
					return;
				}
				V oldValue = pair.getValue();
				V newValue = function.apply(key, oldValue);
				pair.setValue(newValue);
			});
		} finally {
			writeLock.unlock();
		}
		pairs.forEach(pair -> this.cleanup(pair,
			p -> {
				writeLock.lock();
				try {
					delayQueue.remove(p);
					return storage.remove(p.getKey(), p);
				} finally {
					writeLock.unlock();
				}
			},
			PurgeReason.EXPIRY));
	}

	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		return this.computeIfAbsent(key, mappingFunction, ttl, listener);
	}

	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction, Duration ttl) {
		return this.computeIfAbsent(key, mappingFunction, ttl, listener);
	}

	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction, PurgeListener<K, V> listener) {
		return this.computeIfAbsent(key, mappingFunction, ttl, listener);
	}

	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction, Duration ttl, PurgeListener<K, V> listener) {
		Pair<K, V> oldPair;
		V newValue;
		writeLock.lock();
		try {
			oldPair = storage.get(key);
			if (!this.isAbsent(oldPair)) {
				return oldPair.getValue();
			}
			newValue = mappingFunction.apply(key);
			if (Objects.nonNull(newValue)) {
				Pair<K, V> newPair = this.createPair(key, newValue, ttl, listener);
				oldPair = storage.put(key, newPair);
				if (Objects.nonNull(ttl)) {
					delayQueue.add(newPair);
				}
			}
		} finally {
			writeLock.unlock();
		}
		if (Objects.nonNull(oldPair)) {
			this.cleanup(oldPair,
				p -> {
					writeLock.lock();
					try {
						boolean removed = delayQueue.remove(p);
						return Objects.nonNull(newValue) ? removed : storage.remove(p.getKey(), p);
					} finally {
						writeLock.unlock();
					}
				},
				Objects.nonNull(newValue) ? PurgeReason.EXPLICIT : PurgeReason.EXPIRY);
		}
		return newValue;
	}

	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		Pair<K, V> oldPair;
		boolean present;
		writeLock.lock();
		try {
			oldPair = storage.get(key);
			present = !this.isAbsent(oldPair);
			if (present) {
				V oldValue = oldPair.getValue();
				V newValue = remappingFunction.apply(key, oldValue);
				if (Objects.nonNull(newValue)) {
					oldPair.setValue(newValue);
					return newValue;
				}
				oldPair = storage.remove(key);
			}
		} finally {
			writeLock.unlock();
		}
		if (Objects.nonNull(oldPair)) {
			this.cleanup(oldPair,
				p -> {
					writeLock.lock();
					try {
						boolean removed = delayQueue.remove(p);
						return present ? removed : storage.remove(p.getKey(), p);
					} finally {
						writeLock.unlock();
					}
				},
				present ? PurgeReason.EXPLICIT : PurgeReason.EXPIRY);
		}
		return null;
	}

	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return this.compute(key, remappingFunction, ttl, listener);
	}

	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction, Duration ttl) {
		return this.compute(key, remappingFunction, ttl, listener);
	}

	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction, PurgeListener<K, V> listener) {
		return this.compute(key, remappingFunction, ttl, listener);
	}

	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction, Duration ttl, PurgeListener<K, V> listener) {
		Pair<K, V> oldPair;
		V newValue;
		writeLock.lock();
		try {
			oldPair = storage.get(key);
			V oldValue = this.isAbsent(oldPair) ? null : oldPair.getValue();
			newValue = remappingFunction.apply(key, oldValue);
			if (Objects.nonNull(newValue) && Objects.nonNull(oldPair)) {
				oldPair.setValue(newValue);
				return newValue;
			}
			if (Objects.isNull(newValue)) {
				oldPair = storage.remove(key);
			} else {
				Pair<K, V> newPair = this.createPair(key, newValue, ttl, listener);
				oldPair = storage.put(key, newPair);
				if (Objects.nonNull(ttl)) {
					delayQueue.add(newPair);
				}
			}
		} finally {
			writeLock.unlock();
		}
		if (Objects.nonNull(oldPair) && delayQueue.remove(oldPair)) {
			this.notifyListener(oldPair, PurgeReason.EXPLICIT);
		}
		return newValue;
	}

	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return this.merge(key, value, remappingFunction, ttl, listener);
	}

	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction, Duration ttl) {
		return this.merge(key, value, remappingFunction, ttl, listener);
	}

	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction, PurgeListener<K, V> listener) {
		return this.merge(key, value, remappingFunction, ttl, listener);
	}

	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction, Duration ttl, PurgeListener<K, V> listener) {
		Pair<K, V> oldPair;
		V newValue;
		writeLock.lock();
		try {
			oldPair = storage.get(key);
			V oldValue = this.isAbsent(oldPair) ? null : oldPair.getValue();
			newValue = Objects.isNull(oldValue) ? value : remappingFunction.apply(oldValue, value);
			if (Objects.nonNull(newValue) && Objects.nonNull(oldPair)) {
				oldPair.setValue(newValue);
				return newValue;
			}
			if (Objects.isNull(newValue)) {
				oldPair = storage.remove(key);
			} else {
				Pair<K, V> newPair = this.createPair(key, newValue, ttl, listener);
				oldPair = storage.put(key, newPair);
				if (Objects.nonNull(ttl)) {
					delayQueue.add(newPair);
				}
			}
		} finally {
			writeLock.unlock();
		}
		if (Objects.nonNull(oldPair) && delayQueue.remove(oldPair)) {
			this.notifyListener(oldPair, PurgeReason.EXPLICIT);
		}
		return newValue;
	}

	@Override
	public int size() {
		this.cleanupExpired(false);
		readLock.lock();
		try {
			return storage.size();
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public boolean isEmpty() {
		this.cleanupExpired(false);
		readLock.lock();
		try {
			return storage.isEmpty();
		} finally {
			readLock.unlock();
		}
	}

	@Override
	public boolean containsKey(Object key) {
		Pair<K, V> oldPair;
		readLock.lock();
		try {
			oldPair = storage.get(key);
			if (Objects.isNull(oldPair) || !oldPair.isExpired()) {
				return storage.containsKey(key);
			}
		} finally {
			readLock.unlock();
		}
		this.cleanup(oldPair,
			p -> {
				writeLock.lock();
				try {
					delayQueue.remove(p);
					return storage.remove(p.getKey(), p);
				} finally {
					writeLock.unlock();
				}
			},
			PurgeReason.EXPIRY);
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		Collection<Pair<K, V>> pairs = new ArrayList<>(storage.size());
		readLock.lock();
		try {
			for (Pair<K, V> pair : storage.values()) {
				V v = null;
				if (pair.isExpired()) {
					pairs.add(pair);
				} else {
					v = pair.getValue();
				}
				if (Objects.equals(v, value)) {
					return true;
				}
			}
		} finally {
			readLock.unlock();
			pairs.forEach(pair -> this.cleanup(pair,
				p -> {
					writeLock.lock();
					try {
						delayQueue.remove(p);
						return storage.remove(p.getKey(), p);
					} finally {
						writeLock.unlock();
					}
				},
				PurgeReason.EXPIRY));
		}
		return false;
	}

	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		Collection<Pair<K, V>> pairs = new ArrayList<>(storage.size());
		readLock.lock();
		try {
			storage.forEach((key, pair) -> {
				V value = null;
				if (pair.isExpired()) {
					pairs.add(pair);
				} else {
					value = pair.getValue();
				}
				action.accept(key, value);
			});
		} finally {
			readLock.unlock();
		}
		pairs.forEach(pair -> this.cleanup(pair,
			p -> {
				writeLock.lock();
				try {
					delayQueue.remove(p);
					return storage.remove(p.getKey(), p);
				} finally {
					writeLock.unlock();
				}
			},
			PurgeReason.EXPIRY));
	}

	protected transient Set<K> keySet;

	@Override
	public Set<K> keySet() {
		this.cleanupExpired(false);
		readLock.lock();
		try {
			Set<K> ks;
			return (ks = keySet) == null ? (keySet = new KeySet()) : ks;
		} finally {
			readLock.unlock();
		}
	}

	protected transient Collection<V> valueCollection;

	@Override
	public Collection<V> values() {
		this.cleanupExpired(false);
		readLock.lock();
		try {
			Collection<V> vc;
			return (vc = valueCollection) == null ? (valueCollection = new ValueCollection()) : vc;
		} finally {
			readLock.unlock();
		}
	}

	protected transient Set<Entry<K, V>> entrySet;

	@Override
	public Set<Entry<K, V>> entrySet() {
		this.cleanupExpired(false);
		readLock.lock();
		try {
			Set<Entry<K, V>> es;
			return (es = entrySet) == null ? (entrySet = new EntrySet()) : es;
		} finally {
			readLock.unlock();
		}
	}

	protected Thread buildCleanupThread(ThreadBuilder threadBuilder) {
		if (Objects.nonNull(threadBuilder)) {
			return threadBuilder.task(new LoopRunnable(() -> this.cleanupExpired(true))).start().build();
		}
		return null;
	}

	protected void cleanup(Pair<K, V> pair, ToBoolFunction<Pair<K, V>> removeFunction, PurgeReason reason) {
		boolean removed = removeFunction.applyAsBool(pair);
		if (removed) {
			this.notifyListener(pair, reason);
		}
	}

	/**
	 * 清理过期的键值对
	 */
	@SneakyThrows
	protected void cleanupExpired(boolean wait) {
		Pair<K, V> pair;
		while ((pair = (wait ? delayQueue.take() : delayQueue.poll())) != null) {
			this.cleanup(pair,
				p -> {
					writeLock.lock();
					try {
						return storage.remove(p.getKey(), p);
					} finally {
						writeLock.unlock();
					}
				},
				PurgeReason.EXPIRY);
		}
	}

	/**
	 * 通知监听器
	 */
	protected void notifyListener(Pair<K, V> pair, PurgeReason reason) {
		if (Objects.nonNull(pair)) {
			PurgeListener<K, V> listener = pair.getListener();
			if (Objects.nonNull(listener)) {
				listener.onPurge(pair.getKey(), pair.getValue(), reason);
			}
		}
	}

	protected void destroy() throws Exception {
		storage.clear();
		delayQueue.clear();
		if (Objects.nonNull(cleanupThread)) {
			cleanupThread.interrupt();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		this.destroy();
	}

	public static <K, V> Builder<K, V> builder() {
		return new Builder<>();
	}

	@Getter
	protected static class Pair<K, V> extends AbstractMap.SimpleEntry<K, V> implements InstantDelayed {
		protected final Instant expireTime;
		protected final PurgeListener<K, V> listener;

		public Pair(K key, V value, Instant expireTime, PurgeListener<K, V> listener) {
			super(key, value);
			this.expireTime = expireTime;
			this.listener = listener;
		}
	}

	protected class EntrySet extends AbstractSet<Entry<K, V>> {
		@Override
		public Iterator<Entry<K, V>> iterator() {
			return new EntryIterator();
		}

		@Override
		public int size() {
			return ConcurrentTtlMap.this.size();
		}

		@Override
		public boolean isEmpty() {
			return ConcurrentTtlMap.this.isEmpty();
		}

		@Override
		public void clear() {
			ConcurrentTtlMap.this.clear();
		}

		@Override
		public boolean add(Entry<K, V> entry) {
			Pair<K, V> oldPair;
			writeLock.lock();
			try {
				K key = entry.getKey();
				Pair<K, V> newPair;
				if (entry instanceof Pair) {
					newPair = (Pair<K, V>) entry;
				} else {
					newPair = ConcurrentTtlMap.this.createPair(key, entry.getValue(), ttl, listener);
				}
				oldPair = storage.get(key);
				boolean containsKey = (Objects.isNull(oldPair) || !oldPair.isExpired()) && storage.containsKey(key);
				if (containsKey) {
					return false;
				}
				oldPair = storage.put(key, newPair);
			} finally {
				writeLock.unlock();
			}
			if (delayQueue.remove(oldPair)) {
				ConcurrentTtlMap.this.notifyListener(oldPair, PurgeReason.EXPLICIT);
			}
			return Objects.isNull(oldPair);
		}

		@Override
		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}
			Entry<?, ?> e = (Entry<?, ?>) o;
			V value = ConcurrentTtlMap.this.get(e.getKey());
			return Objects.equals(value, e.getValue());
		}

		@Override
		public boolean remove(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}
			Entry<?, ?> e = (Entry<?, ?>) o;
			return ConcurrentTtlMap.this.remove(e.getKey(), e.getValue());
		}
	}

	protected class KeySet extends AbstractSet<K> {
		@Override
		public Iterator<K> iterator() {
			return new KeyIterator();
		}

		@Override
		public int size() {
			return ConcurrentTtlMap.this.size();
		}

		@Override
		public boolean isEmpty() {
			return ConcurrentTtlMap.this.isEmpty();
		}

		@Override
		public void clear() {
			ConcurrentTtlMap.this.clear();
		}
	}

	protected class ValueCollection extends AbstractCollection<V> {
		@Override
		public Iterator<V> iterator() {
			return new ValueIterator();
		}

		@Override
		public int size() {
			return ConcurrentTtlMap.this.size();
		}

		@Override
		public boolean isEmpty() {
			return ConcurrentTtlMap.this.isEmpty();
		}

		@Override
		public void clear() {
			ConcurrentTtlMap.this.clear();
		}
	}

	protected class PairIterator {
		protected final Iterator<Map.Entry<K, Pair<K, V>>> iterator = storage.entrySet().iterator();
		protected Pair<K, V> currentPair;

		public boolean hasNext() {
			Collection<Pair<K, V>> pairs = new ArrayList<>();
			boolean hasNext = false;
			writeLock.lock();
			try {
				while (iterator.hasNext()) {
					Entry<K, Pair<K, V>> entry = iterator.next();
					Pair<K, V> pair = entry.getValue();
					if (!pair.isExpired()) {
						currentPair = pair;
						hasNext = true;
						break;
					}
					pairs.add(pair);
					iterator.remove();
				}
			} finally {
				writeLock.unlock();
			}
			pairs.forEach(pair -> {
				if (delayQueue.remove(pair)) {
					ConcurrentTtlMap.this.notifyListener(pair, PurgeReason.EXPIRY);
				}
			});
			return hasNext;
		}

		protected Pair<K, V> nextPair() {
			Collection<Pair<K, V>> pairs;
			writeLock.lock();
			try {
				if (Objects.nonNull(currentPair)) {
					return currentPair;
				}
				pairs = new ArrayList<>();
				while (iterator.hasNext()) {
					Entry<K, Pair<K, V>> entry = iterator.next();
					Pair<K, V> pair = entry.getValue();
					if (!pair.isExpired()) {
						currentPair = pair;
						break;
					}
					pairs.add(pair);
					iterator.remove();
				}
			} finally {
				writeLock.unlock();
			}

			pairs.forEach(pair -> {
				if (delayQueue.remove(pair)) {
					ConcurrentTtlMap.this.notifyListener(pair, PurgeReason.EXPIRY);
				}
			});
			return currentPair;
		}

		public void remove() {
			writeLock.lock();
			Pair<K, V> pair;
			try {
				if (Objects.isNull(currentPair)) {
					throw new IllegalStateException();
				}
				pair = currentPair;
				iterator.remove();
				delayQueue.remove(currentPair);
				currentPair = null;
			} finally {
				writeLock.unlock();
			}
			ConcurrentTtlMap.this.notifyListener(pair, PurgeReason.EXPLICIT);
		}
	}

	protected class EntryIterator extends PairIterator implements Iterator<Entry<K, V>> {
		@Override
		public Entry<K, V> next() {
			return this.nextPair();
		}
	}

	protected class KeyIterator extends PairIterator implements Iterator<K> {
		@Override
		public K next() {
			return this.nextPair().getKey();
		}
	}

	protected class ValueIterator extends PairIterator implements Iterator<V> {
		@Override
		public V next() {
			return this.nextPair().getValue();
		}
	}

	@Setter
	@Accessors(chain = true, fluent = true)
	@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Builder<K, V> implements org.zero.common.core.extension.java.lang.Builder<ConcurrentTtlMap<K, V>, Builder<K, V>> {
		/**
		 * 核心存储
		 */
		protected Map<K, Pair<K, V>> storage = new HashMap<>(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
		/**
		 * 延迟队列
		 */
		protected DelayQueue<Pair<K, V>> delayQueue = new DelayQueue<>();
		/**
		 * 缓存 TTL
		 */
		protected Duration ttl;
		/**
		 * 缓存失效监听器
		 */
		protected PurgeListener<K, V> listener;

		/**
		 * 是否进行惰性清理。默认：false
		 * <p>
		 * 惰性清理：即后台不启动对应清理线程，仅在进行读写操作前清理。
		 */
		protected boolean lazyCleanup;
		/**
		 * 清理线程名称
		 */
		protected CharSequence cleanupThreadName = getClass().getEnclosingClass().getCanonicalName() + "Cleanup";
		/**
		 * 清理线程组
		 */
		protected ThreadGroup cleanupThreadGroup;
		/**
		 * 清理线程堆栈大小
		 */
		protected long cleanupThreadStackSize;
		/**
		 * 清理线程优先级
		 */
		protected int cleanupThreadPriority = Thread.NORM_PRIORITY;
		/**
		 * 清理线程是否守护
		 */
		protected boolean cleanupThreadDaemon = true;
		/**
		 * 清理线程异常处理
		 */
		protected Thread.UncaughtExceptionHandler cleanupThreadUncaughtExceptionHandler;

		public Builder<K, V> lazyCleanup() {
			return this.lazyCleanup(true);
		}

		public Builder<K, V> cleanupThreadDaemon() {
			return this.cleanupThreadDaemon(true);
		}

		protected ThreadBuilder createCleanupThreadBuilder() {
			ThreadBuilder cleanupThreadBuilder = null;
			if (!lazyCleanup) {
				cleanupThreadBuilder = new ThreadBuilder()
					.taskName(cleanupThreadName)
					.group(cleanupThreadGroup)
					.stackSize(cleanupThreadStackSize)
					.priority(cleanupThreadPriority)
					.daemon(cleanupThreadDaemon)
					.uncaughtExceptionHandler(cleanupThreadUncaughtExceptionHandler);
			}
			return cleanupThreadBuilder;
		}

		@Override
		public ConcurrentTtlMap<K, V> build() {
			ThreadBuilder cleanupThreadBuilder = this.createCleanupThreadBuilder();
			return new ConcurrentTtlMap<>(storage, delayQueue, ttl, listener, cleanupThreadBuilder);
		}
	}
}