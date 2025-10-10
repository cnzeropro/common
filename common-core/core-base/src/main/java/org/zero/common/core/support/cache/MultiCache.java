package org.zero.common.core.support.cache;

import org.zero.common.core.util.java.util.MapUtil;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/21
 */
public class MultiCache<K, V> implements Cache<K, V> {
    protected final List<Cache<K, V>> caches = new CopyOnWriteArrayList<>();

    protected MultiCache(Cache<K, V>... caches) {
        this.addCache(caches);
    }

    protected MultiCache(Collection<Cache<K, V>> caches) {
        this.addCache(caches);
    }

    @Override
    public void put(K key, V value) {
        caches.forEach(cache -> cache.put(key, value));
    }

    @Override
    public V putIfAbsent(K key, V value) {
        V result = null;
        for (Cache<K, V> cache : caches) {
            result = cache.putIfAbsent(key, value);
        }
        return result;
    }

    @Override
    public V mapAndPutIfAbsent(K key, Function<? super K, ? extends V> mapper) {
        V result = null;
        for (Cache<K, V> cache : caches) {
            result = cache.mapAndPutIfAbsent(key, mapper);
        }
        return result;
    }

    @Override
    public V mapAndPutIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> reMapper) {
        V result = null;
        for (Cache<K, V> cache : caches) {
            result = cache.mapAndPutIfPresent(key, reMapper);
        }
        return result;
    }

    @Override
    public V mapAndPut(K key, BiFunction<? super K, ? super V, ? extends V> reMapper) {
        V result = null;
        for (Cache<K, V> cache : caches) {
            result = cache.mapAndPut(key, reMapper);
        }
        return result;
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> reMapper) {
        V result = null;
        for (Cache<K, V> cache : caches) {
            result = cache.merge(key, value, reMapper);
        }
        return result;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        caches.forEach(cache -> cache.putAll(map));
    }

    @Override
    public void putAll(Cache<? extends K, ? extends V> cache) {
        caches.forEach(c -> c.putAll(cache));
    }

    @Override
    public V get(K key) {
        for (Cache<K, V> cache : caches) {
            V value = cache.get(key);
            if (Objects.nonNull(value)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public Map<K, V> get(K... keys) {
        for (Cache<K, V> cache : caches) {
            Map<K, V> result = cache.get(keys);
            if (MapUtil.nonEmpty(result)) {
                return result;
            }
        }
        return Collections.emptyMap();
    }

    @Override
    public Map<K, V> get(Collection<K> keys) {
        for (Cache<K, V> cache : caches) {
            Map<K, V> result = cache.get(keys);
            if (MapUtil.nonEmpty(result)) {
                return result;
            }
        }
        return Collections.emptyMap();
    }

    @Override
    public Map<K, V> getAll() {
        for (Cache<K, V> cache : caches) {
            Map<K, V> result = cache.getAll();
            if (MapUtil.nonEmpty(result)) {
                return result;
            }
        }
        return Collections.emptyMap();
    }

    @Override
    public Optional<V> getOpt(K key) {
        for (Cache<K, V> cache : caches) {
            Optional<V> result = cache.getOpt(key);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean exists(K key) {
        for (Cache<K, V> cache : caches) {
            if (cache.exists(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void remove(K key) {
        for (Cache<K, V> cache : caches) {
            cache.remove(key);
        }
    }

    @Override
    public void remove(K... keys) {
        for (Cache<K, V> cache : caches) {
            cache.remove(keys);
        }
    }

    @Override
    public void remove(Collection<K> keys) {
        for (Cache<K, V> cache : caches) {
            cache.remove(keys);
        }
    }

    @Override
    public void removeAll() {
        for (Cache<K, V> cache : caches) {
            cache.removeAll();
        }
    }

    @Override
    public Number size() {
        for (Cache<K, V> cache : caches) {
            Number size = cache.size();
            if (Objects.nonNull(size) && size.intValue() > 0) {
                return size;
            }
        }
        return BigInteger.ZERO;
    }

    public static <K, V> MultiCache<K, V> of(Cache<K, V>... caches) {
        return new MultiCache<>(caches);
    }

    public static <K, V> MultiCache<K, V> of(Collection<Cache<K, V>> caches) {
        return new MultiCache<>(caches);
    }

    public void addCache(Cache<K, V> cache) {
        caches.add(cache);
    }

    public void addCache(int index, Cache<K, V> cache) {
        caches.add(index, cache);
    }

    public void addCache(Cache<K, V>... caches) {
        this.addCache(Arrays.asList(caches));
    }

    public void addCache(Collection<Cache<K, V>> caches) {
        this.caches.addAll(caches);
    }

    public void addCache(int index, Cache<K, V>... caches) {
        this.addCache(index, Arrays.asList(caches));
    }

    public void addCache(int index, Collection<Cache<K, V>> caches) {
        this.caches.addAll(index, caches);
    }

    public void removeCache(Cache<K, V> cache) {
        caches.remove(cache);
    }

    public void removeCache(Cache<K, V>... caches) {
        this.removeCache(Arrays.asList(caches));
    }

    public void removeCache(Collection<Cache<K, V>> caches) {
        this.caches.removeAll(caches);
    }

    public void removeCache(int index) {
        caches.remove(index);
    }
}
