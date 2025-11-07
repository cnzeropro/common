package org.zero.common.core.support.cache;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/7
 */
public interface Cache<K, V> {
	/* ****************************************************************** put ****************************************************************** */

	void put(K key, V value);

	default V putIfAbsent(K key, V value) {
		V v = this.get(key);
		if (v == null) {
			this.put(key, value);
			v = value;
		}
		return v;
	}

	default V mapAndPutIfAbsent(K key, Function<? super K, ? extends V> mapper) {
		V v;
		if ((v = this.get(key)) == null) {
			V newValue;
			if ((newValue = mapper.apply(key)) != null) {
				this.put(key, newValue);
				return newValue;
			}
		}
		return v;
	}

	default V mapAndPutIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> reMapper) {
		V oldValue;
		if ((oldValue = this.get(key)) != null) {
			V newValue = reMapper.apply(key, oldValue);
			if (newValue != null) {
				this.put(key, newValue);
				return newValue;
			}
			remove(key);
			return null;
		}
		return null;
	}

	default V mapAndPut(K key, BiFunction<? super K, ? super V, ? extends V> reMapper) {
		V oldValue = this.get(key);
		V newValue = reMapper.apply(key, oldValue);
		if (newValue == null) {
			if (oldValue != null || exists(key)) {
				remove(key);
			}
			return null;
		}
		this.put(key, newValue);
		return newValue;
	}

	default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> reMapper) {
		V oldValue = this.get(key);
		V newValue = (oldValue == null) ? value : reMapper.apply(oldValue, value);
		if (newValue == null) {
			this.remove(key);
		} else {
			this.put(key, newValue);
		}
		return newValue;
	}

	default void putAll(Map<? extends K, ? extends V> map) {
		map.forEach(this::put);
	}

	default void putAll(Cache<? extends K, ? extends V> cache) {
		this.putAll(cache.getAll());
	}

	/* ****************************************************************** get ****************************************************************** */

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

	default V getOrSupply(K key, Supplier<? extends V> defaultValueSupplier) {
		return this.getOpt(key).orElseGet(defaultValueSupplier);
	}

	default <X extends Throwable> V getOrThrow(K key, Supplier<? extends X> exceptionSupplier) throws X {
		return this.getOpt(key).orElseThrow(exceptionSupplier);
	}

	default <R> R getAndConvert(K key, Function<? super V, ? extends R> mapper) {
		return this.getOpt(key).map(mapper).orElse(null);
	}

	default boolean exists(K key) {
		return this.getOpt(key).isPresent();
	}

	default Number size() {
		return this.getAll().size();
	}

	default boolean isEmpty() {
		return Objects.equals(this.size(), 0);
	}

	/* ****************************************************************** remove ****************************************************************** */

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
}
