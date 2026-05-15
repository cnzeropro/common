package org.zero.common.core.extension.java.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
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
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * 线程安全的引用类型有界 Map。
 * <p>
 * 该实现扩展了标准 {@link Map} 语义，额外支持强引用、软引用、弱引用、虚引用、存活时间、最大容量淘汰和
 * {@link PurgeListener} 清理回调。公开读写操作会先执行惰性清理，使已过期或已被 GC 回收的 entry
 * 在调用者视角中尽量表现为不存在。
 * <p>
 * 与普通 {@link Map} 的关键差异：该类允许 {@code null} key/value，并且 presence 判断以内部 pair
 * 是否存在为准，而不是以 {@code value != null} 为准。因此虚引用 entry 的 {@code get(...)} 始终返回
 * {@code null}，但 {@code containsKey(...)}、{@code putIfAbsent(...)}、{@code compute...(...)} 等方法仍会
 * 把它视为已映射。
 * <p>
 * 视图集合为 backed view，迭代器采用快照游标加二次身份校验的弱一致模型；{@code entrySet().add(...)}
 * 不受支持。该类内部使用读写锁保证线程安全，但不实现 {@link java.util.concurrent.ConcurrentMap}，
 * 也不承诺高并发写入性能。
 * <p>
 * 清理监听器在同步显式操作中于状态提交后执行并保留异常外抛；后台过期/引用回收清理会记录并抑制
 * 监听器异常，避免清理任务被业务回调打断。
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/3
 */
@Log
public class ReferenceBoundedMap<K, V> extends AbstractMap<K, V> {

	public static final int DEFAULT_MAX_CAPACITY = 1 << 16;
	public static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
	public static final float DEFAULT_LOAD_FACTOR = 0.75F;
	/**
	 * 默认清理线程池核心线程数。
	 */
	public static final int DEFAULT_CLEANUP_THREAD_POOL_SIZE = 2;
	/**
	 * 默认清理线程工厂。
	 */
	public static final ThreadFactory DEFAULT_CLEANUP_THREAD_FACTORY = DefaultThreadFactory.builder("ReferenceBoundedMap-Cleanup").daemon(Boolean.TRUE).build();
	/**
	 * 默认共享清理执行器。
	 */
	public static final Executor DEFAULT_CLEANUP_EXECUTOR = createDefaultCleanupExecutor();
	/**
	 * 最大容量。
	 * <p>
	 * 写入后若超过该值，会按底层 Map 的迭代顺序淘汰最早 entry。
	 */
	protected final int maxCapacity;
	/**
	 * 是否使用有序 Map。
	 */
	protected final boolean ordered;
	/**
	 * 底层存储。
	 * <p>
	 * value 为内部 pair，用于同时保存引用、过期时间和监听器。
	 */
	protected final Map<K, Pair<K, V>> storage;
	/**
	 * 引用队列。
	 * <p>
	 * 软/弱/虚引用 value 被 GC 处理后，pair 会进入该队列等待清理。
	 */
	protected final ReferenceQueue<V> referenceQueue;
	/**
	 * 延迟队列。
	 * <p>
	 * 仅保存配置了存活时间的 pair。
	 */
	protected final DelayQueue<Pair<K, V>> delayQueue;
	/**
	 * 是否进行惰性清理
	 * <p>
	 * 惰性清理：即后台不启动对应清理线程，仅在进行读写操作前清理。
	 */
	protected final boolean lazyCleanup;
	/**
	 * 清理任务线程池。
	 * <p>
	 * 非惰性清理模式会向该执行器提交引用回收和存活时间过期两个长期任务。
	 */
	protected final Executor cleanupExecutor;
	/**
	 * 默认引用类型。
	 */
	protected final ReferenceType referenceType;
	/**
	 * 默认缓存存活时间。
	 * <p>
	 * {@code null} 表示默认不过期，单次写入方法可覆盖该值。
	 */
	protected final Duration timeToLive;
	/**
	 * 默认缓存失效监听器。
	 */
	protected final PurgeListener<K, V> listener;

	/**
	 * 保护 storage、referenceQueue 间接清理和 delayQueue 一致性的读写锁。
	 */
	protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	/**
	 * 读锁，保护读取和快照采集。
	 */
	protected final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
	/**
	 * 写锁，保护所有结构性修改和复合更新。
	 */
	protected final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
	/**
	 * 标记后台清理循环是否已销毁。
	 */
	protected volatile boolean destroyed;
	/**
	 * 当前引用回收清理线程。
	 */
	protected volatile Thread referenceCleanupThread;
	/**
	 * 当前存活时间过期清理线程。
	 */
	protected volatile Thread expirationCleanupThread;

	/**
	 * 创建引用类型有界 Map。
	 */
	protected ReferenceBoundedMap(int maxCapacity, boolean ordered,
	                              Map<K, Pair<K, V>> storage,
	                              ReferenceQueue<V> referenceQueue, DelayQueue<Pair<K, V>> delayQueue,
	                              boolean lazyCleanup, Executor cleanupExecutor,
	                              ReferenceType referenceType, Duration timeToLive, PurgeListener<K, V> listener) {
		this.maxCapacity = maxCapacity;
		this.ordered = ordered;
		this.storage = storage;
		this.referenceQueue = referenceQueue;
		this.delayQueue = delayQueue;
		this.lazyCleanup = lazyCleanup;
		this.cleanupExecutor = cleanupExecutor;
		this.referenceType = referenceType;
		this.timeToLive = timeToLive;
		this.listener = listener;
		this.submitCleanupTask();
	}

	/**
	 * 使用默认配置创建 Map。
	 */
	public static <K, V> ReferenceBoundedMap<K, V> of() {
		return ReferenceBoundedMap.<K, V>builder().build();
	}

	/**
	 * 使用指定最大容量创建 Map。
	 */
	public static <K, V> ReferenceBoundedMap<K, V> of(int maxCapacity) {
		return ReferenceBoundedMap.<K, V>builder().maxCapacity(maxCapacity).build();
	}

	/**
	 * 使用指定默认引用类型创建 Map。
	 */
	public static <K, V> ReferenceBoundedMap<K, V> of(ReferenceType referenceType) {
		return ReferenceBoundedMap.<K, V>builder().referenceType(referenceType).build();
	}

	/**
	 * 创建构建器。
	 */
	public static <K, V> Builder<K, V> builder() {
		return new Builder<>();
	}

	/**
	 * 创建默认清理执行器。
	 * <p>
	 * 每个非惰性实例会提交引用回收和存活时间过期两个长期任务；这里使用可扩展线程池，避免多个实例共享默认
	 * 执行器时互相占满固定工作线程。
	 */
	protected static Executor createDefaultCleanupExecutor() {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(
				DEFAULT_CLEANUP_THREAD_POOL_SIZE,
				Integer.MAX_VALUE,
				60L,
				TimeUnit.SECONDS,
				new SynchronousQueue<>(),
				DEFAULT_CLEANUP_THREAD_FACTORY
		);
		executor.allowCoreThreadTimeOut(true);
		return executor;
	}

	/**
	 * 获取当前 value。
	 * <p>
	 * 访问前会先执行非阻塞清理；虚引用或已回收引用的 value 可能返回 {@code null}，但 key 在清理前可能仍存在。
	 */
	@Override
	public V get(Object key) {
		this.cleanupInvalidEntries();
		Pair<K, V> pair = this.readPair(key);
		return Objects.nonNull(pair) ? pair.getValue() : null;
	}

	/**
	 * 按 pair presence 获取 value。
	 * <p>
	 * 只要 key 仍映射到 pair，即使 value 为 {@code null} 也返回 {@code null}，不会返回 defaultValue。
	 */
	@Override
	public V getOrDefault(Object key, V defaultValue) {
		this.cleanupInvalidEntries();
		Pair<K, V> pair = this.readPair(key);
		return Objects.nonNull(pair) ? pair.getValue() : defaultValue;
	}

	/**
	 * 使用默认引用类型、存活时间和监听器写入 value。
	 */
	@Override
	public V put(K key, V value) {
		return this.put(key, value, referenceType, timeToLive, listener);
	}

	/**
	 * 使用指定引用类型写入 value。
	 */
	public V put(K key, V value, ReferenceType referenceType) {
		return this.put(key, value, referenceType, timeToLive, listener);
	}

	/**
	 * 使用指定存活时间写入 value。
	 */
	public V put(K key, V value, Duration timeToLive) {
		return this.put(key, value, referenceType, timeToLive, listener);
	}

	/**
	 * 使用指定监听器写入 value。
	 */
	public V put(K key, V value, PurgeListener<K, V> listener) {
		return this.put(key, value, referenceType, timeToLive, listener);
	}

	/**
	 * 使用指定引用类型和存活时间写入 value。
	 */
	public V put(K key, V value, ReferenceType referenceType, Duration timeToLive) {
		return this.put(key, value, referenceType, timeToLive, listener);
	}

	/**
	 * 使用指定引用类型和监听器写入 value。
	 */
	public V put(K key, V value, ReferenceType referenceType, PurgeListener<K, V> listener) {
		return this.put(key, value, referenceType, timeToLive, listener);
	}

	/**
	 * 使用指定存活时间和监听器写入 value。
	 */
	public V put(K key, V value, Duration timeToLive, PurgeListener<K, V> listener) {
		return this.put(key, value, referenceType, timeToLive, listener);
	}

	/**
	 * 写入 value，并指定本次 entry 的引用类型、存活时间和监听器。
	 * <p>
	 * 写入前会先清理无效 entry；写入后如超过最大容量，会按底层 Map 顺序淘汰 entry。
	 * 如果覆盖了当前有效 entry，旧 entry 会按 {@link PurgeReason#REPLACED} 通知。
	 */
	public V put(K key, V value, ReferenceType referenceType, Duration timeToLive, PurgeListener<K, V> listener) {
		this.cleanupInvalidEntries();
		Pair<K, V> newPair = this.createPair(key, value, referenceType, timeToLive, listener);
		Pair<K, V> oldPair;
		Collection<Pair<K, V>> evictedPairs;
		writeLock.lock();
		try {
			oldPair = storage.put(key, newPair);
			this.addToDelayQueueLocked(newPair);
			this.removeFromDelayQueueLocked(oldPair);
			evictedPairs = this.evictExcessEntriesLocked();
		} finally {
			writeLock.unlock();
		}
		this.notifyReplacement(oldPair, false);
		this.notifyPurgeListeners(evictedPairs, PurgeReason.EVICTION, false);
		return Objects.nonNull(oldPair) ? oldPair.getValue() : null;
	}

	/**
	 * key 未映射到 pair 时写入 value。
	 */
	@Override
	public V putIfAbsent(K key, V value) {
		return this.putIfAbsent(key, value, referenceType, timeToLive, listener);
	}

	/**
	 * key 未映射到 pair 时，使用指定引用类型写入 value。
	 */
	public V putIfAbsent(K key, V value, ReferenceType referenceType) {
		return this.putIfAbsent(key, value, referenceType, timeToLive, listener);
	}

	/**
	 * key 未映射到 pair 时，使用指定存活时间写入 value。
	 */
	public V putIfAbsent(K key, V value, Duration timeToLive) {
		return this.putIfAbsent(key, value, referenceType, timeToLive, listener);
	}

	/**
	 * key 未映射到 pair 时，使用指定监听器写入 value。
	 */
	public V putIfAbsent(K key, V value, PurgeListener<K, V> listener) {
		return this.putIfAbsent(key, value, referenceType, timeToLive, listener);
	}

	/**
	 * key 未映射到 pair 时，使用指定引用类型和存活时间写入 value。
	 */
	public V putIfAbsent(K key, V value, ReferenceType referenceType, Duration timeToLive) {
		return this.putIfAbsent(key, value, referenceType, timeToLive, listener);
	}

	/**
	 * key 未映射到 pair 时，使用指定引用类型和监听器写入 value。
	 */
	public V putIfAbsent(K key, V value, ReferenceType referenceType, PurgeListener<K, V> listener) {
		return this.putIfAbsent(key, value, referenceType, timeToLive, listener);
	}

	/**
	 * key 未映射到 pair 时，使用指定存活时间和监听器写入 value。
	 */
	public V putIfAbsent(K key, V value, Duration timeToLive, PurgeListener<K, V> listener) {
		return this.putIfAbsent(key, value, referenceType, timeToLive, listener);
	}

	/**
	 * key 未映射到 pair 时写入 value。
	 * <p>
	 * 与 JDK 默认 {@code Map.putIfAbsent} 的 value-null 语义不同，此处以 pair presence 判断是否存在映射；
	 * 已存在但 value 为 {@code null} 的 entry 不会被覆盖。
	 */
	public V putIfAbsent(K key, V value, ReferenceType referenceType, Duration timeToLive, PurgeListener<K, V> listener) {
		this.cleanupInvalidEntries();
		Pair<K, V> oldPair;
		Collection<Pair<K, V>> evictedPairs = new ArrayList<>();
		writeLock.lock();
		try {
			oldPair = storage.get(key);
			if (Objects.nonNull(oldPair)) {
				return oldPair.getValue();
			}
			Pair<K, V> newPair = this.createPair(key, value, referenceType, timeToLive, listener);
			storage.put(key, newPair);
			this.addToDelayQueueLocked(newPair);
			evictedPairs = this.evictExcessEntriesLocked();
		} finally {
			writeLock.unlock();
		}
		this.notifyPurgeListeners(evictedPairs, PurgeReason.EVICTION, false);
		return null;
	}

	/**
	 * 替换已存在 pair 的 value，并保留原引用类型、存活时间和监听器。
	 */
	@Override
	public V replace(K key, V value) {
		this.cleanupInvalidEntries();
		return this.replaceExisting(key, value, null, null, null, true);
	}

	/**
	 * 替换已存在 pair 的 value，并应用新的引用类型、存活时间和监听器。
	 */
	public V replace(K key, V value, ReferenceType referenceType, Duration timeToLive, PurgeListener<K, V> listener) {
		this.cleanupInvalidEntries();
		return this.replaceExisting(key, value, referenceType, timeToLive, listener, false);
	}

	/**
	 * 仅当 key 当前 value 等于 oldValue 时替换为 newValue。
	 */
	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		this.cleanupInvalidEntries();
		writeLock.lock();
		try {
			Pair<K, V> pair = storage.get(key);
			if (Objects.isNull(pair) || !Objects.equals(pair.getValue(), oldValue)) {
				return false;
			}
			Pair<K, V> newPair = this.recreatePair(pair, newValue);
			storage.put(key, newPair);
			this.removeFromDelayQueueLocked(pair);
			this.addToDelayQueueLocked(newPair);
			return true;
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * 替换所有当前有效 pair 的 value，并保留各自原有元数据。
	 */
	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		Objects.requireNonNull(function, "function");
		this.cleanupInvalidEntries();
		writeLock.lock();
		try {
			for (Map.Entry<K, Pair<K, V>> entry : storage.entrySet()) {
				Pair<K, V> pair = entry.getValue();
				V newValue = function.apply(entry.getKey(), pair.getValue());
				Pair<K, V> newPair = this.recreatePair(pair, newValue);
				entry.setValue(newPair);
				this.removeFromDelayQueueLocked(pair);
				this.addToDelayQueueLocked(newPair);
			}
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		this.putAll(map, referenceType, timeToLive, listener);
	}

	/**
	 * 使用指定引用类型批量写入。
	 */
	public void putAll(Map<? extends K, ? extends V> map, ReferenceType referenceType) {
		this.putAll(map, referenceType, timeToLive, listener);
	}

	/**
	 * 使用指定存活时间批量写入。
	 */
	public void putAll(Map<? extends K, ? extends V> map, Duration timeToLive) {
		this.putAll(map, referenceType, timeToLive, listener);
	}

	/**
	 * 使用指定监听器批量写入。
	 */
	public void putAll(Map<? extends K, ? extends V> map, PurgeListener<K, V> listener) {
		this.putAll(map, referenceType, timeToLive, listener);
	}

	/**
	 * 使用指定引用类型和存活时间批量写入。
	 */
	public void putAll(Map<? extends K, ? extends V> map, ReferenceType referenceType, Duration timeToLive) {
		this.putAll(map, referenceType, timeToLive, listener);
	}

	/**
	 * 使用指定引用类型和监听器批量写入。
	 */
	public void putAll(Map<? extends K, ? extends V> map, ReferenceType referenceType, PurgeListener<K, V> listener) {
		this.putAll(map, referenceType, timeToLive, listener);
	}

	/**
	 * 使用指定存活时间和监听器批量写入。
	 */
	public void putAll(Map<? extends K, ? extends V> map, Duration timeToLive, PurgeListener<K, V> listener) {
		this.putAll(map, referenceType, timeToLive, listener);
	}

	/**
	 * 使用指定引用类型、存活时间和监听器批量写入。
	 * <p>
	 * 该方法逐项调用 {@link #put(Object, Object, ReferenceType, Duration, PurgeListener)}，因此每个 entry
	 * 会独立触发替换通知和容量淘汰。
	 */
	public void putAll(Map<? extends K, ? extends V> map, ReferenceType referenceType, Duration timeToLive, PurgeListener<K, V> listener) {
		map.forEach((key, value) -> this.put(key, value, referenceType, timeToLive, listener));
	}

	/**
	 * 删除 key 当前映射。
	 * <p>
	 * 已过期 entry 会按过期原因通知并返回 {@code null}，与 {@link #get(Object)} 的逻辑视图保持一致。
	 */
	@Override
	public V remove(Object key) {
		Pair<K, V> pair = this.removeByKey(key, PurgeReason.EXPLICIT, false);
		return Objects.nonNull(pair) ? pair.getValue() : null;
	}

	/**
	 * 当 key 当前 value 等于指定 value 时删除映射。
	 */
	@Override
	public boolean remove(Object key, Object value) {
		this.cleanupInvalidEntries();
		Pair<K, V> removedPair = null;
		writeLock.lock();
		try {
			Pair<K, V> pair = storage.get(key);
			if (Objects.isNull(pair) || !Objects.equals(pair.getValue(), value)) {
				return false;
			}
			removedPair = this.removeCurrentPairLocked(pair, true);
		} finally {
			writeLock.unlock();
		}
		this.notifyListener(removedPair, PurgeReason.EXPLICIT, false);
		return Objects.nonNull(removedPair);
	}

	/**
	 * 清空所有映射。
	 * <p>
	 * 已过期 entry 按 {@link PurgeReason#EXPIRED} 通知，仍有效 entry 按 {@link PurgeReason#CLEARED} 通知。
	 */
	@Override
	public void clear() {
		Collection<Pair<K, V>> pairs;
		writeLock.lock();
		try {
			pairs = new ArrayList<>(storage.values());
			storage.clear();
			delayQueue.clear();
		} finally {
			writeLock.unlock();
		}
		pairs.forEach(pair -> this.notifyListener(
				pair,
				pair.isExpired() ? PurgeReason.EXPIRED : PurgeReason.CLEARED,
				false
		));
	}

	/**
	 * 返回当前有效映射数量。
	 */
	@Override
	public int size() {
		this.cleanupInvalidEntries();
		readLock.lock();
		try {
			return storage.size();
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 判断 key 是否当前映射到 pair。
	 */
	@Override
	public boolean containsKey(Object key) {
		this.cleanupInvalidEntries();
		readLock.lock();
		try {
			return storage.containsKey(key);
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 判断是否存在当前 value 等于指定 value 的映射。
	 * <p>
	 * 该类允许 {@code null} value，因此 {@code containsValue(null)} 可能返回 {@code true}。
	 */
	@Override
	public boolean containsValue(Object value) {
		this.cleanupInvalidEntries();
		readLock.lock();
		try {
			for (Pair<K, V> pair : storage.values()) {
				if (Objects.equals(pair.getValue(), value)) {
					return true;
				}
			}
		} finally {
			readLock.unlock();
		}
		return false;
	}

	/**
	 * 返回 backed key 视图。
	 */
	@Override
	public Set<K> keySet() {
		return new KeySet();
	}

	/**
	 * 返回 backed value 视图。
	 */
	@Override
	public Collection<V> values() {
		return new ValueCollection();
	}

	/**
	 * 返回 backed entry 视图。
	 */
	@Override
	public Set<Entry<K, V>> entrySet() {
		return new EntrySet();
	}

	/**
	 * 判断当前是否没有有效映射。
	 */
	@Override
	public boolean isEmpty() {
		this.cleanupInvalidEntries();
		readLock.lock();
		try {
			return storage.isEmpty();
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 对当前有效映射执行遍历回调。
	 */
	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		Objects.requireNonNull(action, "action");
		for (Pair<K, V> pair : this.snapshotPairs()) {
			action.accept(pair.getKey(), pair.getValue());
		}
	}

	/**
	 * key 未映射到 pair 时，根据 mappingFunction 计算并写入新 value。
	 * <p>
	 * 与 JDK 默认方法不同，已存在但 value 为 {@code null} 的 pair 仍视为 present，不会调用 mappingFunction。
	 */
	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		Objects.requireNonNull(mappingFunction, "mappingFunction");
		this.cleanupInvalidEntries();
		V result = null;
		Collection<Pair<K, V>> evictedPairs = new ArrayList<>();
		writeLock.lock();
		try {
			Pair<K, V> pair = storage.get(key);
			if (Objects.nonNull(pair)) {
				result = pair.getValue();
			} else {
				V newValue = mappingFunction.apply(key);
				if (Objects.nonNull(newValue)) {
					Pair<K, V> newPair = this.createPair(key, newValue, referenceType, timeToLive, listener);
					storage.put(key, newPair);
					this.addToDelayQueueLocked(newPair);
					evictedPairs = this.evictExcessEntriesLocked();
					result = newValue;
				}
			}
		} finally {
			writeLock.unlock();
		}
		this.notifyPurgeListeners(evictedPairs, PurgeReason.EVICTION, false);
		return result;
	}

	/**
	 * key 当前映射到 pair 时重新计算 value。
	 * <p>
	 * remappingFunction 返回 {@code null} 表示删除该 pair。
	 */
	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		Objects.requireNonNull(remappingFunction, "remappingFunction");
		this.cleanupInvalidEntries();
		Pair<K, V> removedPair = null;
		V result = null;
		writeLock.lock();
		try {
			Pair<K, V> pair = storage.get(key);
			if (Objects.nonNull(pair)) {
				V newValue = remappingFunction.apply(key, pair.getValue());
				if (Objects.isNull(newValue)) {
					removedPair = this.removeCurrentPairLocked(pair, true);
				} else {
					Pair<K, V> newPair = this.recreatePair(pair, newValue);
					storage.put(key, newPair);
					this.removeFromDelayQueueLocked(pair);
					this.addToDelayQueueLocked(newPair);
					result = newValue;
				}
			}
		} finally {
			writeLock.unlock();
		}
		this.notifyListener(removedPair, PurgeReason.EXPLICIT, false);
		return result;
	}

	/**
	 * 按 pair presence 重新计算映射。
	 * <p>
	 * 旧 value 可能为 {@code null}；计算结果为 {@code null} 表示删除当前 pair 或不建立新映射。
	 */
	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		Objects.requireNonNull(remappingFunction, "remappingFunction");
		this.cleanupInvalidEntries();
		Pair<K, V> removedPair = null;
		V result = null;
		Collection<Pair<K, V>> evictedPairs = new ArrayList<>();
		writeLock.lock();
		try {
			Pair<K, V> pair = storage.get(key);
			V oldValue = Objects.nonNull(pair) ? pair.getValue() : null;
			V newValue = remappingFunction.apply(key, oldValue);
			if (Objects.isNull(newValue)) {
				removedPair = this.removeCurrentPairLocked(pair, true);
			} else if (Objects.nonNull(pair)) {
				Pair<K, V> newPair = this.recreatePair(pair, newValue);
				storage.put(key, newPair);
				this.removeFromDelayQueueLocked(pair);
				this.addToDelayQueueLocked(newPair);
				result = newValue;
			} else {
				Pair<K, V> newPair = this.createPair(key, newValue, referenceType, timeToLive, listener);
				storage.put(key, newPair);
				this.addToDelayQueueLocked(newPair);
				evictedPairs = this.evictExcessEntriesLocked();
				result = newValue;
			}
		} finally {
			writeLock.unlock();
		}
		this.notifyListener(removedPair, PurgeReason.EXPLICIT, false);
		this.notifyPurgeListeners(evictedPairs, PurgeReason.EVICTION, false);
		return result;
	}

	/**
	 * 按 pair presence 合并映射。
	 * <p>
	 * key 已映射到 pair 时会调用 remappingFunction，即使旧 value 为 {@code null}；value 参数本身不允许为
	 * {@code null}，保持 {@link Map#merge(Object, Object, BiFunction)} 约定。
	 */
	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		Objects.requireNonNull(value, "value");
		Objects.requireNonNull(remappingFunction, "remappingFunction");
		this.cleanupInvalidEntries();
		Pair<K, V> removedPair = null;
		V result;
		Collection<Pair<K, V>> evictedPairs = new ArrayList<>();
		writeLock.lock();
		try {
			Pair<K, V> pair = storage.get(key);
			V newValue = Objects.nonNull(pair) ? remappingFunction.apply(pair.getValue(), value) : value;
			if (Objects.isNull(newValue)) {
				removedPair = this.removeCurrentPairLocked(pair, true);
				result = null;
			} else if (Objects.nonNull(pair)) {
				Pair<K, V> newPair = this.recreatePair(pair, newValue);
				storage.put(key, newPair);
				this.removeFromDelayQueueLocked(pair);
				this.addToDelayQueueLocked(newPair);
				result = newValue;
			} else {
				Pair<K, V> newPair = this.createPair(key, newValue, referenceType, timeToLive, listener);
				storage.put(key, newPair);
				this.addToDelayQueueLocked(newPair);
				evictedPairs = this.evictExcessEntriesLocked();
				result = newValue;
			}
		} finally {
			writeLock.unlock();
		}
		this.notifyListener(removedPair, PurgeReason.EXPLICIT, false);
		this.notifyPurgeListeners(evictedPairs, PurgeReason.EVICTION, false);
		return result;
	}

	protected void submitCleanupTask() {
		if (!lazyCleanup) {
			cleanupExecutor.execute(this::runReferenceCleanupLoop);
			cleanupExecutor.execute(this::runExpirationCleanupLoop);
		}
	}

	/**
	 * 运行引用队列后台清理循环。
	 */
	protected void runReferenceCleanupLoop() {
		Thread currentThread = Thread.currentThread();
		this.registerReferenceCleanupThread(currentThread);
		try {
			new LoopRunnable(() -> {
				this.throwIfDestroyed();
				this.cleanupCollectedEntries(true);
			}).run();
		} finally {
			this.clearReferenceCleanupThread(currentThread);
		}
	}

	/**
	 * 运行存活时间过期后台清理循环。
	 */
	protected void runExpirationCleanupLoop() {
		Thread currentThread = Thread.currentThread();
		this.registerExpirationCleanupThread(currentThread);
		try {
			new LoopRunnable(() -> {
				this.throwIfDestroyed();
				this.cleanupExpiredEntries(true);
			}).run();
		} finally {
			this.clearExpirationCleanupThread(currentThread);
		}
	}

	/**
	 * 如果当前 Map 已销毁，则终止后台清理循环。
	 */
	protected void throwIfDestroyed() throws InterruptedException {
		if (destroyed) {
			throw new InterruptedException("Cleanup loop has been destroyed.");
		}
	}

	/**
	 * 记录引用队列后台清理线程，便于 {@link #destroy()} 精准中断。
	 */
	protected void registerReferenceCleanupThread(Thread thread) {
		referenceCleanupThread = thread;
	}

	/**
	 * 记录存活时间过期后台清理线程，便于 {@link #destroy()} 精准中断。
	 */
	protected void registerExpirationCleanupThread(Thread thread) {
		expirationCleanupThread = thread;
	}

	/**
	 * 清除引用队列后台清理线程引用。
	 */
	protected void clearReferenceCleanupThread(Thread thread) {
		if (referenceCleanupThread == thread) {
			referenceCleanupThread = null;
		}
	}

	/**
	 * 清除存活时间过期后台清理线程引用。
	 */
	protected void clearExpirationCleanupThread(Thread thread) {
		if (expirationCleanupThread == thread) {
			expirationCleanupThread = null;
		}
	}

	/**
	 * 创建 {@link Pair} 对象
	 */
	protected Pair<K, V> createPair(K key, V value, ReferenceType referenceType, Duration timeToLive, PurgeListener<K, V> listener) {
		Instant expireTime = Objects.nonNull(timeToLive) ? Instant.now().plus(timeToLive) : null;
		return this.createPair(key, value, referenceType, expireTime, listener);
	}

	/**
	 * 按指定过期时间创建 {@link Pair} 对象。
	 */
	protected Pair<K, V> createPair(K key, V value, ReferenceType referenceType, Instant expireTime, PurgeListener<K, V> listener) {
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
	 * 使用旧 pair 的元数据重建 value。
	 */
	protected Pair<K, V> recreatePair(Pair<K, V> pair, V value) {
		return this.createPair(pair.getKey(), value, this.referenceTypeOf(pair), pair.getExpireTime(), pair.getListener());
	}

	/**
	 * 根据 pair 实际类型反推引用类型。
	 * <p>
	 * 替换值时需要保留旧 entry 的引用类型、过期时间和监听器，因此不能直接使用 Map 级默认配置重建 pair。
	 */
	protected ReferenceType referenceTypeOf(Pair<K, V> pair) {
		if (pair instanceof StrongPair) {
			return ReferenceType.STRONG;
		}
		if (pair instanceof SoftPair) {
			return ReferenceType.SOFT;
		}
		if (pair instanceof WeakPair) {
			return ReferenceType.WEAK;
		}
		if (pair instanceof PhantomPair) {
			return ReferenceType.PHANTOM;
		}
		throw new AssertionError("Unsupported pair type: " + pair.getClass().getName());
	}

	/**
	 * 清理无效键值对
	 */
	protected void cleanupInvalidEntries() {
		this.cleanupCollectedEntries(false);
		this.cleanupExpiredEntries(false);
	}

	/**
	 * 清理由 GC 收集的键值对。
	 *
	 * @param blocking 是否阻塞等待引用队列；后台清理传 {@code true}，惰性清理传 {@code false}
	 */
	protected void cleanupCollectedEntries(boolean blocking) {
		while (true) {
			Reference<?> reference;
			try {
				reference = blocking ? referenceQueue.remove() : referenceQueue.poll();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
			if (Objects.isNull(reference)) {
				return;
			}
			if (reference instanceof Pair) {
				@SuppressWarnings("unchecked")
				Pair<K, V> pair = (Pair<K, V>) reference;
				Pair<K, V> removedPair;
				writeLock.lock();
				try {
					removedPair = this.removeCurrentPairLocked(pair, true);
				} finally {
					writeLock.unlock();
				}
				this.notifyListener(removedPair, PurgeReason.COLLECTED, blocking);
			}
		}
	}

	/**
	 * 清理过期的键值对。
	 *
	 * @param blocking 是否阻塞等待延迟队列；后台清理传 {@code true}，惰性清理传 {@code false}
	 */
	protected void cleanupExpiredEntries(boolean blocking) {
		while (true) {
			Pair<K, V> pair;
			try {
				pair = blocking ? delayQueue.take() : delayQueue.poll();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
			if (Objects.isNull(pair)) {
				return;
			}
			Pair<K, V> removedPair;
			writeLock.lock();
			try {
				removedPair = this.removeCurrentPairLocked(pair, false);
			} finally {
				writeLock.unlock();
			}
			this.notifyListener(removedPair, PurgeReason.EXPIRED, blocking);
		}
	}

	/**
	 * 淘汰超出最大容量的键值对。
	 */
	protected Collection<Pair<K, V>> evictExcessEntriesLocked() {
		Collection<Pair<K, V>> evictedPairs = new ArrayList<>();
		while (storage.size() > maxCapacity) {
			Iterator<Map.Entry<K, Pair<K, V>>> iterator = storage.entrySet().iterator();
			if (!iterator.hasNext()) {
				break;
			}
			Map.Entry<K, Pair<K, V>> entry = iterator.next();
			Pair<K, V> pair = entry.getValue();
			iterator.remove();
			this.removeFromDelayQueueLocked(pair);
			evictedPairs.add(pair);
		}
		return evictedPairs;
	}

	/**
	 * 替换当前已存在 pair。
	 *
	 * @param preserveMetadata 是否保留旧 pair 的引用类型、过期时间和监听器
	 */
	protected V replaceExisting(K key, V value, ReferenceType referenceType, Duration timeToLive, PurgeListener<K, V> listener, boolean preserveMetadata) {
		Pair<K, V> oldPair = null;
		writeLock.lock();
		try {
			oldPair = storage.get(key);
			if (Objects.isNull(oldPair)) {
				return null;
			}
			Pair<K, V> newPair = preserveMetadata
					? this.recreatePair(oldPair, value)
					: this.createPair(key, value, referenceType, timeToLive, listener);
			storage.put(key, newPair);
			this.removeFromDelayQueueLocked(oldPair);
			this.addToDelayQueueLocked(newPair);
			return oldPair.getValue();
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * 在读锁保护下读取 pair。
	 */
	protected Pair<K, V> readPair(Object key) {
		readLock.lock();
		try {
			return storage.get(key);
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 获取当前有效 pair 快照。
	 * <p>
	 * 快照用于弱一致视图遍历；返回后仍需在使用点进行 pair 身份校验。
	 */
	protected ArrayList<Pair<K, V>> snapshotPairs() {
		this.cleanupInvalidEntries();
		ArrayList<Pair<K, V>> pairs = new ArrayList<>();
		readLock.lock();
		try {
			pairs.addAll(storage.values());
		} finally {
			readLock.unlock();
		}
		return pairs;
	}

	/**
	 * 判断快照中的 pair 是否仍是当前映射。
	 * <p>
	 * 延迟队列、引用队列和视图迭代器都可能持有旧 pair；这里只按对象身份判断，避免旧代 entry 误删或误返回新映射。
	 */
	protected boolean isCurrentPair(Pair<K, V> pair) {
		readLock.lock();
		try {
			return storage.get(pair.getKey()) == pair;
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 将带过期时间的 pair 加入延迟队列。
	 */
	protected void addToDelayQueueLocked(Pair<K, V> pair) {
		if (Objects.nonNull(pair) && Objects.nonNull(pair.getExpireTime())) {
			delayQueue.add(pair);
		}
	}

	/**
	 * 从延迟队列移除带过期时间的 pair。
	 */
	protected void removeFromDelayQueueLocked(Pair<K, V> pair) {
		if (Objects.nonNull(pair) && Objects.nonNull(pair.getExpireTime())) {
			delayQueue.remove(pair);
		}
	}

	/**
	 * 按 pair 身份删除当前映射。
	 * <p>
	 * 后台清理、容量淘汰和视图删除都应通过该方法收口，禁止只按 key 删除，
	 * 否则旧 pair 可能在稍后出队时删掉同 key 的新值。
	 */
	protected Pair<K, V> removeCurrentPairLocked(Pair<K, V> pair, boolean removeDelayQueue) {
		if (Objects.isNull(pair)) {
			return null;
		}
		Pair<K, V> current = storage.get(pair.getKey());
		if (current != pair) {
			return null;
		}
		storage.remove(pair.getKey());
		if (removeDelayQueue) {
			this.removeFromDelayQueueLocked(pair);
		}
		return pair;
	}

	/**
	 * 显式按 key 删除映射。
	 * <p>
	 * 如果删除点发现 entry 已经过期，则返回 {@code null} 并按 {@link PurgeReason#EXPIRED} 通知，
	 * 使 {@link #remove(Object)} 与 {@link #get(Object)} 对过期 entry 的逻辑视图保持一致。
	 */
	protected Pair<K, V> removeByKey(Object key, PurgeReason reason, boolean suppressListenerException) {
		Pair<K, V> removedPair;
		writeLock.lock();
		try {
			removedPair = storage.remove(key);
			this.removeFromDelayQueueLocked(removedPair);
		} finally {
			writeLock.unlock();
		}
		if (Objects.isNull(removedPair)) {
			return null;
		}
		if (removedPair.isExpired()) {
			this.notifyListener(removedPair, PurgeReason.EXPIRED, suppressListenerException);
			return null;
		}
		this.notifyListener(removedPair, reason, suppressListenerException);
		return removedPair;
	}

	/**
	 * 按 pair 身份删除当前映射并通知监听器。
	 */
	protected Pair<K, V> removeCurrentPair(Pair<K, V> pair, PurgeReason reason, boolean removeDelayQueue, boolean suppressListenerException) {
		Pair<K, V> removedPair;
		writeLock.lock();
		try {
			removedPair = this.removeCurrentPairLocked(pair, removeDelayQueue);
		} finally {
			writeLock.unlock();
		}
		this.notifyListener(removedPair, reason, suppressListenerException);
		return removedPair;
	}

	/**
	 * 批量通知清理监听器。
	 */
	protected void notifyPurgeListeners(Collection<Pair<K, V>> pairs, PurgeReason reason, boolean suppressListenerException) {
		pairs.forEach(pair -> this.notifyListener(pair, reason, suppressListenerException));
	}

	/**
	 * 通知被替换的旧 entry。
	 * <p>
	 * 如果旧 entry 在被替换时已经过期，则它从调用者视角属于过期清理而非替换。
	 */
	protected void notifyReplacement(Pair<K, V> oldPair, boolean suppressListenerException) {
		if (Objects.isNull(oldPair)) {
			return;
		}
		this.notifyListener(oldPair, oldPair.isExpired() ? PurgeReason.EXPIRED : PurgeReason.REPLACED, suppressListenerException);
	}

	/**
	 * 通知监听器
	 *
	 * @param suppressListenerException 是否抑制监听器异常；后台异步清理为 {@code true}
	 */
	protected void notifyListener(Pair<K, V> pair, PurgeReason reason, boolean suppressListenerException) {
		if (Objects.isNull(pair)) {
			return;
		}
		PurgeListener<K, V> listener = pair.getListener();
		if (Objects.isNull(listener)) {
			return;
		}
		try {
			listener.onPurge(pair.getKey(), pair.getValue(), reason);
		} catch (RuntimeException e) {
			if (!suppressListenerException) {
				throw e;
			}
			log.log(Level.WARNING, "Purge listener threw exception during async cleanup.", e);
		}
	}

	protected void destroy() throws Exception {
		destroyed = true;
		Thread referenceThread = referenceCleanupThread;
		Thread expirationThread = expirationCleanupThread;
		if (Objects.nonNull(referenceThread)) {
			referenceThread.interrupt();
		}
		if (Objects.nonNull(expirationThread) && expirationThread != referenceThread) {
			expirationThread.interrupt();
		}
		writeLock.lock();
		try {
			storage.clear();
			delayQueue.clear();
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * 作为兜底保护释放后台清理任务。
	 */
	@Override
	protected void finalize() throws Throwable {
		this.destroy();
	}

	/**
	 * 内部存储节点。
	 * <p>
	 * 该接口把“映射是否存在”和“当前 value 是否可取到”解耦：弱/软/虚引用被回收后，
	 * pair 可能仍短暂存在于 storage 中，随后由引用队列或惰性清理移除。
	 */
	protected interface Pair<K, V> extends InstantDelayed {
		K getKey();

		V getValue();

		PurgeListener<K, V> getListener();
	}

	/**
	 * 强引用节点，value 生命周期由 Map 映射本身持有。
	 */
	@Getter
	@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
	protected static class StrongPair<K, V> implements Pair<K, V> {
		protected final K key;
		protected final V value;
		protected final Instant expireTime;
		protected final PurgeListener<K, V> listener;
	}

	/**
	 * 软引用节点，value 可在内存压力下被 GC 回收。
	 */
	@Getter
	protected static class SoftPair<K, V> extends SoftReference<V> implements Pair<K, V> {
		protected final K key;
		protected final Instant expireTime;
		protected final PurgeListener<K, V> listener;

		/**
		 * 创建软引用节点。
		 */
		protected SoftPair(K key, V value, Instant expireTime, PurgeListener<K, V> listener, ReferenceQueue<V> queue) {
			super(value, queue);
			this.key = key;
			this.expireTime = expireTime;
			this.listener = listener;
		}

		/**
		 * 返回软引用当前可达 value；已被 GC 回收时返回 {@code null}。
		 */
		@Override
		public V getValue() {
			return this.get();
		}
	}

	/**
	 * 弱引用节点，value 不再被外部强引用持有后可被 GC 回收。
	 */
	@Getter
	protected static class WeakPair<K, V> extends WeakReference<V> implements Pair<K, V> {
		protected final K key;
		protected final Instant expireTime;
		protected final PurgeListener<K, V> listener;

		/**
		 * 创建弱引用节点。
		 */
		protected WeakPair(K key, V value, Instant expireTime, PurgeListener<K, V> listener, ReferenceQueue<V> queue) {
			super(value, queue);
			this.key = key;
			this.expireTime = expireTime;
			this.listener = listener;
		}

		/**
		 * 返回弱引用当前可达 value；已被 GC 回收时返回 {@code null}。
		 */
		@Override
		public V getValue() {
			return this.get();
		}
	}

	/**
	 * 虚引用节点。
	 * <p>
	 * 虚引用不会通过 {@link #getValue()} 暴露 value，但 pair 本身仍代表 key 存在映射，直到引用队列清理。
	 */
	@Getter
	protected static class PhantomPair<K, V> extends PhantomReference<V> implements Pair<K, V> {
		protected final K key;
		protected final Instant expireTime;
		protected final PurgeListener<K, V> listener;

		/**
		 * 创建虚引用节点。
		 */
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

	/**
	 * {@link ReferenceBoundedMap} 构建器。
	 */
	public static class Builder<K, V> implements org.zero.common.core.extension.java.lang.Builder<ReferenceBoundedMap<K, V>, Builder<K, V>> {
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
		protected boolean ordered = true;
		/**
		 * 是否使用访问顺序。默认：false
		 * <p>
		 * true：访问顺序；false：插入顺序 <br>
		 * 注意：仅当 {@code ordered = true} 时有效，且该值为 true 时将影响性能。
		 */
		protected boolean accessOrder = false;
		/**
		 * 底层存储。
		 * <p>
		 * 默认使用插入顺序 {@link LinkedHashMap}，容量淘汰依赖该 Map 的迭代顺序。
		 */
		@Setter
		@Accessors(fluent = true, chain = true)
		protected Map<K, Pair<K, V>> storage = new LinkedHashMap<>(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, false);
		/**
		 * 引用队列。
		 */
		@Setter
		@Accessors(fluent = true, chain = true)
		protected ReferenceQueue<V> referenceQueue = new ReferenceQueue<>();
		/**
		 * 延迟队列。
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
		 * 清理任务线程池。
		 */
		@Setter
		@Accessors(fluent = true, chain = true)
		protected Executor cleanupExecutor = DEFAULT_CLEANUP_EXECUTOR;

		/**
		 * 默认引用类型。
		 */
		@Setter
		@Accessors(fluent = true, chain = true)
		protected ReferenceType referenceType = ReferenceType.STRONG;
		/**
		 * 默认缓存存活时间。
		 */
		@Setter
		@Accessors(fluent = true, chain = true)
		protected Duration timeToLive;
		/**
		 * 默认缓存失效监听器。
		 */
		@Setter
		@Accessors(fluent = true, chain = true)
		protected PurgeListener<K, V> listener;

		/**
		 * 开启有序存储。
		 */
		public Builder<K, V> ordered() {
			return this.ordered(true);
		}

		/**
		 * 设置是否使用有序存储。
		 */
		public Builder<K, V> ordered(boolean ordered) {
			this.ordered = ordered;
			return this.initStorage();
		}

		/**
		 * 开启访问顺序存储。
		 */
		public Builder<K, V> accessOrder() {
			return this.accessOrder(true);
		}

		/**
		 * 设置是否使用访问顺序。
		 */
		public Builder<K, V> accessOrder(boolean accessOrder) {
			this.ordered = true;
			this.accessOrder = accessOrder;
			return this.initStorage();
		}

		/**
		 * 设置初始容量。
		 */
		public Builder<K, V> initialCapacity(int initialCapacity) {
			this.initialCapacity = initialCapacity;
			return this.initStorage();
		}

		/**
		 * 设置负载系数。
		 */
		public Builder<K, V> loadFactor(float loadFactor) {
			this.loadFactor = loadFactor;
			return this.initStorage();
		}

		/**
		 * 开启惰性清理。
		 */
		public Builder<K, V> lazyCleanup() {
			return this.lazyCleanup(true);
		}

		/**
		 * 设置是否惰性清理。
		 */
		public Builder<K, V> lazyCleanup(boolean lazyCleanup) {
			this.lazyCleanup = lazyCleanup;
			return this;
		}

		/**
		 * 根据当前容量和顺序配置重建默认底层 Map。
		 */
		protected Builder<K, V> initStorage() {
			this.storage = ordered ? new LinkedHashMap<>(initialCapacity, loadFactor, accessOrder) : new HashMap<>(initialCapacity, loadFactor);
			return this;
		}

		/**
		 * 构建 {@link ReferenceBoundedMap}。
		 */
		@Override
		public ReferenceBoundedMap<K, V> build() {
			return new ReferenceBoundedMap<>(maxCapacity, ordered, storage, referenceQueue, delayQueue, lazyCleanup, cleanupExecutor, referenceType, timeToLive, listener);
		}
	}

	/**
	 * 基于快照的弱一致迭代器。
	 * <p>
	 * 快照只保存遍历候选，真正返回前仍会确认 pair 是当前映射；若候选已过期，则顺手按过期原因清理。
	 * {@link #remove()} 回写原 Map，且只允许删除最近一次 {@link #next()} 返回的元素。
	 */
	protected abstract class SnapshotIterator<E> implements Iterator<E> {
		protected final ArrayList<Pair<K, V>> snapshot = ReferenceBoundedMap.this.snapshotPairs();
		protected int cursor;
		protected Pair<K, V> nextPair;
		protected Pair<K, V> currentPair;
		protected boolean nextReady;

		/**
		 * 判断快照中是否还有当前有效的下一个元素。
		 */
		@Override
		public boolean hasNext() {
			if (nextReady) {
				return true;
			}
			while (cursor < snapshot.size()) {
				Pair<K, V> pair = snapshot.get(cursor++);
				if (ReferenceBoundedMap.this.isCurrentPair(pair)) {
					if (pair.isExpired()) {
						ReferenceBoundedMap.this.removeCurrentPair(pair, PurgeReason.EXPIRED, true, false);
						continue;
					}
					nextPair = pair;
					nextReady = true;
					return true;
				}
			}
			return false;
		}

		/**
		 * 返回下一个当前有效元素。
		 */
		@Override
		public E next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}
			Pair<K, V> pair = nextPair;
			nextPair = null;
			nextReady = false;
			currentPair = pair;
			return this.convert(pair);
		}

		/**
		 * 删除最近一次 {@link #next()} 返回的元素。
		 */
		@Override
		public void remove() {
			if (Objects.isNull(currentPair)) {
				throw new IllegalStateException();
			}
			ReferenceBoundedMap.this.removeByKey(currentPair.getKey(), PurgeReason.EXPLICIT, false);
			currentPair = null;
		}

		/**
		 * 将内部 pair 转换为视图元素。
		 */
		protected abstract E convert(Pair<K, V> pair);
	}

	protected class EntryIterator extends SnapshotIterator<Entry<K, V>> {
		/**
		 * 将内部 pair 包装为安全 entry 视图。
		 */
		@Override
		protected Entry<K, V> convert(Pair<K, V> pair) {
			return new EntryView(pair);
		}
	}

	protected class KeyIterator extends SnapshotIterator<K> {
		/**
		 * 返回 pair 的 key。
		 */
		@Override
		protected K convert(Pair<K, V> pair) {
			return pair.getKey();
		}
	}

	protected class ValueIterator extends SnapshotIterator<V> {
		/**
		 * 返回 pair 的当前 value。
		 */
		@Override
		protected V convert(Pair<K, V> pair) {
			return pair.getValue();
		}
	}

	/**
	 * 对外暴露的 entry 视图。
	 * <p>
	 * 该对象只保存 key 与期望 pair 身份，{@link #setValue(Object)} 会重新回到 Map 内部加锁替换 pair，
	 * 避免调用者直接修改内部节点，同时保留旧节点的引用类型、过期时间和监听器。
	 */
	protected class EntryView implements Entry<K, V> {
		protected final K key;
		protected final Pair<K, V> expectedPair;

		/**
		 * 创建 entry 视图。
		 */
		protected EntryView(Pair<K, V> pair) {
			this.key = pair.getKey();
			this.expectedPair = pair;
		}

		/**
		 * 返回 entry key。
		 */
		@Override
		public K getKey() {
			return key;
		}

		/**
		 * 返回当前 value；value 本身允许为 {@code null}。
		 */
		@Override
		public V getValue() {
			return ReferenceBoundedMap.this.get(key);
		}

		/**
		 * 回写当前 entry value。
		 * <p>
		 * 若 entry 已被替换或已过期，则抛出 {@link IllegalStateException}。
		 */
		@Override
		public V setValue(V value) {
			Pair<K, V> expiredPair = null;
			writeLock.lock();
			try {
				Pair<K, V> current = storage.get(key);
				if (current != expectedPair) {
					throw new IllegalStateException("Entry is no longer valid.");
				}
				if (current.isExpired()) {
					expiredPair = ReferenceBoundedMap.this.removeCurrentPairLocked(current, true);
					throw new IllegalStateException("Entry is no longer valid.");
				}
				Pair<K, V> newPair = ReferenceBoundedMap.this.recreatePair(current, value);
				storage.put(key, newPair);
				ReferenceBoundedMap.this.removeFromDelayQueueLocked(current);
				ReferenceBoundedMap.this.addToDelayQueueLocked(newPair);
				return current.getValue();
			} finally {
				writeLock.unlock();
				ReferenceBoundedMap.this.notifyListener(expiredPair, PurgeReason.EXPIRED, false);
			}
		}

		/**
		 * 按 Map.Entry 值语义比较。
		 */
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Entry)) {
				return false;
			}
			Entry<?, ?> other = (Entry<?, ?>) obj;
			return Objects.equals(this.getKey(), other.getKey()) && Objects.equals(this.getValue(), other.getValue());
		}

		/**
		 * 按 Map.Entry 值语义计算哈希。
		 */
		@Override
		public int hashCode() {
			return Objects.hashCode(this.getKey()) ^ Objects.hashCode(this.getValue());
		}
	}

	/**
	 * backed entry 视图。
	 */
	protected class EntrySet extends AbstractSet<Entry<K, V>> {
		/**
		 * 返回弱一致 entry 迭代器。
		 */
		@Override
		public Iterator<Entry<K, V>> iterator() {
			return new EntryIterator();
		}

		/**
		 * 返回当前有效 entry 数量。
		 */
		@Override
		public int size() {
			return ReferenceBoundedMap.this.size();
		}

		/**
		 * 判断当前是否没有有效 entry。
		 */
		@Override
		public boolean isEmpty() {
			return ReferenceBoundedMap.this.isEmpty();
		}

		/**
		 * 清空原 Map。
		 */
		@Override
		public void clear() {
			ReferenceBoundedMap.this.clear();
		}

		/**
		 * entry 视图不支持新增。
		 */
		@Override
		public boolean add(Entry<K, V> entry) {
			throw new UnsupportedOperationException("entrySet view does not support add.");
		}

		/**
		 * 判断 entry 是否存在于当前映射中。
		 */
		@Override
		public boolean contains(Object o) {
			if (!(o instanceof Entry)) {
				return false;
			}
			Entry<?, ?> entry = (Entry<?, ?>) o;
			Object value = ReferenceBoundedMap.this.get(entry.getKey());
			if (Objects.nonNull(value)) {
				return Objects.equals(value, entry.getValue());
			}
			return Objects.equals(entry.getValue(), null) && ReferenceBoundedMap.this.containsKey(entry.getKey());
		}

		/**
		 * 当 key/value 均匹配时从原 Map 删除 entry。
		 */
		@Override
		public boolean remove(Object o) {
			if (!(o instanceof Entry)) {
				return false;
			}
			Entry<?, ?> entry = (Entry<?, ?>) o;
			return ReferenceBoundedMap.this.remove(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * backed key 视图。
	 */
	protected class KeySet extends AbstractSet<K> {
		/**
		 * 返回弱一致 key 迭代器。
		 */
		@Override
		public Iterator<K> iterator() {
			return new KeyIterator();
		}

		/**
		 * 返回当前有效 key 数量。
		 */
		@Override
		public int size() {
			return ReferenceBoundedMap.this.size();
		}

		/**
		 * 判断当前是否没有有效 key。
		 */
		@Override
		public boolean isEmpty() {
			return ReferenceBoundedMap.this.isEmpty();
		}

		/**
		 * 判断 key 是否存在于当前映射中。
		 */
		@Override
		public boolean contains(Object o) {
			return ReferenceBoundedMap.this.containsKey(o);
		}

		/**
		 * 从原 Map 删除 key。
		 */
		@Override
		public boolean remove(Object o) {
			return Objects.nonNull(ReferenceBoundedMap.this.removeByKey(o, PurgeReason.EXPLICIT, false));
		}

		/**
		 * 清空原 Map。
		 */
		@Override
		public void clear() {
			ReferenceBoundedMap.this.clear();
		}
	}

	/**
	 * backed value 视图。
	 */
	protected class ValueCollection extends AbstractCollection<V> {
		/**
		 * 返回弱一致 value 迭代器。
		 */
		@Override
		public Iterator<V> iterator() {
			return new ValueIterator();
		}

		/**
		 * 返回当前有效 value 数量。
		 */
		@Override
		public int size() {
			return ReferenceBoundedMap.this.size();
		}

		/**
		 * 判断当前是否没有有效 value。
		 */
		@Override
		public boolean isEmpty() {
			return ReferenceBoundedMap.this.isEmpty();
		}

		/**
		 * 判断 value 是否存在于当前映射中。
		 */
		@Override
		public boolean contains(Object o) {
			return ReferenceBoundedMap.this.containsValue(o);
		}

		/**
		 * 删除第一个匹配 value 的当前 entry。
		 */
		@Override
		public boolean remove(Object o) {
			for (Pair<K, V> pair : ReferenceBoundedMap.this.snapshotPairs()) {
				if (Objects.equals(pair.getValue(), o) && ReferenceBoundedMap.this.remove(pair.getKey(), o)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * 清空原 Map。
		 */
		@Override
		public void clear() {
			ReferenceBoundedMap.this.clear();
		}
	}
}
