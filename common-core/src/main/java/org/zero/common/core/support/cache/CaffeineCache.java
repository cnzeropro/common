package org.zero.common.core.support.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/8
 */
@Getter
@RequiredArgsConstructor(staticName = "of")
public class CaffeineCache<K, V> implements Cache<K, V> {
    protected final com.github.benmanes.caffeine.cache.Cache<K, V> cache;

    @Override
    public void set(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public void setAll(Map<? extends K, ? extends V> map) {
        cache.putAll(map);
    }

    @Override
    public V get(K key) {
        return cache.getIfPresent(key);
    }

    @Override
    public Map<K, V> get(Collection<K> keys) {
        return cache.getAllPresent(keys);
    }

    @Override
    public Map<K, V> getAll() {
        return Collections.unmodifiableMap(cache.asMap());
    }

    @Override
    public Optional<V> getOpt(K key) {
        return Optional.ofNullable(this.get(key));
    }

    @Override
    public void remove(K key) {
        cache.invalidate(key);
    }

    @Override
    public void remove(Collection<K> keys) {
        cache.invalidateAll(keys);
    }

    @Override
    public void removeAll() {
        cache.invalidateAll();
    }

    @Override
    public long size() {
        cache.cleanUp();
        return cache.estimatedSize();
    }

    @Override
    public Duration ttl(K key) {
        throw new UnsupportedOperationException();
    }

    public static <K, V> CaffeineCache<K, V> of(Caffeine<Object, Object> caffeine) {
        return of(caffeine.build());
    }

    public static <K, V> CaffeineCache<K, V> of(CaffeineSpec caffeineSpec) {
        return of(Caffeine.from(caffeineSpec));
    }

    public static <K, V> CaffeineCache<K, V> of(String spec) {
        return of(Caffeine.from(spec));
    }

    public static <K, V> CaffeineCache<K, V> of(int initCapacity) {
        return of(Caffeine.newBuilder().initialCapacity(initCapacity));
    }

    public static <K, V> CaffeineCache<K, V> of(long maxCapacity) {
        return of(Caffeine.newBuilder().maximumSize(maxCapacity));
    }

    public static <K, V> CaffeineCache<K, V> of(int initCapacity, long maxCapacity) {
        return of(Caffeine.newBuilder().initialCapacity(initCapacity).maximumSize(maxCapacity));
    }

    public static <K, V> CaffeineCache<K, V> of(int initCapacity, Duration timeout) {
        return of(Caffeine.newBuilder().initialCapacity(initCapacity).expireAfterAccess(timeout));
    }

    public static <K, V> CaffeineCache<K, V> of(long maxCapacity, Duration timeout) {
        return of(Caffeine.newBuilder().maximumSize(maxCapacity).expireAfterAccess(timeout));
    }

    public static <K, V> CaffeineCache<K, V> of(int initCapacity, long maxCapacity, Duration timeout) {
        return of(Caffeine.newBuilder().initialCapacity(initCapacity).maximumSize(maxCapacity).expireAfterAccess(timeout));
    }
}
