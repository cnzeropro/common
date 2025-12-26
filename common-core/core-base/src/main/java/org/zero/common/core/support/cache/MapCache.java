package org.zero.common.core.support.cache;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;
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
	protected final Map<K, V> map;

	@Override
	public void set(K key, V value) {
		map.put(key, value);
	}

	@Override
	public V put(K key, V value) {
		return map.put(key, value);
	}

	@Override
	public V putIfAbsent(K key, V value) {
		return map.putIfAbsent(key, value);
	}

	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> reMapper) {
		return map.compute(key, reMapper);
	}

	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mapper) {
		return map.computeIfAbsent(key, mapper);
	}

	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> reMapper) {
		return map.computeIfPresent(key, reMapper);
	}

	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> reMapper) {
		return map.merge(key, value, reMapper);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		this.map.putAll(map);
	}

	@Override
	public Map<K, V> asMap() {
		return Collections.unmodifiableMap(map);
	}

	@Override
	public V get(K key) {
		return map.get(key);
	}

	@Override
	public V getOrDefault(K key, V defaultValue) {
		return map.getOrDefault(key, defaultValue);
	}

	@Override
	public void remove(K key) {
		map.remove(key);
	}

	@Override
	public Collection<K> keys() {
		return map.keySet();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		map.forEach(action);
	}

	@Override
	public void removeAll() {
		map.clear();
	}

	@Override
	public boolean existKey(K key) {
		return map.containsKey(key);
	}

	@Override
	public boolean existValue(V value) {
		return map.containsValue(value);
	}

	@Override
	public Number size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean nonEmpty() {
		return !this.isEmpty();
	}


	public static <K, V> MapCache<K, V> of(Supplier<Map<K, V>> supplier) {
		return of(supplier.get());
	}
}
