package org.zero.common.core.support.cache;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/21
 */
public interface TtlCache<K, V> extends Cache<K, V> {
    /**
     * 获取过期时间
     *
     * @param key 缓存键
     * @return 过期时间，如果永不过期则返回 null
     */
    default Duration getTtl(K key) {
        return null;
    }

    default Duration setTtl(K key, Duration timeout) {
        return null;
    }

    default Duration setTtl(K key, long timeout, TimeUnit unit) {
        return this.setTtl(key, Duration.ofNanos(unit.toNanos(timeout)));
    }

    default void put(K key, V value, Duration timeout) {
        this.put(key, value);
        this.setTtl(key, timeout);
    }

    default void put(K key, V value, long timeout) {
        this.put(key, value, timeout, TimeUnit.MILLISECONDS);
    }

    default void put(K key, V value, long timeout, TimeUnit unit) {
        this.put(key, value, Duration.ofNanos(unit.toNanos(timeout)));
    }

    default V putIfAbsent(K key, V value, Duration timeout) {
        V v = this.get(key);
        if (v == null) {
            this.put(key, value, timeout);
            v = value;
        }
        return v;
    }

    default V mapAndPutIfAbsent(K key, Function<? super K, ? extends V> mapper, Duration timeout) {
        V v;
        if ((v = this.get(key)) == null) {
            V newValue;
            if ((newValue = mapper.apply(key)) != null) {
                this.put(key, newValue, timeout);
                return newValue;
            }
        }
        return v;
    }

    default V mapAndPutIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> reMapper, Duration timeout) {
        V oldValue;
        if ((oldValue = this.get(key)) != null) {
            V newValue = reMapper.apply(key, oldValue);
            if (newValue != null) {
                this.put(key, newValue, timeout);
                return newValue;
            }
            remove(key);
            return null;
        }
        return null;
    }

    default V mapAndPut(K key, BiFunction<? super K, ? super V, ? extends V> reMapper, Duration timeout) {
        V oldValue = this.get(key);
        V newValue = reMapper.apply(key, oldValue);
        if (newValue == null) {
            if (oldValue != null || exists(key)) {
                remove(key);
            }
            return null;
        }
        this.put(key, newValue, timeout);
        return newValue;
    }

    default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> reMapper, Duration timeout) {
        V oldValue = this.get(key);
        V newValue = (oldValue == null) ? value : reMapper.apply(oldValue, value);
        if (newValue == null) {
            this.remove(key);
        } else {
            this.put(key, newValue, timeout);
        }
        return newValue;
    }

    default void putAll(Map<? extends K, ? extends V> map, Duration timeout) {
        map.forEach((k, v) -> this.put(k, v, timeout));
    }

    default void putAll(Cache<? extends K, ? extends V> cache, Duration timeout) {
        this.putAll(cache.getAll(), timeout);
    }
}
