package org.zero.common.core.support.cache;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/7
 */
public interface Cache<K, V> {
	/* ****************************************************************** put ****************************************************************** */
	void set(K key, V value);

	default V put(K key, V value) {
		V oldValue = this.get(key);
		this.set(key, value);
		return oldValue;
	}

	default V putIfAbsent(K key, V value) {
		V oldValue = this.get(key);
		if (Objects.isNull(oldValue)) {
			oldValue = this.put(key, value);
		}
		return oldValue;
	}

	default V computeIfAbsent(K key, Function<? super K, ? extends V> mapper) {
		V oldValue;
		if ((oldValue = this.get(key)) == null) {
			V newValue;
			if ((newValue = mapper.apply(key)) != null) {
				this.put(key, newValue);
				return newValue;
			}
		}
		return oldValue;
	}

	default V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> reMapper) {
		V oldValue;
		if ((oldValue = this.get(key)) != null) {
			V newValue = reMapper.apply(key, oldValue);
			if (Objects.nonNull(newValue)) {
				this.put(key, newValue);
				return newValue;
			}
			this.remove(key);
			return null;
		}
		return null;
	}

	default V compute(K key, BiFunction<? super K, ? super V, ? extends V> reMapper) {
		V oldValue = this.get(key);
		V newValue = reMapper.apply(key, oldValue);
		if (Objects.isNull(newValue)) {
			if (Objects.nonNull(oldValue) || this.existKey(key)) {
				this.remove(key);
			}
			return null;
		}
		this.put(key, newValue);
		return newValue;
	}

	default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> reMapper) {
		V oldValue = this.get(key);
		V newValue = Objects.isNull(oldValue) ? value : reMapper.apply(oldValue, value);
		if (Objects.isNull(newValue)) {
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
		this.putAll(cache.asMap());
	}


	/* ****************************************************************** get ****************************************************************** */
	Map<K, V> asMap();

	default V get(K key) {
		return this.asMap().get(key);
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

	default Optional<V> getOpt(K key) {
		return Optional.ofNullable(this.get(key));
	}

	default V getOrDefault(K key, V defaultValue) {
		return this.getOpt(key).orElse(defaultValue);
	}

	default V getOrSupply(K key, Supplier<? extends V> defaultValueSupplier) {
		return this.getOpt(key).orElseGet(defaultValueSupplier);
	}

	default <X extends Throwable> V getOrThrow(K key, Supplier<? extends X> exceptionSupplier) throws X {
		return this.getOpt(key).orElseThrow(exceptionSupplier);
	}

	default <R> R getAndConvert(K key, Function<? super V, ? extends R> converter) {
		return this.getOpt(key).map(converter).orElse(null);
	}

	default V getAndFilter(K key, Predicate<? super V> filter) {
		return this.getOpt(key).filter(filter).orElse(null);
	}

	/* ****************************************************************** remove ****************************************************************** */

	void remove(K key);

	default void remove(K... keys) {
		this.remove(Arrays.asList(keys));
	}

	default void remove(Collection<K> keys) {
		keys.forEach(this::remove);
	}

	default void removeAll() {
		this.remove(this.keys());
	}

	/* ****************************************************************** other ****************************************************************** */
	default V replace(K key, V value) {
		V oldValue;
		if (((oldValue = this.get(key)) != null) || this.existKey(key)) {
			oldValue = this.put(key, value);
		}
		return oldValue;
	}

	default boolean replace(K key, V oldValue, V newValue) {
		Object value = this.get(key);
		if (!Objects.equals(value, oldValue) ||
			(Objects.isNull(value) && !this.existKey(key))) {
			return false;
		}
		this.put(key, newValue);
		return true;
	}

	default void replaceAll(BiFunction<? super K, ? super V, ? extends V> reMapper) {
		for (K key : this.keys()) {
			V oldValue = this.get(key);
			V newValue = reMapper.apply(key, oldValue);
			if (Objects.isNull(newValue)) {
				this.remove(key);
			} else {
				this.set(key, newValue);
			}
		}
	}

	default boolean hasValue(K key) {
		return this.getOpt(key).isPresent();
	}

	default Collection<K> keys() {
		return this.asMap().keySet();
	}

	default Collection<V> values() {
		return this.asMap().values();
	}

	default boolean exist(K key, V value) {
		return this.hasValue(key) && Objects.equals(this.get(key), value);
	}

	default boolean existKey(K key) {
		return this.keys().contains(key);
	}

	default boolean existValue(V value) {
		return this.values().contains(value);
	}

	default Number size() {
		return this.asMap().size();
	}

	default boolean isEmpty() {
		return !this.nonEmpty();
	}

	default boolean nonEmpty() {
		Number size = this.size();
		if (Objects.isNull(size)) {
			return false;
		}
		if (size instanceof Long || size instanceof Integer || size instanceof Short || size instanceof Byte) {
			return size.longValue() > 0;
		}
		BigInteger bigInteger = size instanceof BigInteger ? (BigInteger) size : new BigInteger(size.toString());
		return bigInteger.compareTo(BigInteger.ZERO) > 0;
	}

	default void forEach(BiConsumer<? super K, ? super V> action) {
		this.asMap().forEach(action);
	}
}
