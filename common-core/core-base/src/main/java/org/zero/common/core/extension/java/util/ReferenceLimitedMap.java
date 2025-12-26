package org.zero.common.core.extension.java.util;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.zero.common.core.extension.java.lang.LoopRunnable;
import org.zero.common.core.extension.java.util.concurrent.DefaultThreadFactory;
import org.zero.common.core.extension.java.util.concurrent.InstantDelayed;
import org.zero.common.core.util.java.lang.ref.ReferenceType;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * 线程安全的引用类型 Map，支持最大容量限制和缓存过期时间
 *
 * todo
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/3
 */
public class ReferenceLimitedMap<K, V> extends AbstractMap<K, V> {
	public static final int DEFAULT_MAX_CAPACITY = 1 << 16;
	public static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
	public static final float DEFAULT_LOAD_FACTOR = 0.75F;
	public static final int DEFAULT_CLEANUP_THREAD_POOL_SIZE = 2;
	public static final ThreadFactory DEFAULT_CLEANUP_THREAD_FACTORY = DefaultThreadFactory.builder("ReferenceLimitedMap-Cleanup").daemon(Boolean.TRUE).build();
	public static final Executor DEFAULT_CLEANUP_EXECUTOR = Executors.newFixedThreadPool(DEFAULT_CLEANUP_THREAD_POOL_SIZE, DEFAULT_CLEANUP_THREAD_FACTORY);

	/**
	 * 最大容量
	 */
	protected final int maxCapacity;
	/**
	 * 是否有序
	 */
	protected final boolean order;
	/**
	 * 底层存储
	 */
	protected final Map<K, Pair<K, V>> storage;
	/**
	 * 引用队列
	 */
	protected final ReferenceQueue<V> referenceQueue;
	/**
	 * 延迟队列
	 */
	protected final DelayQueue<Pair<K, V>> delayQueue;
	/**
	 * 是否进行惰性清理
	 * <p>
	 * 惰性清理：即后台不启动对应清理线程，仅在进行读写操作前清理。
	 */
	protected final boolean lazyCleanup;
	/**
	 * 清理任务线程池
	 */
	protected final Executor cleanupExecutor;
	/**
	 * 引用类型
	 */
	protected final ReferenceType referenceType;
	/**
	 * 缓存 TTL
	 */
	protected final Duration ttl;
	/**
	 * 缓存失效监听器
	 */
	protected final PurgeListener<K, V> listener;

	/**
	 * 读写锁
	 */
	protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	protected final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
	protected final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

	protected ReferenceLimitedMap(int maxCapacity, boolean order,
								  Map<K, Pair<K, V>> storage,
								  ReferenceQueue<V> referenceQueue, DelayQueue<Pair<K, V>> delayQueue,
								  boolean lazyCleanup, Executor cleanupExecutor,
								  ReferenceType referenceType, Duration ttl, PurgeListener<K, V> listener) {
		this.maxCapacity = maxCapacity;
		this.order = order;
		this.storage = storage;
		this.referenceQueue = referenceQueue;
		this.delayQueue = delayQueue;
		this.lazyCleanup = lazyCleanup;
		this.cleanupExecutor = cleanupExecutor;
		this.referenceType = referenceType;
		this.ttl = ttl;
		this.listener = listener;
		// 启动清理任务
		this.submitCleanupTask();
	}

	public static <K, V> ReferenceLimitedMap<K, V> of() {
		return ReferenceLimitedMap.<K, V>builder().build();
	}

	public static <K, V> ReferenceLimitedMap<K, V> of(int maxCapacity) {
		return ReferenceLimitedMap.<K, V>builder().maxCapacity(maxCapacity).build();
	}

	public static <K, V> ReferenceLimitedMap<K, V> of(ReferenceType referenceType) {
		return ReferenceLimitedMap.<K, V>builder().referenceType(referenceType).build();
	}

	public static <K, V> Builder<K, V> builder() {
		return new Builder<>();
	}

	@Override
	public V get(Object key) {
		this.cleanupInvalidEntries();
		Pair<K, V> pair;
		readLock.lock();
		try {
			pair = storage.get(key);
		} finally {
			readLock.unlock();
		}
		return Objects.nonNull(pair) ? pair.getValue() : null;
	}

	@Override
	public V getOrDefault(Object key, V defaultValue) {
		this.cleanupInvalidEntries();
		Pair<K, V> pair;
		readLock.lock();
		try {
			pair = storage.get(key);
		} finally {
			readLock.unlock();
		}
		V v;
		if ((Objects.nonNull(pair) && (v = pair.getValue()) != null)) {
			return v;
		}
		return defaultValue;
	}

	@Override
	public V put(K key, V value) {
		return this.put(key, value, referenceType, ttl, listener);
	}

	public V put(K key, V value, ReferenceType referenceType) {
		return this.put(key, value, referenceType, ttl, listener);
	}

	public V put(K key, V value, Duration ttl) {
		return this.put(key, value, referenceType, ttl, listener);
	}

	public V put(K key, V value, PurgeListener<K, V> listener) {
		return this.put(key, value, referenceType, ttl, listener);
	}

	public V put(K key, V value, ReferenceType referenceType, Duration ttl) {
		return this.put(key, value, referenceType, ttl, listener);
	}

	public V put(K key, V value, ReferenceType referenceType, PurgeListener<K, V> listener) {
		return this.put(key, value, referenceType, ttl, listener);
	}

	public V put(K key, V value, Duration ttl, PurgeListener<K, V> listener) {
		return this.put(key, value, referenceType, ttl, listener);
	}

	public V put(K key, V value, ReferenceType referenceType, Duration ttl, PurgeListener<K, V> listener) {
		this.cleanupInvalidEntries();
		Pair<K, V> newPair = this.createPair(key, value, referenceType, ttl, listener);
		Pair<K, V> oldPair;
		writeLock.lock();
		try {
			oldPair = storage.put(key, newPair);
			delayQueue.remove(oldPair);
			// 如果指定了生命周期，则添加到延迟队列
			if (Objects.nonNull(ttl)) {
				delayQueue.put(newPair);
			}
		} finally {
			writeLock.unlock();
		}
		return Objects.nonNull(oldPair) ? oldPair.getValue() : null;
	}

	@Override
	public V putIfAbsent(K key, V value) {
		return this.putIfAbsent(key, value, referenceType, ttl, listener);
	}

	public V putIfAbsent(K key, V value, ReferenceType referenceType) {
		return this.putIfAbsent(key, value, referenceType, ttl, listener);
	}

	public V putIfAbsent(K key, V value, Duration ttl) {
		return this.putIfAbsent(key, value, referenceType, ttl, listener);
	}

	public V putIfAbsent(K key, V value, PurgeListener<K, V> listener) {
		return this.putIfAbsent(key, value, referenceType, ttl, listener);
	}

	public V putIfAbsent(K key, V value, ReferenceType referenceType, Duration ttl) {
		return this.putIfAbsent(key, value, referenceType, ttl, listener);
	}

	public V putIfAbsent(K key, V value, ReferenceType referenceType, PurgeListener<K, V> listener) {
		return this.putIfAbsent(key, value, referenceType, ttl, listener);
	}

	public V putIfAbsent(K key, V value, Duration ttl, PurgeListener<K, V> listener) {
		return this.putIfAbsent(key, value, referenceType, ttl, listener);
	}

	public V putIfAbsent(K key, V value, ReferenceType referenceType, Duration ttl, PurgeListener<K, V> listener) {
		writeLock.lock();
		try {
			V v;
			if ((v = this.get(key)) == null || this.containsKey(key)) {
				v = this.put(key, value, referenceType, ttl, listener);
			}
			return v;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public V replace(K key, V value) {
		Pair<K, V> pair;
		readLock.lock();
		try {
			pair = storage.get(key);
		} finally {
			readLock.unlock();
		}
		V v;
		if ((Objects.nonNull(pair) && (v = pair.getValue()) != null)) {
			return v;
		}
		return null;
	}

	public V replace(K key, V value, ReferenceType referenceType, Duration ttl, PurgeListener<K, V> listener) {
		return null;
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		return super.replace(key, oldValue, newValue);
	}

	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		super.replaceAll(function);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		this.putAll(m, referenceType, ttl, listener);
	}

	public void putAll(Map<? extends K, ? extends V> map, ReferenceType referenceType) {
		this.putAll(map, referenceType, ttl, listener);
	}

	public void putAll(Map<? extends K, ? extends V> map, Duration ttl) {
		this.putAll(map, referenceType, ttl, listener);
	}

	public void putAll(Map<? extends K, ? extends V> map, PurgeListener<K, V> listener) {
		this.putAll(map, referenceType, ttl, listener);
	}

	public void putAll(Map<? extends K, ? extends V> map, ReferenceType referenceType, Duration ttl) {
		this.putAll(map, referenceType, ttl, listener);
	}

	public void putAll(Map<? extends K, ? extends V> map, ReferenceType referenceType, PurgeListener<K, V> listener) {
		this.putAll(map, referenceType, ttl, listener);
	}

	public void putAll(Map<? extends K, ? extends V> map, Duration ttl, PurgeListener<K, V> listener) {
		this.putAll(map, referenceType, ttl, listener);
	}

	public void putAll(Map<? extends K, ? extends V> map, ReferenceType referenceType, Duration ttl, PurgeListener<K, V> listener) {
		for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
			this.put(entry.getKey(), entry.getValue(), referenceType, ttl, listener);
		}
	}

	@Override
	public V remove(Object key) {
		Pair<K, V> pair;
		synchronized (storage) {
			pair = storage.remove(key);
			delayQueue.remove(pair);
		}
		this.notifyListener(pair, PurgeReason.EXPLICIT);
		return Objects.nonNull(pair) ? pair.getValue() : null;
	}

	@Override
	public boolean remove(Object key, Object value) {
		return super.remove(key, value);
	}

	@Override
	public void clear() {
		Collection<Pair<K, V>> values;
		synchronized (storage) {
			values = storage.values();
			storage.clear();
			delayQueue.clear();
		}
		values.forEach(pair -> this.notifyListener(pair, PurgeReason.EXPLICIT));
	}

	@Override
	public int size() {
		this.cleanupInvalidEntries();
		synchronized (storage) {
			return storage.size();
		}
	}

	@Override
	public boolean containsKey(Object key) {
		this.cleanupInvalidEntries();
		synchronized (storage) {
			return storage.containsKey(key);
		}
	}

	@Override
	public boolean containsValue(Object value) {
		this.cleanupInvalidEntries();
		synchronized (storage) {
			for (Pair<K, V> pair : storage.values()) {
				V v = pair.getValue();
				if (Objects.equals(v, value)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Set<K> keySet() {
		this.cleanupInvalidEntries();
		Set<K> keys;
		synchronized (storage) {
			keys = storage.keySet();
		}
		return order ? new LinkedHashSet<>(keys) : new HashSet<>(keys);
	}

	@Override
	public Collection<V> values() {
		this.cleanupInvalidEntries();
		synchronized (storage) {
			return storage.values().stream().map(Pair::getValue).collect(Collectors.toList());
		}
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		this.cleanupInvalidEntries();
		Set<Entry<K, V>> entries = order ? new LinkedHashSet<>() : new HashSet<>();
		synchronized (storage) {
			for (Entry<K, Pair<K, V>> e : storage.entrySet()) {
				Pair<K, V> pair = e.getValue();
				Entry<K, V> entry = new SimpleImmutableEntry<>(e.getKey(), pair.getValue());
				entries.add(entry);
			}
		}
		return entries;
	}

	@Override
	public boolean isEmpty() {
		this.cleanupInvalidEntries();
		synchronized (storage) {
			return storage.isEmpty();
		}
	}

	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		this.cleanupInvalidEntries();
		synchronized (storage) {
			storage.forEach((k, v) -> action.accept(k, v.getValue()));
		}
	}

	protected void submitCleanupTask() {
		if (!lazyCleanup) {
			cleanupExecutor.execute(new LoopRunnable(() -> this.cleanupReclaimedEntries(true)));
			cleanupExecutor.execute(new LoopRunnable(() -> this.cleanupExpiredEntries(true)));
		}
	}

	/**
	 * 创建 {@link Pair} 对象
	 */
	protected Pair<K, V> createPair(K key, V value, ReferenceType referenceType, Duration ttl, PurgeListener<K, V> listener) {
		Instant expireTime = Objects.nonNull(ttl) ? Instant.now().plus(ttl) : null;
		switch (referenceType) {
			case STRONG:
				return new StrongPair<>(key, value, expireTime, listener);
			case SOFT:
				return new SoftPair<>(key, value, expireTime, listener, referenceQueue);
			case WEAK:
				return new WeakPair<>(key, value, expireTime, listener, referenceQueue);
			case PHANTOM:
				return new PhantomPair<>(key, value, expireTime, listener, referenceQueue);
			default:
				throw new AssertionError("Invalid reference type: " + referenceType);
		}
	}

	/**
	 * 清理无效键值对
	 */
	@SneakyThrows
	protected void cleanupInvalidEntries() {
		this.cleanupReclaimedEntries(false);
		this.cleanupExpiredEntries(false);
	}

	/**
	 * 清理 GC 回收过的键值对
	 */
	protected void cleanupReclaimedEntries(boolean wait) throws InterruptedException {
		Reference<?> reference;
		while ((reference = (wait ? referenceQueue.remove() : referenceQueue.poll())) != null) {
			if (reference instanceof Pair) {
				@SuppressWarnings("unchecked")
				Pair<K, V> pair = (Pair<K, V>) reference;
				synchronized (storage) {
					storage.remove(pair.getKey());
				}
				this.notifyListener(pair, PurgeReason.RECLAMATION);
			}
		}
	}

	/**
	 * 清理过期的键值对
	 */
	protected void cleanupExpiredEntries(boolean wait) throws InterruptedException {
		Pair<K, V> pair;
		while ((pair = (wait ? delayQueue.take() : delayQueue.poll())) != null) {
			synchronized (storage) {
				storage.remove(pair.getKey());
			}
			this.notifyListener(pair, PurgeReason.EXPIRY);
		}
	}

	/**
	 * 清理超出最大容量的键值对
	 */
	protected void cleanupExcess() {
		writeLock.lock();
		try {
			// 如果超过最大容量，移除第一个键值对，键值对的迭代器顺序和 coreMap 类型有关
			while (storage.size() > maxCapacity) {
				Iterator<Map.Entry<K, Pair<K, V>>> iterator = storage.entrySet().iterator();
				if (iterator.hasNext()) {
					Entry<K, Pair<K, V>> entry = iterator.next();
					iterator.remove();
					this.notifyListener(entry.getValue(), PurgeReason.EVICTION);
				}
			}
		} finally {
			writeLock.unlock();
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
		if (cleanupExecutor instanceof ExecutorService) {
			ExecutorService executorService = (ExecutorService) cleanupExecutor;
			executorService.shutdown();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		this.destroy();
	}

	protected interface Pair<K, V> extends InstantDelayed {
		K getKey();

		V getValue();

		PurgeListener<K, V> getListener();
	}

	@Getter
	@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
	@EqualsAndHashCode(onlyExplicitlyIncluded = true)
	protected static class StrongPair<K, V> implements Pair<K, V> {
		@EqualsAndHashCode.Include
		protected final K key;
		protected final V value;
		protected final Instant expireTime;
		protected final PurgeListener<K, V> listener;
	}

	@Getter
	@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
	protected static class SoftPair<K, V> extends SoftReference<V> implements Pair<K, V> {
		@EqualsAndHashCode.Include
		protected final K key;
		protected final Instant expireTime;
		protected final PurgeListener<K, V> listener;

		protected SoftPair(K key, V value, Instant expireTime, PurgeListener<K, V> listener, ReferenceQueue<V> queue) {
			super(value, queue);
			this.key = key;
			this.expireTime = expireTime;
			this.listener = listener;
		}

		@Override
		public V getValue() {
			return this.get();
		}
	}

	@Getter
	@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
	protected static class WeakPair<K, V> extends WeakReference<V> implements Pair<K, V> {
		@EqualsAndHashCode.Include
		protected final K key;
		protected final Instant expireTime;
		protected final PurgeListener<K, V> listener;

		protected WeakPair(K key, V value, Instant expireTime, PurgeListener<K, V> listener, ReferenceQueue<V> queue) {
			super(value, queue);
			this.key = key;
			this.expireTime = expireTime;
			this.listener = listener;
		}

		@Override
		public V getValue() {
			return this.get();
		}
	}

	@Getter
	@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
	protected static class PhantomPair<K, V> extends PhantomReference<V> implements Pair<K, V> {
		@EqualsAndHashCode.Include
		protected final K key;
		protected final Instant expireTime;
		protected final PurgeListener<K, V> listener;

		protected PhantomPair(K key, V value, Instant expireTime, PurgeListener<K, V> listener, ReferenceQueue<V> queue) {
			super(value, queue);
			this.key = key;
			this.expireTime = expireTime;
			this.listener = listener;
		}

		/**
		 * 虚引用始终返回 {@code null}
		 */
		@Override
		public V getValue() {
			return this.get();
		}
	}

	public static class Builder<K, V> implements org.zero.common.core.extension.java.lang.Builder<ReferenceLimitedMap<K, V>, Builder<K, V>> {
		/**
		 * 初始容量。默认：16
		 */
		protected int initialCapacity = DEFAULT_INITIAL_CAPACITY;
		/**
		 * 最大容量。默认：65536
		 */
		@Setter
		@Accessors(fluent = true, chain = true)
		protected int maxCapacity = DEFAULT_MAX_CAPACITY;
		/**
		 * 负载系数。默认：0.75
		 */
		protected float loadFactor = DEFAULT_LOAD_FACTOR;
		/**
		 * 是否有序。默认：true
		 */
		protected boolean order = true;
		/**
		 * 是否使用访问顺序。默认：false
		 * <p>
		 * true：访问顺序；false：插入顺序 <br>
		 * 注意：仅当 {@code order = true} 时有效，且该值为 true 时将影响性能。
		 */
		protected boolean accessOrder = false;
		/**
		 * 底层存储
		 */
		@Setter
		@Accessors(fluent = true, chain = true)
		protected Map<K, Pair<K, V>> storage = new LinkedHashMap<>(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, false);
		/**
		 * 引用队列
		 */
		@Setter
		@Accessors(fluent = true, chain = true)
		protected ReferenceQueue<V> referenceQueue = new ReferenceQueue<>();
		/**
		 * 延迟队列
		 */
		@Setter
		@Accessors(fluent = true, chain = true)
		protected DelayQueue<Pair<K, V>> delayQueue = new DelayQueue<>();
		/**
		 * 是否进行惰性清理。默认：false
		 * <p>
		 * 惰性清理：即后台不启动对应清理线程，仅在进行读写操作前清理。
		 */
		protected boolean lazyCleanup;
		/**
		 * 清理任务线程池
		 */
		@Setter
		@Accessors(fluent = true, chain = true)
		protected Executor cleanupExecutor = DEFAULT_CLEANUP_EXECUTOR;

		/**
		 * 引用类型
		 */
		@Setter
		@Accessors(fluent = true, chain = true)
		protected ReferenceType referenceType = ReferenceType.STRONG;
		/**
		 * 缓存 TTL
		 */
		@Setter
		@Accessors(fluent = true, chain = true)
		protected Duration ttl;
		/**
		 * 缓存失效监听器
		 */
		@Setter
		@Accessors(fluent = true, chain = true)
		protected PurgeListener<K, V> listener;

		public Builder<K, V> order() {
			return this.order(true);
		}

		public Builder<K, V> order(boolean order) {
			this.order = order;
			return this.initMap();
		}

		public Builder<K, V> accessOrder() {
			return this.accessOrder(true);
		}

		public Builder<K, V> accessOrder(boolean accessOrder) {
			this.order = true;
			this.accessOrder = accessOrder;
			return this.initMap();
		}

		public Builder<K, V> initialCapacity(int initialCapacity) {
			this.initialCapacity = initialCapacity;
			return this.initMap();
		}

		public Builder<K, V> loadFactor(float loadFactor) {
			this.loadFactor = loadFactor;
			return this.initMap();
		}

		public Builder<K, V> lazyCleanup() {
			return this.lazyCleanup(true);
		}

		public Builder<K, V> lazyCleanup(boolean lazyCleanup) {
			this.lazyCleanup = lazyCleanup;
			return this;
		}

		protected Builder<K, V> initMap() {
			this.storage = order ? new LinkedHashMap<>(initialCapacity, loadFactor, accessOrder) : new HashMap<>(initialCapacity, loadFactor);
			return this;
		}

		@Override
		public ReferenceLimitedMap<K, V> build() {
			return new ReferenceLimitedMap<>(maxCapacity, order, storage, referenceQueue, delayQueue, lazyCleanup, cleanupExecutor, referenceType, ttl, listener);
		}
	}
}
