package org.zero.common.core.support.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/11/17
 */
public class PartitionedCache<K, V> extends MultiCache<K, V> {
	protected PartitionedCache(Collection<Cache<K, V>> caches) {
		super(caches);
	}

	@Override
	public void set(K key, V value) {
		Cache<K, V> selectedCache = this.selectCache(key);
		selectedCache.set(key, value);
	}

	@Override
	public V put(K key, V value) {
		Cache<K, V> selectedCache = this.selectCache(key);
		return selectedCache.put(key, value);
	}

	@Override
	public V get(K key) {
		Cache<K, V> selectedCache = this.selectCache(key);
		return selectedCache.get(key);
	}

	@Override
	public V putIfAbsent(K key, V value) {
		Cache<K, V> selectedCache = this.selectCache(key);
		return selectedCache.putIfAbsent(key, value);
	}

	@Override
	public V compute(K key, BiFunction<? super K, ? super V, ? extends V> reMapper) {
		Cache<K, V> selectedCache = this.selectCache(key);
		return selectedCache.compute(key, reMapper);
	}

	@Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mapper) {
		Cache<K, V> selectedCache = this.selectCache(key);
		return selectedCache.computeIfAbsent(key, mapper);
	}

	@Override
	public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> reMapper) {
		Cache<K, V> selectedCache = this.selectCache(key);
		return selectedCache.computeIfPresent(key, reMapper);
	}

	@Override
	public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> reMapper) {
		Cache<K, V> selectedCache = this.selectCache(key);
		return selectedCache.merge(key, value, reMapper);
	}

	@Override
	public boolean existKey(K key) {
		Cache<K, V> selectedCache = this.selectCache(key);
		return selectedCache.existKey(key);
	}

	@Override
	public boolean exist(K key, V value) {
		Cache<K, V> selectedCache = this.selectCache(key);
		return selectedCache.exist(key, value);
	}

	@Override
	public void remove(K key) {
		Cache<K, V> selectedCache = this.selectCache(key);
		selectedCache.remove(key);
	}

	protected Cache<K, V> selectCache(K key) {
		Collection<Cache<K, V>> selectedCaches = this.selectCaches(key);
		if (selectedCaches.isEmpty()) {
			throw new IllegalArgumentException("No cache found for key: " + key);
		}
		return selectedCaches.iterator().next();
	}

	@Override
	protected final Collection<Cache<K, V>> selectCaches(K key) {
		if (caches.isEmpty()) {
			return Collections.emptyList();
		}
		List<Cache<K, V>> list;
		if (caches instanceof List) {
			list = (List<Cache<K, V>>) this.caches;
		} else {
			list = new ArrayList<>(this.caches);
		}
		int hash = Objects.hashCode(key);
		int index = Math.abs(hash % list.size());
		Cache<K, V> cache = list.get(index);
		return Collections.singletonList(cache);
	}

	@SafeVarargs
	public static <K, V> PartitionedCache<K, V> of(Cache<K, V>... caches) {
		return of(new ArrayList<>(Arrays.asList(caches)));
	}

	public static <K, V> PartitionedCache<K, V> of(Collection<Cache<K, V>> caches) {
		return new PartitionedCache<>(caches);
	}
}
