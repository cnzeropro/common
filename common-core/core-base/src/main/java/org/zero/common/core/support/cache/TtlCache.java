package org.zero.common.core.support.cache;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
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
		return this.setTtl(key, toDuration(timeout, unit));
	}

	default void set(K key, V value, Duration timeout) {
		this.set(key, value);
		try {
			this.setTtl(key, timeout);
		} catch (Exception e) {
			this.remove(key);
			throw e;
		}
	}

	default void set(K key, V value, long timeout) {
		this.set(key, value, timeout, TimeUnit.MILLISECONDS);
	}

	default void set(K key, V value, long timeout, TimeUnit unit) {
		this.set(key, value, toDuration(timeout, unit));
	}

	default V put(K key, V value, Duration timeout) {
		V oldValue = this.put(key, value);
		try {
			this.setTtl(key, timeout);
		} catch (Exception e) {
			this.remove(key);
			throw e;
		}
		return oldValue;
	}

	default V put(K key, V value, long timeout) {
		return this.put(key, value, timeout, TimeUnit.MILLISECONDS);
	}

	default V put(K key, V value, long timeout, TimeUnit unit) {
		return this.put(key, value, toDuration(timeout, unit));
	}

	static Duration toDuration(long timeout, TimeUnit timeUnit) {
		switch (timeUnit) {
			case NANOSECONDS:
				return Duration.of(timeout, ChronoUnit.NANOS);
			case MICROSECONDS:
				return Duration.of(timeout, ChronoUnit.MICROS);
			case MILLISECONDS:
				return Duration.of(timeout, ChronoUnit.MILLIS);
			case SECONDS:
				return Duration.of(timeout, ChronoUnit.SECONDS);
			case MINUTES:
				return Duration.of(timeout, ChronoUnit.MINUTES);
			case HOURS:
				return Duration.of(timeout, ChronoUnit.HOURS);
			case DAYS:
				return Duration.of(timeout, ChronoUnit.DAYS);
			default:
				throw new AssertionError("Unknown TimeUnit: " + timeUnit);
		}
	}

	default V putIfAbsent(K key, V value, Duration timeout) {
		V oldValue = this.get(key);
		if (Objects.isNull(oldValue)) {
			oldValue = this.put(key, value, timeout);
		}
		return oldValue;
	}

	default V computeIfAbsent(K key, Function<? super K, ? extends V> mapper, Duration timeout) {
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

	default V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> reMapper, Duration timeout) {
		V oldValue;
		if ((oldValue = this.get(key)) != null) {
			V newValue = reMapper.apply(key, oldValue);
			if (Objects.nonNull(newValue)) {
				this.put(key, newValue, timeout);
				return newValue;
			}
			this.remove(key);
			return null;
		}
		return null;
	}

	default V compute(K key, BiFunction<? super K, ? super V, ? extends V> reMapper, Duration timeout) {
		V oldValue = this.get(key);
		V newValue = reMapper.apply(key, oldValue);
		if (Objects.isNull(newValue)) {
			if (Objects.nonNull(oldValue) || this.existKey(key)) {
				this.remove(key);
			}
			return null;
		}
		this.put(key, newValue, timeout);
		return newValue;
	}

	default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> reMapper, Duration timeout) {
		V oldValue = this.get(key);
		V newValue = Objects.isNull(oldValue) ? value : reMapper.apply(oldValue, value);
		if (Objects.isNull(newValue)) {
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
		this.putAll(cache.asMap(), timeout);
	}
}
