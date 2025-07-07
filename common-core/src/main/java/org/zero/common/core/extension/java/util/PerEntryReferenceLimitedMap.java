package org.zero.common.core.extension.java.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程安全的引用类型 Map，支持最大容量限制和缓存过期时间
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Zero (cnzeropro@163.com)
 * @since 2025/7/3
 */
@Log
public class PerEntryReferenceLimitedMap<K, V> extends AbstractMap<K, V> implements Serializable {
    public static final int DEFAULT_MAX_CAPACITY = 1 << 16;
    public static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    public static final float DEFAULT_LOAD_FACTOR = 0.75F;
    /**
     * 最大容量
     */
    protected final int maxCapacity;
    /**
     * 是否有序（插入的顺序）
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
    protected final Executor executor;

    protected PerEntryReferenceLimitedMap(int maxCapacity, boolean order,
                                          Map<K, Pair<K, V>> coreMap,
                                          ReferenceQueue<V> referenceQueue, DelayQueue<Pair<K, V>> delayQueue,
                                          boolean lazyCleanup,
                                          Executor executor) {
        this.maxCapacity = maxCapacity;
        this.order = order;
        this.coreMap = coreMap;
        this.referenceQueue = referenceQueue;
        this.delayQueue = delayQueue;
        this.lazyCleanup = lazyCleanup;
        this.executor = executor;
        this.submitCleanupTask();
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder<>();
    }

    public static <K, V> PerEntryReferenceLimitedMap<K, V> of(int maxCapacity) {
        return PerEntryReferenceLimitedMap.<K, V>builder().maxCapacity(maxCapacity).build();
    }

    protected void submitCleanupTask() {
        if (!lazyCleanup) {
            executor.execute(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    this.cleanupInvalidEntries(true);
                }
            });
        }
    }

    public V put(K key, V value, EvictionListener<K, V> listener) {
        return this.put(key, value, ReferenceType.STRONG, null, listener);
    }

    public V put(K key, V value, ReferenceType referenceType) {
        return this.put(key, value, referenceType, null, null);
    }

    public V put(K key, V value, ReferenceType referenceType, EvictionListener<K, V> listener) {
        return this.put(key, value, referenceType, null, listener);
    }

    public V put(K key, V value, Duration ttl) {
        return this.put(key, value, ReferenceType.STRONG, ttl, null);
    }

    public V put(K key, V value, Duration ttl, EvictionListener<K, V> listener) {
        return this.put(key, value, ReferenceType.STRONG, ttl, listener);
    }

    public V put(K key, V value, ReferenceType referenceType, Duration ttl) {
        return this.put(key, value, referenceType, ttl, null);
    }

    public V put(K key, V value, ReferenceType referenceType, Duration ttl, EvictionListener<K, V> listener) {
        this.cleanupInvalidEntries(false);
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

    /**
     * 创建 {@link Pair} 对象
     */
    protected Pair<K, V> createPair(K key, V value, ReferenceType referenceType, Duration ttl, EvictionListener<K, V> listener) {
        Instant expireTime = null;
        if (Objects.nonNull(ttl)) {
            expireTime = Instant.now().plus(ttl);
        }
        switch (referenceType) {
            case STRONG:
                return new StrongPair<>(key, value, expireTime, listener);
            case SOFT:
                return new SoftPair<>(key, value, referenceQueue, expireTime, listener);
            case WEAK:
                return new WeakPair<>(key, value, referenceQueue, expireTime, listener);
            case PHANTOM:
                return new PhantomPair<>(key, value, referenceQueue, expireTime, listener);
            default:
                throw new IllegalArgumentException("Invalid reference type: " + referenceType);
        }
    }

    /**
     * 清理无效键值对
     */
    @SneakyThrows
    protected void cleanupInvalidEntries(boolean wait) {
        this.cleanupReclaimedEntries(wait);
        this.cleanupExpiredEntries(wait);
    }

    /**
     * 清理 GC 回收过的键值对
     */
    protected void cleanupReclaimedEntries(boolean wait) throws InterruptedException {
        Reference<?> reference;
        while ((reference = (wait ? referenceQueue.remove() : referenceQueue.poll())) != null) {
            synchronized (coreMap) {
                for (Iterator<Entry<K, Pair<K, V>>> iterator = coreMap.entrySet().iterator(); iterator.hasNext(); ) {
                    Entry<K, Pair<K, V>> entry = iterator.next();
                    Pair<K, V> pair = entry.getValue();
                    if (pair instanceof PerEntryReferenceLimitedMap.ReferencePair && ((ReferencePair<?, ?>) pair).reference == reference) {
                        iterator.remove();
                        this.notifyListener(pair, EvictionReason.GC_RECLAIMED);
                    }
                }
            }
        }
    }

    /**
     * 清理过期的键值对
     */
    protected void cleanupExpiredEntries(boolean wait) throws InterruptedException {
        Pair<K, V> pair;
        while ((pair = (wait ? delayQueue.take() : delayQueue.poll())) != null) {
            synchronized (coreMap) {
                coreMap.remove(pair.key);
            }
            this.notifyListener(pair, EvictionReason.EXPIRED);
        }
    }

    /**
     * 清理超出最大容量的键值对
     */
    protected void cleanupExcessEntries() {
        synchronized (coreMap) {
            // 如果超过最大容量，移除最老键值对
            while (coreMap.size() > maxCapacity) {
                Iterator<Map.Entry<K, Pair<K, V>>> iterator = coreMap.entrySet().iterator();
                if (iterator.hasNext()) {
                    Entry<K, Pair<K, V>> entry = iterator.next();
                    iterator.remove();
                    this.notifyListener(entry.getValue(), EvictionReason.CAPACITY);
                }
            }
        }
    }

    protected void notifyListener(Pair<K, V> pair, EvictionReason reason) {
        if (Objects.nonNull(pair)) {
            EvictionListener<K, V> listener = pair.listener;
            if (Objects.nonNull(listener)) {
                listener.onEviction(pair.key, pair.getValue(), reason);
            }
        }
    }

    @Override
    public V get(Object key) {
        this.cleanupInvalidEntries(false);
        Pair<K, V> pair;
        synchronized (coreMap) {
            pair = coreMap.get(key);
        }
        return Objects.nonNull(pair) ? pair.getValue() : null;
    }

    @Override
    public V put(K key, V value) {
        return this.put(key, value, ReferenceType.STRONG, null, null);
    }

    @Override
    public V remove(Object key) {
        Pair<K, V> pair;
        synchronized (coreMap) {
            pair = coreMap.remove(key);
        }
        this.notifyListener(pair, EvictionReason.EXPLICIT);
        return Objects.nonNull(pair) ? pair.getValue() : null;

    }

    @Override
    public void clear() {
        synchronized (coreMap) {
            coreMap.forEach((k, v) -> this.notifyListener(v, EvictionReason.EXPLICIT));
            coreMap.clear();
        }
    }

    @Override
    public int size() {
        this.cleanupInvalidEntries(false);
        synchronized (coreMap) {
            return coreMap.size();
        }
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        this.cleanupInvalidEntries(false);
        synchronized (coreMap) {
            return coreMap.containsKey(key);
        }
    }

    @Override
    public boolean containsValue(Object value) {
        this.cleanupInvalidEntries(false);
        synchronized (coreMap) {
            for (Pair<K, V> pair : coreMap.values()) {
                V v = pair.getValue();
                if (Objects.nonNull(v) && v.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Set<K> keySet() {
        this.cleanupInvalidEntries(false);
        synchronized (coreMap) {
            Set<K> keys = coreMap.keySet();
            return order ? new LinkedHashSet<>(keys) : new HashSet<>(keys);
        }
    }

    @Override
    public Collection<V> values() {
        this.cleanupInvalidEntries(false);
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
    public Set<Map.Entry<K, V>> entrySet() {
        this.cleanupInvalidEntries(false);
        Set<Entry<K, V>> entries = order ? new LinkedHashSet<>() : new HashSet<>();
        synchronized (coreMap) {
            for (Map.Entry<K, Pair<K, V>> entry : coreMap.entrySet()) {
                SimpleEntry<K, V> simpleEntry = new SimpleEntry<>(entry.getKey(), entry.getValue().getValue());
                entries.add(simpleEntry);
            }
        }
        return entries;
    }

    @RequiredArgsConstructor
    protected abstract static class Pair<K, V> implements Delayed {
        protected static final long SECOND_SCALE = TimeUnit.SECONDS.toNanos(1);

        protected final K key;
        protected final Instant expireTime;
        protected final ReferenceType referenceType;
        protected final EvictionListener<K, V> listener;

        protected abstract V getValue();

        @Override
        public long getDelay(TimeUnit unit) {
            if (Objects.isNull(expireTime)) {
                return Long.MAX_VALUE;
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
        public int compareTo(Delayed o) {
            if (o == this) {
                return 0;
            }
            if (o instanceof Pair) {
                Pair<?, ?> other = (Pair<?, ?>) o;
                Instant expireTime = other.expireTime;
                if (this.expireTime == expireTime) {
                    return 0;
                }
                if (Objects.isNull(this.expireTime)) {
                    return 1;
                }
                if (Objects.isNull(expireTime)) {
                    return -1;
                }
                return this.expireTime.compareTo(expireTime);
            }
            return Long.compare(this.getDelay(TimeUnit.NANOSECONDS), o.getDelay(TimeUnit.NANOSECONDS));
        }
    }

    @Getter
    protected static class StrongPair<K, V> extends Pair<K, V> {
        protected V value;

        public StrongPair(K key, V value, Instant expireTime, EvictionListener<K, V> listener) {
            super(key, expireTime, ReferenceType.STRONG, listener);
            this.value = value;
        }
    }

    protected abstract static class ReferencePair<K, V> extends Pair<K, V> {
        protected final Reference<V> reference;

        public ReferencePair(K key, V value, ReferenceType referenceType, ReferenceQueue<V> referenceQueue, Instant expireTime, EvictionListener<K, V> listener) {
            super(key, expireTime, referenceType, listener);
            this.reference = createReference(value, referenceQueue);
        }

        abstract Reference<V> createReference(V value, ReferenceQueue<V> queue);

        @Override
        public V getValue() {
            return reference.get();
        }
    }

    protected static class SoftPair<K, V> extends ReferencePair<K, V> {
        public SoftPair(K key, V value, ReferenceQueue<V> referenceQueue, Instant expireTime, EvictionListener<K, V> listener) {
            super(key, value, ReferenceType.SOFT, referenceQueue, expireTime, listener);
        }

        @Override
        Reference<V> createReference(V value, ReferenceQueue<V> queue) {
            return new SoftReference<>(value, queue);
        }
    }

    protected static class WeakPair<K, V> extends ReferencePair<K, V> {
        public WeakPair(K key, V value, ReferenceQueue<V> referenceQueue, Instant expireTime, EvictionListener<K, V> listener) {
            super(key, value, ReferenceType.WEAK, referenceQueue, expireTime, listener);
        }

        @Override
        Reference<V> createReference(V value, ReferenceQueue<V> queue) {
            return new WeakReference<>(value, queue);
        }
    }

    protected static class PhantomPair<K, V> extends ReferencePair<K, V> {
        public PhantomPair(K key, V value, ReferenceQueue<V> referenceQueue, Instant expireTime, EvictionListener<K, V> listener) {
            super(key, value, ReferenceType.PHANTOM, referenceQueue, expireTime, listener);
        }

        @Override
        Reference<V> createReference(V value, ReferenceQueue<V> queue) {
            return new PhantomReference<>(value, queue);
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
         * 是否有序（插入的顺序）。默认：true
         */
        protected boolean order = true;
        /**
         * 核心 Map
         */
        protected Map<K, Pair<K, V>> coreMap = new LinkedHashMap<>(initialCapacity, loadFactor);
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
        protected Executor executor = Executors.newSingleThreadExecutor(createThreadFactory());

        public Builder<K, V> order(boolean order) {
            this.order = order;
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

        public Builder<K, V> lazyCleanup(boolean lazyCleanup) {
            this.lazyCleanup = lazyCleanup;
            return lazyCleanup ? this.executor(null) : this;
        }

        protected Builder<K, V> initializeCoreMap() {
            this.coreMap = order ? new LinkedHashMap<>(maxCapacity, loadFactor) : new HashMap<>(maxCapacity, loadFactor);
            return this;
        }

        protected ThreadFactory createThreadFactory() {
            return new CustomThreadFactory();
        }

        public PerEntryReferenceLimitedMap<K, V> build() {
            return new PerEntryReferenceLimitedMap<>(maxCapacity, order, coreMap, referenceQueue, delayQueue, lazyCleanup, executor);
        }

        public static class CustomThreadFactory implements ThreadFactory {
            protected final ThreadGroup group;
            protected static final AtomicInteger poolNumber = new AtomicInteger(0);
            protected final AtomicInteger threadNumber = new AtomicInteger(1);

            public CustomThreadFactory() {
                this(Thread.currentThread().getThreadGroup());
            }

            public CustomThreadFactory(ThreadGroup group) {
                this.group = group;
                poolNumber.getAndIncrement();
            }

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(group, r, String.format("%s[pool-%d-thread-%d]", "Cleanup", poolNumber.get(), threadNumber.getAndIncrement()), 0);
                t.setDaemon(true);
                t.setPriority(Thread.NORM_PRIORITY);
                return t;
            }
        }
    }
}
