package org.zero.common.core.support.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/7
 */
@Getter
@RequiredArgsConstructor(staticName = "of")
public class MapCache<K, V> implements Cache<K, V> {
    protected final Map<K, V> cache;

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return cache.putIfAbsent(key, value);
    }

    @Override
    public V mapAndPut(K key, BiFunction<? super K, ? super V, ? extends V> reMapper) {
        return cache.compute(key, reMapper);
    }

    @Override
    public V mapAndPutIfAbsent(K key, Function<? super K, ? extends V> mapper) {
        return cache.computeIfAbsent(key, mapper);
    }

    @Override
    public V mapAndPutIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> reMapper) {
        return cache.computeIfPresent(key, reMapper);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> reMapper) {
        return cache.merge(key, value, reMapper);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        cache.putAll(map);
    }

    @Override
    public V get(K key) {
        return cache.get(key);
    }

    @Override
    public Map<K, V> getAll() {
        return Collections.unmodifiableMap(cache);
    }

    @Override
    public Optional<V> getOpt(K key) {
        return Optional.ofNullable(this.get(key));
    }

    @Override
    public V getOrDefault(K key, V defaultValue) {
        return cache.getOrDefault(key, defaultValue);
    }

    @Override
    public void remove(K key) {
        cache.remove(key);
    }

    @Override
    public void removeAll() {
        cache.clear();
    }

    @Override
    public boolean exists(K key) {
        return cache.containsKey(key);
    }

    @Override
    public Number size() {
        return cache.size();
    }

    public static <K, V> MapCache<K, V> of(Supplier<Map<K, V>> supplier) {
        return of(supplier.get());
    }
}
