package org.zero.common.core.support.cache;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/7
 */
public interface Cache<K, V> {
    void set(K key, V value);

    default void set(K key, V value, long timeout) {
        set(key, value, timeout, TimeUnit.MILLISECONDS);
    }

    default void set(K key, V value, Duration timeout) {
        throw new UnsupportedOperationException("Expiration-set requires explicit implementation. Check cache type: " +
                this.getClass().getName());
    }

    default void set(K key, V value, long timeout, TimeUnit unit) {
        set(key, value, Duration.ofNanos(unit.toNanos(timeout)));
    }

    default V setIfAbsent(K key, V value) {
        V v = this.get(key);
        if (v == null) {
            this.set(key, value);
            v = value;
        }
        return v;
    }

    default V setIfAbsent(K key, V value, Duration timeout) {
        V v = this.get(key);
        if (v == null) {
            this.set(key, value, timeout);
            v = value;
        }
        return v;
    }

    default V mapAndSetIfAbsent(K key, Function<? super K, ? extends V> mapper) {
        V v;
        if ((v = this.get(key)) == null) {
            V newValue;
            if ((newValue = mapper.apply(key)) != null) {
                this.set(key, newValue);
                return newValue;
            }
        }
        return v;
    }

    default V mapAndSetIfAbsent(K key, Function<? super K, ? extends V> mapper, Duration timeout) {
        V v;
        if ((v = this.get(key)) == null) {
            V newValue;
            if ((newValue = mapper.apply(key)) != null) {
                this.set(key, newValue, timeout);
                return newValue;
            }
        }
        return v;
    }

    default V mapAndSetIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> reMapper) {
        V oldValue;
        if ((oldValue = this.get(key)) != null) {
            V newValue = reMapper.apply(key, oldValue);
            if (newValue != null) {
                this.set(key, newValue);
                return newValue;
            }
            remove(key);
            return null;
        }
        return null;
    }

    default V mapAndSetIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> reMapper, Duration timeout) {
        V oldValue;
        if ((oldValue = this.get(key)) != null) {
            V newValue = reMapper.apply(key, oldValue);
            if (newValue != null) {
                this.set(key, newValue, timeout);
                return newValue;
            }
            remove(key);
            return null;
        }
        return null;
    }

    default V mapAndSet(K key, BiFunction<? super K, ? super V, ? extends V> reMapper) {
        V oldValue = this.get(key);
        V newValue = reMapper.apply(key, oldValue);
        if (newValue == null) {
            if (oldValue != null || exists(key)) {
                remove(key);
            }
            return null;
        }
        this.set(key, newValue);
        return newValue;
    }

    default V mapAndSet(K key, BiFunction<? super K, ? super V, ? extends V> reMapper, Duration timeout) {
        V oldValue = this.get(key);
        V newValue = reMapper.apply(key, oldValue);
        if (newValue == null) {
            if (oldValue != null || exists(key)) {
                remove(key);
            }
            return null;
        }
        this.set(key, newValue, timeout);
        return newValue;
    }

    default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> reMapper) {
        V oldValue = this.get(key);
        V newValue = (oldValue == null) ? value : reMapper.apply(oldValue, value);
        if (newValue == null) {
            this.remove(key);
        } else {
            this.set(key, newValue);
        }
        return newValue;
    }

    default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> reMapper, Duration timeout) {
        V oldValue = this.get(key);
        V newValue = (oldValue == null) ? value : reMapper.apply(oldValue, value);
        if (newValue == null) {
            this.remove(key);
        } else {
            this.set(key, newValue, timeout);
        }
        return newValue;
    }

    default void setAll(Map<? extends K, ? extends V> map) {
        map.forEach(this::set);
    }

    default void setAll(Map<? extends K, ? extends V> map, Duration timeout) {
        map.forEach((k, v) -> this.set(k, v, timeout));
    }

    default void setAll(Cache<? extends K, ? extends V> cache) {
        this.setAll(cache.getAll());
    }

    default void setAll(Cache<? extends K, ? extends V> cache, Duration timeout) {
        this.setAll(cache.getAll(), timeout);
    }


    default V get(K key) {
        return this.getOpt(key).orElse(null);
    }

    default Map<K, V> get(K... keys) {
        return this.get(Arrays.asList(keys));
    }

    default Map<K, V> get(Collection<K> keys) {
        Map<K, V> map = new LinkedHashMap<>();
        for (K key : keys) {
            map.put(key, this.get(key));
        }
        return map;
    }

    Map<K, V> getAll();

    Optional<V> getOpt(K key);

    default V getOrDefault(K key, V defaultValue) {
        return this.getOpt(key).orElse(defaultValue);
    }

    default <R> R getAndConvert(K key, Function<? super V, R> mapper) {
        return this.getOpt(key).map(mapper).orElse(null);
    }

    default boolean exists(K key) {
        return this.getOpt(key).isPresent();
    }

    void remove(K key);

    default void remove(K... keys) {
        this.remove(Arrays.asList(keys));
    }

    default void remove(Collection<K> keys) {
        for (K key : keys) {
            this.remove(key);
        }
    }

    default void removeAll() {
        this.getAll().forEach((k, v) -> this.remove(k));
    }

    default long size() {
        return this.getAll().size();
    }

    /**
     * 获取过期时间
     *
     * @param key 缓存键
     * @return 过期时间，如果永不过期则返回 null
     */
    default Duration ttl(K key) {
        return null;
    }
}
