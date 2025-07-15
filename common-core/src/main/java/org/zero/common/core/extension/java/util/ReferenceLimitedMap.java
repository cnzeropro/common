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

import java.io.Serializable;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 线程安全的引用类型 Map，支持最大容量限制和缓存过期时间
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/3
 */
public class ReferenceLimitedMap<K, V> extends AbstractMap<K, V> implements Serializable {
    public static final int DEFAULT_MAX_CAPACITY = 1 << 16;
    public static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    public static final float DEFAULT_LOAD_FACTOR = 0.75F;
    public static final int DEFAULT_CLEANUP_THREAD_POOL_SIZE = 2;
    public static final ThreadFactory DEFAULT_CLEANUP_THREAD_FACTORY = DefaultThreadFactory.builder().jobName("Cleanup").daemon(true).build();
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
     * 核心 Map
     */
    protected final Map<K, Pair<K, V>> coreMap;
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
     * <b>注意：如果不允许惰性清理（即实时清理）将影响系统性能</b>
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

    protected ReferenceLimitedMap(int maxCapacity, boolean order,
                                  Map<K, Pair<K, V>> coreMap,
                                  ReferenceQueue<V> referenceQueue, DelayQueue<Pair<K, V>> delayQueue,
                                  boolean lazyCleanup, Executor cleanupExecutor,
                                  ReferenceType referenceType, Duration ttl, PurgeListener<K, V> listener) {
        this.maxCapacity = maxCapacity;
        this.order = order;
        this.coreMap = coreMap;
        this.referenceQueue = referenceQueue;
        this.delayQueue = delayQueue;
        this.lazyCleanup = lazyCleanup;
        this.cleanupExecutor = cleanupExecutor;
        this.referenceType = referenceType;
        this.ttl = ttl;
        this.listener = listener;
        this.submitCleanupTask();
    }

    public static <K, V> ReferenceLimitedMap<K, V> of() {
        return ReferenceLimitedMap.<K, V>builder().build();
    }

    public static <K, V> ReferenceLimitedMap<K, V> of(int maxCapacity) {
        return ReferenceLimitedMap.<K, V>builder().maxCapacity(maxCapacity).build();
    }

    public static <K, V> ReferenceLimitedMap<K, V> of(ReferenceType referenceType) {
        return ReferenceLimitedMap.<K, V>builder()
                .maxCapacity(Integer.MAX_VALUE)
                .referenceType(referenceType).build();
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder<>();
    }

    protected void submitCleanupTask() {
        if (!lazyCleanup) {
            cleanupExecutor.execute(new LoopRunnable(() -> this.cleanupReclaimedEntries(true)));
            cleanupExecutor.execute(new LoopRunnable(() -> this.cleanupExpiredEntries(true)));
        }
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
        Pair<K, V> pair = this.createPair(key, value, referenceType, ttl, listener);
        Pair<K, V> oldPair;
        synchronized (coreMap) {
            oldPair = coreMap.put(key, pair);
            // 如果指定了生命周期，则添加到延迟队列
            if (Objects.nonNull(ttl)) {
                delayQueue.put(pair);
            }
        }
        this.cleanupExcessEntries();
        return Objects.nonNull(oldPair) ? oldPair.getValue() : null;
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

    /**
     * 创建 {@link Pair} 对象
     */
    protected Pair<K, V> createPair(K key, V value, ReferenceType referenceType, Duration ttl, PurgeListener<K, V> listener) {
        Instant expireTime = null;
        if (Objects.nonNull(ttl)) {
            expireTime = Instant.now().plus(ttl);
        }
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
                throw new IllegalArgumentException("Invalid reference type: " + referenceType);
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
                K key = pair.getKey();
                synchronized (coreMap) {
                    coreMap.remove(key);
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
            K key = pair.getKey();
            synchronized (coreMap) {
                coreMap.remove(key);
            }
            this.notifyListener(pair, PurgeReason.EXPIRY);
        }
    }

    /**
     * 清理超出最大容量的键值对
     */
    protected void cleanupExcessEntries() {
        synchronized (coreMap) {
            // 如果超过最大容量，移除第一个键值对，键值对的迭代器顺序和 coreMap 类型有关
            while (coreMap.size() > maxCapacity) {
                Iterator<Map.Entry<K, Pair<K, V>>> iterator = coreMap.entrySet().iterator();
                if (iterator.hasNext()) {
                    Entry<K, Pair<K, V>> entry = iterator.next();
                    iterator.remove();
                    this.notifyListener(entry.getValue(), PurgeReason.EVICTION);
                }
            }
        }
    }

    protected void notifyListener(Pair<K, V> pair, PurgeReason reason) {
        if (Objects.nonNull(pair)) {
            PurgeListener<K, V> listener = pair.getListener();
            if (Objects.nonNull(listener)) {
                listener.onPurge(pair.getKey(), pair.getValue(), reason);
            }
        }
    }

    @Override
    public V get(Object key) {
        this.cleanupInvalidEntries();
        Pair<K, V> pair;
        synchronized (coreMap) {
            pair = coreMap.get(key);
        }
        return Objects.nonNull(pair) ? pair.getValue() : null;
    }

    @Override
    public V put(K key, V value) {
        return this.put(key, value, referenceType, ttl, listener);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        this.putAll(m, referenceType, ttl, listener);
    }

    @Override
    public V remove(Object key) {
        Pair<K, V> pair;
        synchronized (coreMap) {
            pair = coreMap.remove(key);
        }
        this.notifyListener(pair, PurgeReason.EXPLICIT);
        return Objects.nonNull(pair) ? pair.getValue() : null;

    }

    @Override
    public void clear() {
        synchronized (coreMap) {
            Collection<Pair<K, V>> values = coreMap.values();
            coreMap.clear();
            values.forEach(pair -> this.notifyListener(pair, PurgeReason.EXPLICIT));
        }
    }

    @Override
    public int size() {
        this.cleanupInvalidEntries();
        synchronized (coreMap) {
            return coreMap.size();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        this.cleanupInvalidEntries();
        synchronized (coreMap) {
            return coreMap.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        this.cleanupInvalidEntries();
        synchronized (coreMap) {
            for (Pair<K, V> pair : coreMap.values()) {
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
        synchronized (coreMap) {
            keys = coreMap.keySet();
        }
        return order ? new LinkedHashSet<>(keys) : new HashSet<>(keys);
    }

    @Override
    public Collection<V> values() {
        this.cleanupInvalidEntries();
        Collection<V> values = new ArrayList<>();
        synchronized (coreMap) {
            for (Pair<K, V> pair : coreMap.values()) {
                V value = pair.getValue();
                values.add(value);
            }
        }
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        this.cleanupInvalidEntries();
        Set<Entry<K, V>> entries = order ? new LinkedHashSet<>() : new HashSet<>();
        synchronized (coreMap) {
            for (Entry<K, Pair<K, V>> entry : coreMap.entrySet()) {
                Pair<K, V> pair = entry.getValue();
                SimpleEntry<K, V> simpleEntry = new SimpleEntry<>(entry.getKey(), pair.getValue());
                entries.add(simpleEntry);
            }
        }
        return entries;
    }

    protected interface Pair<K, V> extends Delayed {
        long SECOND_SCALE = TimeUnit.SECONDS.toNanos(1);
        long PERPETUITY = Long.MAX_VALUE;

        K getKey();

        V getValue();

        Instant getExpireTime();

        PurgeListener<K, V> getListener();

        @Override
        default long getDelay(TimeUnit unit) {
            Instant expireTime = this.getExpireTime();
            if (Objects.isNull(expireTime)) {
                return PERPETUITY;
            }
            Duration duration = Duration.between(Instant.now(), expireTime);
            // copy from java 11: java.util.concurrent.TimeUnit.convert(java.time.Duration)
            long scale = unit.toNanos(1);
            long secRatio = (scale >= SECOND_SCALE) ? (scale / SECOND_SCALE) : (SECOND_SCALE / scale);
            long maxSecs = Long.MAX_VALUE / secRatio;
            long secs = duration.getSeconds();
            int nano = duration.getNano();
            if (secs < 0 && nano > 0) {
                // use representation compatible with integer division
                secs++;
                nano -= (int) SECOND_SCALE;
            }
            final long s, nanoVal;
            // Optimize for the common case - NANOSECONDS without overflow
            if (unit == TimeUnit.NANOSECONDS) {
                nanoVal = nano;
            } else if ((s = scale) < SECOND_SCALE) {
                nanoVal = nano / s;
            } else if (unit == TimeUnit.SECONDS) {
                return secs;
            } else {
                return secs / secRatio;
            }
            long val = secs * secRatio + nanoVal;
            return ((secs < maxSecs && secs > -maxSecs) ||
                    (secs == maxSecs && val > 0) ||
                    (secs == -maxSecs && val < 0))
                    ? val
                    : (secs > 0) ? Long.MAX_VALUE : Long.MIN_VALUE;
        }

        @Override
        default int compareTo(Delayed o) {
            if (o == this) {
                return 0;
            }
            if (o instanceof Pair) {
                return Comparator.nullsFirst(Instant::compareTo)
                        .compare(this.getExpireTime(), ((Pair<?, ?>) o).getExpireTime());
            }
            return Long.compare(this.getDelay(TimeUnit.NANOSECONDS), o.getDelay(TimeUnit.NANOSECONDS));
        }
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    @EqualsAndHashCode
    protected static class StrongPair<K, V> implements Pair<K, V> {
        protected final K key;
        protected final V value;
        @EqualsAndHashCode.Exclude
        protected final Instant expireTime;
        @EqualsAndHashCode.Exclude
        protected final PurgeListener<K, V> listener;
    }

    @Getter
    @EqualsAndHashCode(callSuper = false)
    protected static class SoftPair<K, V> extends SoftReference<V> implements Pair<K, V> {
        protected final K key;
        @EqualsAndHashCode.Exclude
        protected final Instant expireTime;
        @EqualsAndHashCode.Exclude
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
    @EqualsAndHashCode(callSuper = false)
    protected static class WeakPair<K, V> extends WeakReference<V> implements Pair<K, V> {
        protected final K key;
        @EqualsAndHashCode.Exclude
        protected final Instant expireTime;
        @EqualsAndHashCode.Exclude
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
        protected final K key;
        @EqualsAndHashCode.Exclude
        protected final Instant expireTime;
        @EqualsAndHashCode.Exclude
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
            return null;
        }
    }

    public static class Builder<K, V> {
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
         * 顺序模式。true：插入顺序；false：访问顺序（LRU 缓存）。
         * 仅当 {@code order = true} 时有效。默认：false
         * <p>
         * <b>注意：{@code accessOrder = true} 会增加每次访问操作的开销</b>
         */
        protected boolean accessOrder = false;
        /**
         * 核心 Map
         */
        protected Map<K, Pair<K, V>> coreMap = new LinkedHashMap<>(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, false);
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
         * 是否进行惰性清理。默认：true
         * <p>
         * <b>注意：如果不允许惰性清理（即实时清理）将影响系统性能</b>
         */
        protected boolean lazyCleanup = true;
        /**
         * 清理任务线程池
         */
        @Setter
        @Accessors(fluent = true, chain = true)
        protected Executor cleanupExecutor;

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
            return this.initializeCoreMap();
        }

        public Builder<K, V> accessOrder() {
            return this.accessOrder(true);
        }

        public Builder<K, V> accessOrder(boolean accessOrder) {
            this.order = true;
            this.accessOrder = accessOrder;
            return this.initializeCoreMap();
        }

        public Builder<K, V> initialCapacity(int initialCapacity) {
            this.initialCapacity = initialCapacity;
            return this.initializeCoreMap();
        }

        public Builder<K, V> loadFactor(float loadFactor) {
            this.loadFactor = loadFactor;
            return this.initializeCoreMap();
        }

        public Builder<K, V> lazyCleanup() {
            return this.lazyCleanup(true);
        }

        public Builder<K, V> lazyCleanup(boolean lazyCleanup) {
            this.lazyCleanup = lazyCleanup;
            return lazyCleanup ? this.cleanupExecutor(null) : this.cleanupExecutor(DEFAULT_CLEANUP_EXECUTOR);
        }

        protected Builder<K, V> initializeCoreMap() {
            this.coreMap = order ? new LinkedHashMap<>(maxCapacity, loadFactor, accessOrder) : new HashMap<>(maxCapacity, loadFactor);
            return this;
        }

        public ReferenceLimitedMap<K, V> build() {
            return new ReferenceLimitedMap<>(maxCapacity, order, coreMap, referenceQueue, delayQueue, lazyCleanup, cleanupExecutor, referenceType, ttl, listener);
        }
    }
}
