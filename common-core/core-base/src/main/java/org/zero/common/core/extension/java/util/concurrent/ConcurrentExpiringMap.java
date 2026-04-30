package org.zero.common.core.extension.java.util.concurrent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import org.zero.common.core.extension.java.lang.LoopRunnable;
import org.zero.common.core.extension.java.lang.ThreadBuilder;
import org.zero.common.core.extension.java.util.PurgeListener;
import org.zero.common.core.extension.java.util.PurgeReason;

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
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;

import static org.zero.common.core.util.java.util.MapUtil.DEFAULT_INITIAL_CAPACITY;
import static org.zero.common.core.util.java.util.MapUtil.DEFAULT_LOAD_FACTOR;

/**
 * 线程安全的 TTL Map。
 * <p>
 * 该实现以 {@link ConcurrentMap} 契约为基准：公开写路径不允许 {@code null} key/value，
 * 已过期 entry 在所有公开读写路径中都按“逻辑不存在”处理。读操作通过读锁保护，复合写操作通过
 * 一把全局写锁串行化，因此语义上是线程安全的，但并不提供 {@link java.util.concurrent.ConcurrentHashMap}
 * 那种细粒度并发性能。
 * <p>
 * 视图集合为 backed view，迭代器采用快照游标加二次身份校验的弱一致模型：不会抛出 fail-fast
 * {@link java.util.ConcurrentModificationException}，也不会暴露内部存储使用的 {@link Pair} 实例。
 * {@code entrySet().add(...)} 不受支持。
 * <p>
 * 清理监听器在同步显式操作中于状态提交后执行并保留异常外抛；后台过期清理会记录并吞掉监听器异常，
 * 避免清理线程被业务回调打断。
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/17
 */
@Log
public class ConcurrentExpiringMap<K, V> implements ConcurrentMap<K, V> {

	/**
	 * 核心存储。
	 * <p>
	 * value 使用带过期时间和监听器元数据的 {@link Pair} 包装，所有修改必须配合 {@link #writeLock}。
	 */
	protected final Map<K, Pair<K, V>> storage;
	/**
	 * 延迟队列。
	 * <p>
	 * 仅保存配置了 TTL 的 pair；同 key 被替换时旧 pair 必须同步从该队列移除。
	 */
	protected final DelayQueue<Pair<K, V>> delayQueue;
	/**
	 * 默认缓存 TTL。
	 * <p>
	 * {@code null} 表示默认不过期，单次写入方法可传入自定义 TTL 覆盖该值。
	 */
	protected final Duration ttl;
	/**
	 * 默认缓存失效监听器。
	 * <p>
	 * 单次写入方法可传入自定义 listener 覆盖该值。
	 */
	protected final PurgeListener<K, V> listener;
	/**
	 * 后台过期清理线程。
	 * <p>
	 * 惰性清理模式下为 {@code null}，过期 entry 会在公开操作中被顺手清理。
	 */
	protected final Thread cleanupThread;
	/**
	 * 保护 {@link #storage} 和 {@link #delayQueue} 一致性的读写锁。
	 */
	protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	/**
	 * 读锁，保护只读访问和快照采集。
	 */
	protected final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
	/**
	 * 写锁，保护所有结构性修改和复合更新。
	 */
	protected final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

	/**
	 * 创建 TTL Map。
	 *
	 * @param storage              底层存储
	 * @param delayQueue           过期延迟队列
	 * @param ttl                  默认 TTL，{@code null} 表示默认不过期
	 * @param listener             默认清理监听器
	 * @param cleanupThreadBuilder 后台清理线程构建器；{@code null} 表示惰性清理
	 */
	protected ConcurrentExpiringMap(Map<K, Pair<K, V>> storage, DelayQueue<Pair<K, V>> delayQueue, Duration ttl, PurgeListener<K, V> listener, ThreadBuilder cleanupThreadBuilder) {
		this.storage = storage;
		this.delayQueue = delayQueue;
		this.ttl = ttl;
		this.listener = listener;
		this.cleanupThread = this.buildCleanupThread(cleanupThreadBuilder);
	}

	/**
	 * 创建构建器。
	 */
	public static <K, V> Builder<K, V> builder() {
		return new Builder<>();
	}

	/**
	 * 获取未过期 value。
	 * <p>
	 * 如果 entry 已过期，则按过期原因删除并返回 {@code null}。
	 */
	@Override
	public V get(Object key) {
		return this.getOrDefault(key, null);
	}

	/**
	 * 获取未过期 value，不存在或已过期时返回默认值。
	 *
	 * @param key          key
	 * @param defaultValue 默认值
	 * @return 当前有效 value 或默认值
	 */
	@Override
	public V getOrDefault(Object key, V defaultValue) {
		Pair<K, V> pair = this.readPair(key);
		if (Objects.isNull(pair)) {
			return defaultValue;
		}
		if (pair.isExpired()) {
			this.removeCurrentPair(pair, PurgeReason.EXPIRY, true, false);
			return defaultValue;
		}
		return pair.getValue();
	}

	/**
	 * 使用默认 TTL 和默认监听器写入 value。
	 */
	@Override
	public V put(K key, V value) {
		return this.put(key, value, ttl, listener);
	}

	/**
	 * 使用指定 TTL 和默认监听器写入 value。
	 *
	 * @param ttl 本次写入的 TTL，{@code null} 表示不过期
	 */
	public V put(K key, V value, Duration ttl) {
		return this.put(key, value, ttl, listener);
	}

	/**
	 * 使用默认 TTL 和指定监听器写入 value。
	 *
	 * @param listener 本次写入的清理监听器
	 */
	public V put(K key, V value, PurgeListener<K, V> listener) {
		return this.put(key, value, ttl, listener);
	}

	/**
	 * 写入 value，并为本次 entry 指定 TTL 与清理监听器。
	 * <p>
	 * 如果旧 entry 已过期，返回值按 {@code absent} 处理为 {@code null}，但仍会按 {@link PurgeReason#EXPIRY}
	 * 通知旧 entry。
	 *
	 * @throws NullPointerException key 或 value 为 {@code null}
	 */
	public V put(K key, V value, Duration ttl, PurgeListener<K, V> listener) {
		this.requireNonNullKey(key);
		this.requireNonNullValue(value);
		Pair<K, V> newPair = this.createPair(key, value, ttl, listener);
		Pair<K, V> oldPair;
		writeLock.lock();
		try {
			oldPair = storage.put(key, newPair);
			this.addDelayQueueLocked(newPair);
			this.removeDelayQueueLocked(oldPair);
		} finally {
			writeLock.unlock();
		}
		this.notifyReplacement(oldPair, false);
		return Objects.nonNull(oldPair) && !oldPair.isExpired() ? oldPair.getValue() : null;
	}

	/**
	 * 创建内部存储节点。
	 *
	 * @param ttl TTL，{@code null} 表示不过期
	 */
	protected Pair<K, V> createPair(K key, V value, Duration ttl, PurgeListener<K, V> listener) {
		Instant expireTime = Objects.isNull(ttl) ? null : Instant.now().plus(ttl);
		return new Pair<>(key, value, expireTime, listener);
	}

	/**
	 * 使用默认 TTL 和默认监听器批量写入。
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		this.putAll(map, ttl, listener);
	}

	/**
	 * 使用指定 TTL 和默认监听器批量写入。
	 */
	public void putAll(Map<? extends K, ? extends V> map, Duration ttl) {
		this.putAll(map, ttl, listener);
	}

	/**
	 * 使用默认 TTL 和指定监听器批量写入。
	 */
	public void putAll(Map<? extends K, ? extends V> map, PurgeListener<K, V> listener) {
		this.putAll(map, ttl, listener);
	}

	/**
	 * 使用指定 TTL 和监听器批量写入。
	 * <p>
	 * 该方法逐项调用 {@link #put(Object, Object, Duration, PurgeListener)}，因此每个 entry 会独立触发替换通知。
	 */
	public void putAll(Map<? extends K, ? extends V> map, Duration ttl, PurgeListener<K, V> listener) {
		map.forEach((key, value) -> this.put(key, value, ttl, listener));
	}

	/**
	 * key 当前不存在或仅存在过期 entry 时写入 value。
	 */
	@Override
	public V putIfAbsent(K key, V value) {
		return this.putIfAbsent(key, value, ttl, listener);
	}

	/**
	 * key 当前不存在或仅存在过期 entry 时，使用指定 TTL 写入 value。
	 */
	public V putIfAbsent(K key, V value, Duration ttl) {
		return this.putIfAbsent(key, value, ttl, listener);
	}

	/**
	 * key 当前不存在或仅存在过期 entry 时，使用指定监听器写入 value。
	 */
	public V putIfAbsent(K key, V value, PurgeListener<K, V> listener) {
		return this.putIfAbsent(key, value, ttl, listener);
	}

	/**
	 * key 当前不存在或仅存在过期 entry 时写入 value。
	 * <p>
	 * 如果旧 entry 已过期，它会被替换并按 {@link PurgeReason#EXPIRY} 通知，返回值仍为 {@code null}。
	 *
	 * @throws NullPointerException key 或 value 为 {@code null}
	 */
	public V putIfAbsent(K key, V value, Duration ttl, PurgeListener<K, V> listener) {
		this.requireNonNullKey(key);
		this.requireNonNullValue(value);
		Pair<K, V> oldPair;
		Pair<K, V> newPair = null;
		boolean replacedExpired = false;
		writeLock.lock();
		try {
			oldPair = storage.get(key);
			if (Objects.nonNull(oldPair) && !oldPair.isExpired()) {
				return oldPair.getValue();
			}
			newPair = this.createPair(key, value, ttl, listener);
			storage.put(key, newPair);
			this.addDelayQueueLocked(newPair);
			this.removeDelayQueueLocked(oldPair);
			replacedExpired = Objects.nonNull(oldPair);
		} finally {
			writeLock.unlock();
		}
		if (replacedExpired) {
			this.notifyListener(oldPair, PurgeReason.EXPIRY, false);
		}
		return null;
	}

	/**
	 * 删除 key 对应的当前 entry。
	 * <p>
	 * 如果删除时发现 entry 已经过期，则返回 {@code null} 并按 {@link PurgeReason#EXPIRY} 通知。
	 */
	@Override
	public V remove(Object key) {
		this.requireNonNullKey(key);
		Pair<K, V> removedPair;
		writeLock.lock();
		try {
			removedPair = storage.remove(key);
			this.removeDelayQueueLocked(removedPair);
		} finally {
			writeLock.unlock();
		}
		if (Objects.isNull(removedPair)) {
			return null;
		}
		this.notifyListener(removedPair, removedPair.isExpired() ? PurgeReason.EXPIRY : PurgeReason.EXPLICIT, false);
		return removedPair.isExpired() ? null : removedPair.getValue();
	}

	/**
	 * 仅当 key 当前映射到指定 value 时删除。
	 * <p>
	 * 已过期 entry 会被清理，但方法返回 {@code false}，因为调用者视角中它已经不存在。
	 */
	@Override
	public boolean remove(Object key, Object value) {
		this.requireNonNullKey(key);
		this.requireNonNullValue(value);
		Pair<K, V> removedPair = null;
		PurgeReason reason = PurgeReason.EXPLICIT;
		writeLock.lock();
		try {
			Pair<K, V> pair = storage.get(key);
			if (Objects.isNull(pair)) {
				return false;
			}
			if (pair.isExpired()) {
				removedPair = this.removeCurrentPairLocked(pair, true);
				reason = PurgeReason.EXPIRY;
			} else if (Objects.equals(pair.getValue(), value)) {
				removedPair = this.removeCurrentPairLocked(pair, true);
			} else {
				return false;
			}
		} finally {
			writeLock.unlock();
		}
		this.notifyListener(removedPair, reason, false);
		return Objects.nonNull(removedPair) && PurgeReason.EXPLICIT.equals(reason);
	}

	/**
	 * 清空所有 entry。
	 * <p>
	 * 已经过期的 entry 按 {@link PurgeReason#EXPIRY} 通知，仍有效的 entry 按 {@link PurgeReason#EXPLICIT} 通知。
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
		pairs.forEach(pair -> this.notifyListener(pair, pair.isExpired() ? PurgeReason.EXPIRY : PurgeReason.EXPLICIT, false));
	}

	/**
	 * 替换仍有效的 entry value，并保留原 TTL 与监听器。
	 * <p>
	 * key 不存在或 entry 已过期时返回 {@code null}。
	 */
	@Override
	public V replace(K key, V value) {
		this.requireNonNullKey(key);
		this.requireNonNullValue(value);
		Pair<K, V> expiredPair = null;
		writeLock.lock();
		try {
			Pair<K, V> pair = storage.get(key);
			if (Objects.isNull(pair)) {
				return null;
			}
			if (pair.isExpired()) {
				expiredPair = this.removeCurrentPairLocked(pair, true);
				return null;
			}
			return pair.setValue(value);
		} finally {
			writeLock.unlock();
			this.notifyListener(expiredPair, PurgeReason.EXPIRY, false);
		}
	}

	/**
	 * 仅当 key 当前映射到 oldValue 时替换为 newValue。
	 * <p>
	 * 替换成功时保留原 TTL 与监听器；已过期 entry 会被清理并返回 {@code false}。
	 */
	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		this.requireNonNullKey(key);
		this.requireNonNullValue(oldValue);
		this.requireNonNullValue(newValue);
		Pair<K, V> expiredPair = null;
		writeLock.lock();
		try {
			Pair<K, V> pair = storage.get(key);
			if (Objects.isNull(pair)) {
				return false;
			}
			if (pair.isExpired()) {
				expiredPair = this.removeCurrentPairLocked(pair, true);
				return false;
			}
			if (!Objects.equals(pair.getValue(), oldValue)) {
				return false;
			}
			pair.setValue(newValue);
			return true;
		} finally {
			writeLock.unlock();
			this.notifyListener(expiredPair, PurgeReason.EXPIRY, false);
		}
	}

	/**
	 * 替换所有仍有效 entry 的 value。
	 * <p>
	 * remapping 结果不允许为 {@code null}；遍历过程中发现的过期 entry 会被清理并按过期原因通知。
	 */
	@Override
	public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		Objects.requireNonNull(function, "function");
		Collection<Pair<K, V>> expiredPairs = new ArrayList<>();
		writeLock.lock();
		try {
			for (Pair<K, V> pair : storage.values()) {
				if (pair.isExpired()) {
					expiredPairs.add(pair);
					continue;
				}
				V newValue = function.apply(pair.getKey(), pair.getValue());
				this.requireNonNullValue(newValue);
				pair.setValue(newValue);
			}
			expiredPairs.forEach(pair -> this.removeCurrentPairLocked(pair, true));
		} finally {
			writeLock.unlock();
		}
		expiredPairs.forEach(pair -> this.notifyListener(pair, PurgeReason.EXPIRY, false));
	}

	/**
	 * key 不存在或仅存在过期 entry 时，根据 mappingFunction 计算并写入新 value。
	 */
	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		return this.computeIfAbsent(key, mappingFunction, ttl, listener);
	}

	/**
	 * key 不存在或仅存在过期 entry 时，根据 mappingFunction 计算并使用指定 TTL 写入新 value。
	 */
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction, Duration ttl) {
		return this.computeIfAbsent(key, mappingFunction, ttl, listener);
	}

	/**
	 * key 不存在或仅存在过期 entry 时，根据 mappingFunction 计算并使用指定监听器写入新 value。
	 */
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction, PurgeListener<K, V> listener) {
		return this.computeIfAbsent(key, mappingFunction, ttl, listener);
	}

	/**
	 * key 不存在或仅存在过期 entry 时，根据 mappingFunction 计算并写入新 value。
	 * <p>
	 * mappingFunction 返回 {@code null} 时不建立映射；如果旧 entry 已过期，会先按过期原因清理旧 entry。
	 */
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction, Duration ttl, PurgeListener<K, V> listener) {
		this.requireNonNullKey(key);
		Objects.requireNonNull(mappingFunction, "mappingFunction");
		Pair<K, V> expiredPair = null;
		writeLock.lock();
		try {
			Pair<K, V> pair = storage.get(key);
			if (Objects.nonNull(pair) && !pair.isExpired()) {
				return pair.getValue();
			}
			V newValue = mappingFunction.apply(key);
			if (Objects.isNull(newValue)) {
				expiredPair = this.removeCurrentPairLocked(pair, true);
				return null;
			}
			Pair<K, V> newPair = this.createPair(key, newValue, ttl, listener);
			expiredPair = Objects.nonNull(pair) ? this.removeCurrentPairLocked(pair, true) : null;
			storage.put(key, newPair);
			this.addDelayQueueLocked(newPair);
			return newValue;
		} finally {
			writeLock.unlock();
			this.notifyListener(expiredPair, PurgeReason.EXPIRY, false);
		}
	}

	/**
	 * 仅当 key 当前映射到有效 entry 时执行 remapping。
	 * <p>
	 * remapping 返回 {@code null} 表示显式删除当前有效 entry。
	 */
	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		this.requireNonNullKey(key);
		Objects.requireNonNull(remappingFunction, "remappingFunction");
		Pair<K, V> removedPair = null;
		PurgeReason reason = PurgeReason.EXPLICIT;
		writeLock.lock();
		try {
			Pair<K, V> pair = storage.get(key);
			if (Objects.isNull(pair)) {
				return null;
			}
			if (pair.isExpired()) {
				removedPair = this.removeCurrentPairLocked(pair, true);
				reason = PurgeReason.EXPIRY;
				return null;
			}
			V newValue = remappingFunction.apply(key, pair.getValue());
			if (Objects.isNull(newValue)) {
				removedPair = this.removeCurrentPairLocked(pair, true);
				return null;
			}
			pair.setValue(newValue);
			return newValue;
		} finally {
			writeLock.unlock();
			this.notifyListener(removedPair, reason, false);
		}
	}

	/**
	 * 按 {@link ConcurrentMap#compute(Object, BiFunction)} 语义重新计算映射。
	 */
	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return this.compute(key, remappingFunction, ttl, listener);
	}

	/**
	 * 按 compute 语义重新计算映射，并在新建 entry 时使用指定 TTL。
	 */
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction, Duration ttl) {
		return this.compute(key, remappingFunction, ttl, listener);
	}

	/**
	 * 按 compute 语义重新计算映射，并在新建 entry 时使用指定监听器。
	 */
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction, PurgeListener<K, V> listener) {
		return this.compute(key, remappingFunction, ttl, listener);
	}

	/**
	 * 按 compute 语义重新计算映射。
	 * <p>
	 * 已过期 entry 传给 remappingFunction 的旧值为 {@code null}；如果计算结果非空，会新建 pair 并应用传入
	 * TTL/listener，而不是原地复活旧 pair。
	 */
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction, Duration ttl, PurgeListener<K, V> listener) {
		this.requireNonNullKey(key);
		Objects.requireNonNull(remappingFunction, "remappingFunction");
		Pair<K, V> removedPair = null;
		PurgeReason reason = PurgeReason.EXPLICIT;
		writeLock.lock();
		try {
			Pair<K, V> pair = storage.get(key);
			boolean live = Objects.nonNull(pair) && !pair.isExpired();
			V oldValue = live ? pair.getValue() : null;
			V newValue = remappingFunction.apply(key, oldValue);
			if (Objects.isNull(newValue)) {
				removedPair = this.removeCurrentPairLocked(pair, true);
				reason = live ? PurgeReason.EXPLICIT : PurgeReason.EXPIRY;
				return null;
			}
			if (live) {
				pair.setValue(newValue);
				return newValue;
			}
			Pair<K, V> newPair = this.createPair(key, newValue, ttl, listener);
			removedPair = this.removeCurrentPairLocked(pair, true);
			storage.put(key, newPair);
			this.addDelayQueueLocked(newPair);
			reason = PurgeReason.EXPIRY;
			return newValue;
		} finally {
			writeLock.unlock();
			this.notifyListener(removedPair, reason, false);
		}
	}

	/**
	 * 按 {@link ConcurrentMap#merge(Object, Object, BiFunction)} 语义合并映射。
	 */
	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return this.merge(key, value, remappingFunction, ttl, listener);
	}

	/**
	 * 按 merge 语义合并映射，并在新建 entry 时使用指定 TTL。
	 */
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction, Duration ttl) {
		return this.merge(key, value, remappingFunction, ttl, listener);
	}

	/**
	 * 按 merge 语义合并映射，并在新建 entry 时使用指定监听器。
	 */
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction, PurgeListener<K, V> listener) {
		return this.merge(key, value, remappingFunction, ttl, listener);
	}

	/**
	 * 按 merge 语义合并映射。
	 * <p>
	 * key 不存在或仅存在过期 entry 时直接写入 value，并使用传入 TTL/listener 创建新 pair。
	 */
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction, Duration ttl, PurgeListener<K, V> listener) {
		this.requireNonNullKey(key);
		this.requireNonNullValue(value);
		Objects.requireNonNull(remappingFunction, "remappingFunction");
		Pair<K, V> removedPair = null;
		PurgeReason reason = PurgeReason.EXPLICIT;
		writeLock.lock();
		try {
			Pair<K, V> pair = storage.get(key);
			boolean live = Objects.nonNull(pair) && !pair.isExpired();
			if (!live) {
				Pair<K, V> newPair = this.createPair(key, value, ttl, listener);
				removedPair = this.removeCurrentPairLocked(pair, true);
				storage.put(key, newPair);
				this.addDelayQueueLocked(newPair);
				reason = PurgeReason.EXPIRY;
				return value;
			}
			V newValue = remappingFunction.apply(pair.getValue(), value);
			if (Objects.isNull(newValue)) {
				removedPair = this.removeCurrentPairLocked(pair, true);
				return null;
			}
			pair.setValue(newValue);
			return newValue;
		} finally {
			writeLock.unlock();
			this.notifyListener(removedPair, reason, false);
		}
	}

	/**
	 * 返回当前有效 entry 数量。
	 * <p>
	 * 统计前会先执行一次非阻塞过期清理。
	 */
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

	/**
	 * 判断当前是否没有有效 entry。
	 */
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

	/**
	 * 判断 key 是否映射到未过期 entry。
	 */
	@Override
	public boolean containsKey(Object key) {
		Pair<K, V> pair = this.readPair(key);
		if (Objects.isNull(pair)) {
			return false;
		}
		if (pair.isExpired()) {
			this.removeCurrentPair(pair, PurgeReason.EXPIRY, true, false);
			return false;
		}
		return true;
	}

	/**
	 * 判断是否存在未过期 entry 映射到指定 value。
	 * <p>
	 * 由于该类不允许 {@code null} value，传入 {@code null} 时直接返回 {@code false}。
	 */
	@Override
	public boolean containsValue(Object value) {
		if (Objects.isNull(value)) {
			return false;
		}
		for (Pair<K, V> pair : this.snapshotPairs()) {
			if (Objects.equals(pair.getValue(), value)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 对当前有效 entry 执行遍历回调。
	 */
	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		Objects.requireNonNull(action, "action");
		for (Pair<K, V> pair : this.snapshotPairs()) {
			action.accept(pair.getKey(), pair.getValue());
		}
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
	 * <p>
	 * 视图不支持 {@code add}，迭代返回的是安全包装后的 entry。
	 */
	@Override
	public Set<Entry<K, V>> entrySet() {
		return new EntrySet();
	}

	/**
	 * 按当前有效 entry 集合比较 Map 值语义。
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Map)) {
			return false;
		}
		return this.entrySet().equals(((Map<?, ?>) obj).entrySet());
	}

	/**
	 * 按当前有效 entry 集合计算哈希。
	 */
	@Override
	public int hashCode() {
		return this.entrySet().hashCode();
	}

	/**
	 * 返回当前有效 entry 的字符串表示。
	 */
	@Override
	public String toString() {
		this.cleanupExpired(false);
		Map<K, V> snapshot = new LinkedHashMap<>();
		readLock.lock();
		try {
			storage.forEach((key, pair) -> snapshot.put(key, pair.getValue()));
		} finally {
			readLock.unlock();
		}
		return snapshot.toString();
	}

	protected Thread buildCleanupThread(ThreadBuilder threadBuilder) {
		if (Objects.nonNull(threadBuilder)) {
			return threadBuilder.task(new LoopRunnable(() -> this.cleanupExpired(true))).start().build();
		}
		return null;
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
		this.cleanupExpired(false);
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
	 * 校验快照中的 pair 仍是当前有效映射。
	 * <p>
	 * 延迟队列和迭代器都可能持有旧 pair；这里只按对象身份判断，避免旧代 entry 误删或误返回新映射。
	 */
	protected boolean isCurrentLivePair(Pair<K, V> pair) {
		readLock.lock();
		try {
			Pair<K, V> current = storage.get(pair.getKey());
			return current == pair && !pair.isExpired();
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 将带过期时间的 pair 加入延迟队列。
	 */
	protected void addDelayQueueLocked(Pair<K, V> pair) {
		if (Objects.nonNull(pair) && Objects.nonNull(pair.getExpireTime())) {
			delayQueue.add(pair);
		}
	}

	/**
	 * 从延迟队列移除带过期时间的 pair。
	 */
	protected void removeDelayQueueLocked(Pair<K, V> pair) {
		if (Objects.nonNull(pair) && Objects.nonNull(pair.getExpireTime())) {
			delayQueue.remove(pair);
		}
	}

	/**
	 * 按 pair 身份删除当前映射。
	 * <p>
	 * 所有异步过期、迭代器清理和替换路径都应通过该方法收口，禁止只按 key 删除，
	 * 否则旧 pair 可能在延迟队列稍后出队时删掉同 key 的新值。
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
			this.removeDelayQueueLocked(pair);
		}
		return pair;
	}

	/**
	 * 按 pair 身份删除当前映射并通知监听器。
	 */
	protected Pair<K, V> removeCurrentPair(Pair<K, V> pair, PurgeReason reason, boolean removeDelayQueue, boolean swallowListenerException) {
		Pair<K, V> removedPair;
		writeLock.lock();
		try {
			removedPair = this.removeCurrentPairLocked(pair, removeDelayQueue);
		} finally {
			writeLock.unlock();
		}
		this.notifyListener(removedPair, reason, swallowListenerException);
		return removedPair;
	}

	/**
	 * 通知被替换的旧 entry。
	 * <p>
	 * 如果旧 entry 在被替换时已经过期，则它从调用者视角属于过期清理而非显式删除。
	 */
	protected void notifyReplacement(Pair<K, V> oldPair, boolean swallowListenerException) {
		if (Objects.isNull(oldPair)) {
			return;
		}
		this.notifyListener(oldPair, oldPair.isExpired() ? PurgeReason.EXPIRY : PurgeReason.EXPLICIT, swallowListenerException);
	}

	/**
	 * 清理过期 entry。
	 *
	 * @param wait 是否阻塞等待下一个过期 entry；后台清理线程传 {@code true}，惰性清理传 {@code false}
	 */
	protected void cleanupExpired(boolean wait) {
		while (true) {
			Pair<K, V> pair;
			try {
				pair = wait ? delayQueue.take() : delayQueue.poll();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
			if (Objects.isNull(pair)) {
				return;
			}
			this.removeCurrentPair(pair, PurgeReason.EXPIRY, false, wait);
		}
	}

	/**
	 * 通知清理监听器。
	 *
	 * @param swallowListenerException 是否吞掉监听器异常；后台异步清理为 {@code true}
	 */
	protected void notifyListener(Pair<K, V> pair, PurgeReason reason, boolean swallowListenerException) {
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
			if (!swallowListenerException) {
				throw e;
			}
			log.log(Level.WARNING, "Purge listener threw exception during async cleanup.", e);
		}
	}

	/**
	 * 校验 key 非空。
	 */
	protected void requireNonNullKey(Object key) {
		Objects.requireNonNull(key, "key");
	}

	/**
	 * 校验 value 非空。
	 */
	protected void requireNonNullValue(Object value) {
		Objects.requireNonNull(value, "value");
	}

	/**
	 * 销毁当前 Map 的后台清理资源并清空数据。
	 */
	protected void destroy() throws Exception {
		storage.clear();
		delayQueue.clear();
		if (Objects.nonNull(cleanupThread)) {
			cleanupThread.interrupt();
		}
	}

	/**
	 * 作为兜底保护释放后台清理线程。
	 */
	@Override
	protected void finalize() throws Throwable {
		this.destroy();
	}

	/**
	 * 内部存储节点。
	 * <p>
	 * {@link #equals(Object)} 和 {@link #hashCode()} 使用对象身份，确保延迟队列中的旧节点不会与同 key/value
	 * 的新节点混淆。该类型不会直接暴露给 {@code entrySet()} 调用者。
	 */
	@Getter
	protected static class Pair<K, V> extends AbstractMap.SimpleEntry<K, V> implements InstantDelayed {
		protected final Instant expireTime;
		protected final PurgeListener<K, V> listener;

		/**
		 * 创建内部存储节点。
		 */
		public Pair(K key, V value, Instant expireTime, PurgeListener<K, V> listener) {
			super(key, value);
			this.expireTime = expireTime;
			this.listener = listener;
		}

		/**
		 * 使用对象身份比较，避免不同代 pair 因 key/value 相同而被误判为同一节点。
		 */
		@Override
		public boolean equals(Object obj) {
			return this == obj;
		}

		/**
		 * 使用对象身份哈希。
		 */
		@Override
		public int hashCode() {
			return System.identityHashCode(this);
		}
	}

	/**
	 * {@link ConcurrentExpiringMap} 构建器。
	 */
	@Setter
	@Accessors(chain = true, fluent = true)
	@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
	public static class Builder<K, V> implements org.zero.common.core.extension.java.lang.Builder<ConcurrentExpiringMap<K, V>, Builder<K, V>> {
		/**
		 * 核心存储。
		 * <p>
		 * 默认使用 {@link HashMap}，如需稳定遍历顺序可传入 {@link LinkedHashMap}。
		 */
		protected Map<K, Pair<K, V>> storage = new HashMap<>(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
		/**
		 * 延迟队列。
		 */
		protected DelayQueue<Pair<K, V>> delayQueue = new DelayQueue<>();
		/**
		 * 默认缓存 TTL。
		 */
		protected Duration ttl;
		/**
		 * 默认缓存失效监听器。
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

		/**
		 * 开启惰性清理。
		 */
		public Builder<K, V> lazyCleanup() {
			return this.lazyCleanup(true);
		}

		/**
		 * 将清理线程设置为守护线程。
		 */
		public Builder<K, V> cleanupThreadDaemon() {
			return this.cleanupThreadDaemon(true);
		}

		/**
		 * 根据当前配置创建清理线程构建器。
		 * <p>
		 * 惰性清理模式下返回 {@code null}。
		 */
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

		/**
		 * 构建 {@link ConcurrentExpiringMap}。
		 */
		@Override
		public ConcurrentExpiringMap<K, V> build() {
			ThreadBuilder cleanupThreadBuilder = this.createCleanupThreadBuilder();
			return new ConcurrentExpiringMap<>(storage, delayQueue, ttl, listener, cleanupThreadBuilder);
		}
	}

	/**
	 * 基于快照的弱一致迭代器。
	 * <p>
	 * 快照只保存遍历候选，真正返回前仍会确认 pair 是当前有效映射；{@link #remove()} 回写原 Map，
	 * 且只允许删除最近一次 {@link #next()} 返回的元素。
	 */
	protected abstract class SnapshotIterator<E> implements Iterator<E> {
		protected final ArrayList<Pair<K, V>> snapshot = ConcurrentExpiringMap.this.snapshotPairs();
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
				if (ConcurrentExpiringMap.this.isCurrentLivePair(pair)) {
					nextPair = pair;
					nextReady = true;
					return true;
				}
				if (pair.isExpired()) {
					ConcurrentExpiringMap.this.removeCurrentPair(pair, PurgeReason.EXPIRY, true, false);
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
			ConcurrentExpiringMap.this.removeCurrentPair(currentPair, PurgeReason.EXPLICIT, true, false);
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
	 * 该对象只保存 key 与期望 pair 身份，{@link #setValue(Object)} 会重新回到 Map 内部加锁修改，
	 * 避免调用者直接修改内部 {@link Pair}。
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
		 * 返回当前有效 value；entry 已失效时返回 {@code null}。
		 */
		@Override
		public V getValue() {
			return ConcurrentExpiringMap.this.get(key);
		}

		/**
		 * 回写当前 entry value。
		 * <p>
		 * 若 entry 已被替换或已过期，则抛出 {@link IllegalStateException}。
		 */
		@Override
		public V setValue(V value) {
			ConcurrentExpiringMap.this.requireNonNullValue(value);
			Pair<K, V> expiredPair = null;
			writeLock.lock();
			try {
				Pair<K, V> current = storage.get(key);
				if (current != expectedPair) {
					throw new IllegalStateException("Entry is no longer valid.");
				}
				if (current.isExpired()) {
					expiredPair = ConcurrentExpiringMap.this.removeCurrentPairLocked(current, true);
					throw new IllegalStateException("Entry is no longer valid.");
				}
				return current.setValue(value);
			} finally {
				writeLock.unlock();
				ConcurrentExpiringMap.this.notifyListener(expiredPair, PurgeReason.EXPIRY, false);
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
			return ConcurrentExpiringMap.this.size();
		}

		/**
		 * 判断当前是否没有有效 entry。
		 */
		@Override
		public boolean isEmpty() {
			return ConcurrentExpiringMap.this.isEmpty();
		}

		/**
		 * 清空原 Map。
		 */
		@Override
		public void clear() {
			ConcurrentExpiringMap.this.clear();
		}

		/**
		 * entry 视图不支持新增。
		 */
		@Override
		public boolean add(Entry<K, V> entry) {
			throw new UnsupportedOperationException("entrySet view does not support add.");
		}

		/**
		 * 判断 entry 是否存在于当前有效映射中。
		 */
		@Override
		public boolean contains(Object o) {
			if (!(o instanceof Entry)) {
				return false;
			}
			Entry<?, ?> entry = (Entry<?, ?>) o;
			Object value = ConcurrentExpiringMap.this.get(entry.getKey());
			return Objects.nonNull(value) && Objects.equals(value, entry.getValue());
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
			if (Objects.isNull(entry.getValue())) {
				return false;
			}
			return ConcurrentExpiringMap.this.remove(entry.getKey(), entry.getValue());
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
			return ConcurrentExpiringMap.this.size();
		}

		/**
		 * 判断当前是否没有有效 key。
		 */
		@Override
		public boolean isEmpty() {
			return ConcurrentExpiringMap.this.isEmpty();
		}

		/**
		 * 判断 key 是否存在于当前有效映射中。
		 */
		@Override
		public boolean contains(Object o) {
			return ConcurrentExpiringMap.this.containsKey(o);
		}

		/**
		 * 从原 Map 删除 key。
		 */
		@Override
		public boolean remove(Object o) {
			return Objects.nonNull(ConcurrentExpiringMap.this.remove(o));
		}

		/**
		 * 清空原 Map。
		 */
		@Override
		public void clear() {
			ConcurrentExpiringMap.this.clear();
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
			return ConcurrentExpiringMap.this.size();
		}

		/**
		 * 判断当前是否没有有效 value。
		 */
		@Override
		public boolean isEmpty() {
			return ConcurrentExpiringMap.this.isEmpty();
		}

		/**
		 * 判断 value 是否存在于当前有效映射中。
		 */
		@Override
		public boolean contains(Object o) {
			return ConcurrentExpiringMap.this.containsValue(o);
		}

		/**
		 * 删除第一个匹配 value 的当前有效 entry。
		 */
		@Override
		public boolean remove(Object o) {
			if (Objects.isNull(o)) {
				return false;
			}
			for (Pair<K, V> pair : ConcurrentExpiringMap.this.snapshotPairs()) {
				if (Objects.equals(pair.getValue(), o) && ConcurrentExpiringMap.this.remove(pair.getKey(), o)) {
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
			ConcurrentExpiringMap.this.clear();
		}
	}
}
